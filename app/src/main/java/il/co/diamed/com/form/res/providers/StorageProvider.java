package il.co.diamed.com.form.res.providers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
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

    public StorageProvider(){
        mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_BUCKET);

    }

    public void uploadFile(String dest, String destArray){
        mStorageReferance  = mFirebaseStorage.getReference(PARENT+destArray);
        Uri file = Uri.fromFile(new File(dest));
        UploadTask uploadTask = mStorageReferance.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed Uploading");
            }
        });
    }
}
