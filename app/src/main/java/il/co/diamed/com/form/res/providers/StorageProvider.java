package il.co.diamed.com.form.res.providers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
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

public class StorageProvider {
    private static final String STORAGE_BUCKET = "gs://mediforms-04052018.appspot.com";
    private static final String TAG = "StorageProvider";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferance;

    public StorageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_BUCKET);

    }

    public void uploadFile(File file, String parent) {
        String filePath = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(parent));
        mStorageReferance = mFirebaseStorage.getReference(filePath);
        Uri fileURI = Uri.fromFile(file);
        UploadTask uploadTask = mStorageReferance.putFile(fileURI);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.e(TAG, "File Uploaded! getting metadata");
            mStorageReferance.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Files")
                            .child(filePath.substring(0,filePath.lastIndexOf(".")));
                    databaseReference.child("name").setValue(storageMetadata.getName());
                    databaseReference.child("file_location").setValue(storageMetadata.getPath());
                    //databaseReference.child("download_url").setValue(storageMetadata.getReference());
                    databaseReference.child("type").setValue(storageMetadata.getContentType());

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

    public void getDir(String path) {
        mStorageReferance = mFirebaseStorage.getReference();
        //StorageReference storageRef =  mStorageReferance.child("MediForms/");
        mStorageReferance.child("/MediForms/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e(TAG, "Got URI back: "+uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error: "+exception.getMessage());
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
