package il.co.diamed.com.form.menu;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.DevicesFragment;
import il.co.diamed.com.form.filebrowser.FileBrowserFragment;
import il.co.diamed.com.form.inventory.InventoryFragment;
import il.co.diamed.com.form.inventory.InventoryItem;
import il.co.diamed.com.form.inventory.InventoryViewerAdapter;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class MainMenuAcitivity extends AppCompatActivity  {

    private static final String TAG = "MainMenu";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    private DevicesFragment mDevicesFragment;
    private InventoryFragment mInventoryFragment;
    FileBrowserFragment mFileBrowserFragment;
    ClassApplication application;
    DatabaseProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        application = (ClassApplication) getApplication();
        provider = application.getDatabaseProvider(this);
        Slide slide = new Slide();
        slide.setDuration(500);
        slide.setSlideEdge(Gravity.END);
        getWindow().setEnterTransition(slide);
        setContentView(R.layout.activity_main_menu);
        setContentView(R.layout.activity_main_menu);

        mFragmentManager = getSupportFragmentManager();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");

        Bundle calibrationDevices = new Bundle();
        calibrationDevices.putString("thermometer", thermometer);
        calibrationDevices.putString("barometer", barometer);
        calibrationDevices.putString("speedometer", speedometer);
        calibrationDevices.putString("timer", timer);
        calibrationDevices.putString("techName", techname);
        calibrationDevices.putString("signature", signature);

        updateUser();


        // in stock summery
        setInventorySummery();

        // Drawer Code
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //set icon
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);

                    Log.e(TAG, "itemid: " + menuItem.getItemId() + " - itemname: " + menuItem.getTitle());
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    switch (menuItem.getItemId()) {
                        case R.id.nav_forms: {
                            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

                            if (mDevicesFragment == null) {
                                mDevicesFragment = new DevicesFragment();
                            }
                            Fade fade = new Fade();
                            fade.setDuration(400);
                            mDevicesFragment.setEnterTransition(fade);
                            mFragmentTransaction.addToBackStack(null);
                            mFragmentTransaction.replace(R.id.module_container, mDevicesFragment).commit();


                            break;
                        }
                        case R.id.nav_settings: {
                            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.nav_files: {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                launchFileBrowser();
                            } else {
                                // Permission is not granted
                                getPermission();
                            }

                            break;
                        }
                        case R.id.nav_stock: {
                            //Toast.makeText(getApplicationContext(), getText(R.string.soon), Toast.LENGTH_SHORT).show();
                            showInventory();
                            break;
                        }
                        default:

                    }

                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    return true;


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
        // Drawer Code END

    }

    private void showInventory() {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mInventoryFragment == null) {
            mInventoryFragment = new InventoryFragment();
        }
        Slide slide = new Slide();
        slide.setDuration(500);
        mInventoryFragment.setEnterTransition(slide);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.module_container, mInventoryFragment).commit();
    }

    private void setInventorySummery() {


        RecyclerView recyclerView = findViewById(R.id.recycler_missing_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<InventoryItem> missingInv = provider.getMissingInv();
        if(missingInv!=null) {
            try {
                unregisterReceiver(databaseReceiver);
            }catch (Exception ignored){}
            RecyclerView.Adapter adapter = new InventoryViewerAdapter(missingInv, this);
            recyclerView.setAdapter(adapter);

            recyclerView.setOnClickListener(v ->
                    showInventory());
        }else{
            registerReceiver(databaseReceiver,
                    new IntentFilter(DatabaseProvider.BROADCAST_DB_READY));
        }
    }

    private BroadcastReceiver databaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Database alert!");
            setInventorySummery();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();


    }

    private void updateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView title = findViewById(R.id.titleUser);
        if (user != null) {
            title.setText(user.getEmail());
            title.setBackgroundColor(Color.parseColor("#3C9824"));
        } else {
            title.setText(getString(R.string.noUser));
            title.setBackgroundColor(Color.parseColor("#AF2525"));
        }

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

                    launchFileBrowser();

                    // permission denied, boo!
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noReadPermission), Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void launchFileBrowser() {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mFileBrowserFragment == null) {
            mFileBrowserFragment = new FileBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", Environment.getExternalStorageDirectory() + "/Documents/MediForms/");
            mFileBrowserFragment.setArguments(bundle);
        }
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        slide.setDuration(500);
        mFileBrowserFragment.setEnterTransition(slide);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.module_container, mFileBrowserFragment).commit();

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


    public void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
    }

    @Override
    public void onBackPressed() {

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();

            if (mDevicesFragment != null) {
                mFragmentManager.beginTransaction().remove(mDevicesFragment).commit();
            }
            setInventorySummery();

        } else {
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
