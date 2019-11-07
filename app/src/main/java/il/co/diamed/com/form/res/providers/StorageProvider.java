package il.co.diamed.com.form.res.providers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
import java.util.Stack;

import il.co.diamed.com.form.R;

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


    public void downloadFile(String savePath, String filename) {

        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(savePath.substring("Files/".length())).child(filename);
        File localFile = null;


        File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/MediForms/" + savePath.substring("Files/Mediforms/".length()) + File.separator);
        boolean b = dir.mkdirs();
        //localFile = File.createTempFile(filename.substring(0, filename.length() - 4), filename.substring(filename.length() - 4),dir);
        //localFile = new File(dir,filename.substring(0, filename.length() - 4)+filename.substring(filename.length() - 4));
        localFile = new File(dir,filename);
        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //download next if any
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

                /* DISPLAY PDF
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY);
                Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, localFile);
                target.setDataAndType(uri, "application/pdf");
                try {
                    startActivity(target);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(context, "Failed to download file - "+exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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
    }

}
