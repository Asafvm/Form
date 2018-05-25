package il.co.diamed.com.form.menu;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.res.FileBrowserFragment;
import il.co.diamed.com.form.R;

public class MainMenuAcitivity extends AppCompatActivity implements DevicesFragment.OnFragmentInteractionListener,
        FileBrowserFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainMenu";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    private DrawerLayout mDrawerLayout;
    private Bundle calibrationDevices;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private DevicesFragment mDevicesFragment;
    FileBrowserFragment mFileBrowserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //ClassApplication application = (ClassApplication) getApplication();
        //application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));
        mFragmentManager = getSupportFragmentManager();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");

        calibrationDevices = new Bundle();
        calibrationDevices.putString("thermometer", thermometer);
        calibrationDevices.putString("barometer", barometer);
        calibrationDevices.putString("speedometer", speedometer);
        calibrationDevices.putString("timer", timer);
        calibrationDevices.putString("techName", techname);
        calibrationDevices.putString("signature", signature);

        updateUser();

        /** Drawer Code **/
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //set icon
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Log.e(TAG, "itemid: " + menuItem.getItemId() + " - itemname: " + menuItem.getTitle());
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent = null;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_forms: {
                                mFragmentTransaction = mFragmentManager.beginTransaction();
                                if (mDevicesFragment == null) {
                                    mDevicesFragment = new DevicesFragment();
                                }
                                //mFragmentManager.beginTransaction().detach(mDevicesFragment).attach(mDevicesFragment).commit();
                                //mFragmentTransaction.setCustomAnimations(R.animator, R.animator.);
                                mFragmentTransaction.addToBackStack(null);
                                mFragmentTransaction.replace(R.id.module_container, mDevicesFragment).commit();


                                break;
                            }
                            case R.id.nav_settings: {
                                intent = new Intent(getBaseContext(), SettingsActivity.class);
                                break;
                            }
                            case R.id.nav_files: {
                                if (mFileBrowserFragment == null) {
                                    mFileBrowserFragment = new FileBrowserFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", Environment.getExternalStorageDirectory() + "/Documents/MediForms/");
                                    mFileBrowserFragment.setArguments(bundle);
                                }
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    mFragmentTransaction = mFragmentManager.beginTransaction();
                                    //ft.setCustomAnimations(R.animator, R.animator.fade_in);
                                    mFragmentTransaction.addToBackStack(null);
                                    mFragmentTransaction.replace(R.id.module_container, mFileBrowserFragment).commit();

                                } else {
                                    // Permission is not granted
                                    getPermission();
                                }

                                break;
                            }
                            case R.id.nav_stock: {
                                Toast.makeText(getApplicationContext(), getText(R.string.soon), Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default:

                        }
                        if (intent != null)
                            startActivity(intent);
                        return true;
                    }
                });


        //Not mandatory
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        String name = sp.getString("techName", "");
                        TextView et = findViewById(R.id.nav_header);
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            et.setText(String.format("%s %s", getString(R.string.helloHeader), name));
                        } else {
                            et.setText(String.format("%s%s", getString(R.string.navbar_header), name));
                        }
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        /** Drawer Code END**/

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();
    }

    private void updateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView title = findViewById(R.id.titleUser);
        if(user!=null){
            title.setText(user.getEmail());
            title.setBackgroundColor(Color.parseColor("#3C9824"));
        }else{
            title.setText(getString(R.string.noUser));
            title.setBackgroundColor(Color.parseColor("#AF2525"));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    //ft.setCustomAnimations(R.animator, R.animator.fade_in);
                    mFragmentTransaction.replace(R.id.module_container, mFileBrowserFragment).commit();
                    mFragmentTransaction.addToBackStack(null);

                    // permission denied, boo! Disable the
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noReadPermission), Toast.LENGTH_SHORT).show();

                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deviceSelect(View view) {
        mDevicesFragment.deviceSelect(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.module_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    @Override
    public void onBackPressed() {

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();

            if (mDevicesFragment != null) {
                mFragmentManager.beginTransaction().remove(mDevicesFragment).commit();

            }
        } else{
            finish();
        }
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "MainMenu got intent reciever");

        }
    };

}
