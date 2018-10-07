package il.co.diamed.com.form.res.providers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.menu.SettingsActivity;

public class StorageProvider {
    private static final String STORAGE_BUCKET = "gs://mediforms-04052018.appspot.com";
    private static final String TAG = "StorageProvider";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferance;
    private Stack<File> fileStack = new Stack<>();
    private Stack<String> fileParentStack = new Stack<>();
    private int fileCount;
    private Context context;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private int fileUploaded;

    public StorageProvider(Context context) {
        this.context = context;
        mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_BUCKET);
        fileCount = 1;
        fileUploaded = 0;
        setupNotification();
    }

    private void setupNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "STORAGE_CHANNEL";
            String description = "NO DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("STORAGE_CHANNEL", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }

        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_upload_files);
        // Apply the layouts to the notification
        mBuilder = new NotificationCompat.Builder(context, "STORAGE_CHANNEL")
                .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                .setContentTitle("מעלה קבצים")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]{0L});


    }

    public void uploadFile(File file, String parent) {
        if (fileUploaded == 0) {
            mBuilder.setContentText("Preparing Files");
            if (notificationManager != null) {
                notificationManager.notify(0, mBuilder.build());
            }
        }
        if (mStorageReferance != null) {
            fileCount++;
            fileStack.push(file);
            fileParentStack.push(parent);
        } else {
            String filePath = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(parent));
            mStorageReferance = mFirebaseStorage.getReference(filePath);
            Uri fileURI = Uri.fromFile(file);
            UploadTask uploadTask = mStorageReferance.putFile(fileURI);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.e(TAG, "File Uploaded! getting metadata");
                fileUploaded++;
                mStorageReferance.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Files")
                                .child(filePath.substring(0, filePath.lastIndexOf(".")));
                        databaseReference.child("name").setValue(storageMetadata.getName());
                        databaseReference.child("file_location").setValue(storageMetadata.getPath());
                        //databaseReference.child("download_url").setValue(storageMetadata.getReference());
                        databaseReference.child("type").setValue(storageMetadata.getContentType());

                        //get next in stack
                        mStorageReferance = null;
                        if (notificationManager != null) {
                            if (!fileStack.empty()) {
                                mBuilder.setContentTitle("Uploading: " + fileUploaded + " / " + fileCount + " Files");
                                mBuilder.setContentText(file.getName());
                                notificationManager.notify(0, mBuilder.build());
                                uploadFile(fileStack.pop(), fileParentStack.pop());

                            } else {
                                mBuilder.setContentTitle("Upload Complete!");
                                mBuilder.setContentText("Uploaded " + fileUploaded + " Files!");
                                mBuilder.setTimeoutAfter(5000);
                                notificationManager.notify(0, mBuilder.build());
                                fileCount = 0;
                                fileUploaded = 0;
                                //mBuilder.setTimeoutAfter(5000);
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed Uploading");
                }
            });
        }
    }

    private void notificationProgress() {
        //RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_upload_files);
        //notificationLayout.setTextViewText(R.id.uploadFile,file.getName());
        //notificationLayout.setTextViewText(R.id.uploadStatus,fileCount + " / " + fileStack.size());
        //mBuilder.setCustomContentView(notificationLayout);
//        mBuilder.setContentText(fileCount + " / " + fileStack.size());

    }

    public void getDir(String path) {
        mStorageReferance = mFirebaseStorage.getReference();
        //StorageReference storageRef =  mStorageReferance.child("MediForms/");
        mStorageReferance.child("/MediForms/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e(TAG, "Got URI back: " + uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error: " + exception.getMessage());
            }
        });
/*
        FileDownloadTask downloadTask = mStorageReferance.getFile(Uri.parse(path));
        downloadTask.onSuccessTask(new ).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*//*
        mStorageReferance.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/
    }

}
