package il.co.diamed.com.form.res.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import il.co.diamed.com.form.R;

public class PermissionManager extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    public static final int MY_LOCATION_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    private static final PermissionManager ourInstance = new PermissionManager();

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
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                    //launchFileBrowser();

                    // permission denied, boo!
                } else {
                    //call broadcast function to send message

                }

                break;
            }

            case MY_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }

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
