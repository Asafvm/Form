package il.co.diamed.com.form.res.providers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageProvider {
    private static final String STORAGE_BUCKET = "gs://mediforms-04052018.appspot.com";
    private static final String TAG = "StorageProvider";
    private final String PARENT = "MediForms/";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferance;

    public StorageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_BUCKET);

    }

    public void uploadFile(String dest, String destArray) {
        mStorageReferance = mFirebaseStorage.getReference(PARENT + destArray);
        Uri file = Uri.fromFile(new File(dest));
        UploadTask uploadTask = mStorageReferance.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed Uploading");
            }
        });
    }

    public void getDir(String path) {
        mStorageReferance = mFirebaseStorage.getReference(path);
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
        });*/
        mStorageReferance.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

}
