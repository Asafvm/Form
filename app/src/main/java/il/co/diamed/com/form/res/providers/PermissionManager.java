package il.co.diamed.com.form.res.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import il.co.diamed.com.form.R;

public class PermissionManager extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    public static final int MY_LOCATION_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 2;
    private static final PermissionManager ourInstance = new PermissionManager();
    private static final String TAG = "Permission Manager: ";

    public static PermissionManager getInstance() {
        return ourInstance;
    }

    private PermissionManager() {


    }

    public boolean checkPermission(Context applicationContext, String permission) {
        return ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "Got answer");

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {

                case MY_PERMISSIONS_REQUEST_READ_EXTERNAL:
                    break;
                case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                    break;
                case MY_LOCATION_REQUEST_CODE:
                    break;
            }
        }
    }

    public void requestPermission(Activity activity, String[] manifestPermission, int permission) {
        ActivityCompat.requestPermissions(activity,
                manifestPermission,
                permission);
    }
}
