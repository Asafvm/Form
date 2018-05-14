package il.co.diamed.com.form;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import il.co.diamed.com.form.devices.CentrifugeActivity;
import il.co.diamed.com.form.devices.Diacent12Activity;
import il.co.diamed.com.form.devices.DiacentCWActivity;
import il.co.diamed.com.form.devices.DiacentUltraCWActivity;
import il.co.diamed.com.form.devices.DoconActivity;
import il.co.diamed.com.form.devices.GelstationActivity;
import il.co.diamed.com.form.devices.GeneralUseActivity;
import il.co.diamed.com.form.devices.HC10Activity;
import il.co.diamed.com.form.devices.IH1000Activity;
import il.co.diamed.com.form.devices.IH500Activity;
import il.co.diamed.com.form.devices.IncubatorActivity;
import il.co.diamed.com.form.devices.PlasmaThawerActivity;
import il.co.diamed.com.form.res.MultiLayoutActivity;
import il.co.diamed.com.form.res.FileBrowserActivity;
import il.co.diamed.com.form.res.providers.SettingsActivity;

public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = "DeviceActivity";
    private DrawerLayout mDrawerLayout;
private Bundle calibrationDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        //ClassApplication application = (ClassApplication) getApplication();
        //application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");



        calibrationDevices = new Bundle();
        calibrationDevices.putString("thermometer",thermometer);
        calibrationDevices.putString("barometer",barometer);
        calibrationDevices.putString("speedometer",speedometer);
        calibrationDevices.putString("timer",timer);
        calibrationDevices.putString("techName",techname);
        calibrationDevices.putString("signature",signature);
        /* Tabs */
        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.pager);
        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


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
                        Log.e(TAG, "itemid: "+menuItem.getItemId()+" - itemname: "+menuItem.getTitle());
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent = null;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_settings: {
                                intent = new Intent(getBaseContext(), SettingsActivity.class);
                                break;
                            }
                            case R.id.nav_files: {
                                intent = new Intent(getBaseContext(), FileBrowserActivity.class);
                                intent.putExtra("path", Environment.getExternalStorageDirectory() + "/Documents/MediForms/"); //Environment.getExternalStorageDirectory() + "/Documents/
                                break;
                            }
                            default:

                        }
                        if(intent!=null)
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
                        ClassApplication application = (ClassApplication)getApplication();
                        String name = sp.getString("techName", "");
                        TextView et = findViewById(R.id.nav_header);
                        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                            et.setText(String.format("%s%s%s", getString(R.string.helloHeader), getString(R.string.space), name));
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
        Intent intent;
        switch (view.getId()) {
            case R.id.idInc:
                intent = new Intent(getBaseContext(), IncubatorActivity.class);
                break;
            case R.id.idCent:
                intent = new Intent(getBaseContext(), CentrifugeActivity.class);
                break;
            case R.id.idDiacent12:
                intent = new Intent(getBaseContext(), Diacent12Activity.class);
                break;
            case R.id.idDiacentCW:
                intent = new Intent(getBaseContext(), DiacentCWActivity.class);
                break;
            case R.id.ultraCW:
                intent = new Intent(getBaseContext(), DiacentUltraCWActivity.class);
                break;
            case R.id.plasma:
                intent = new Intent(getBaseContext(), PlasmaThawerActivity.class);
                break;
            case R.id.idGelstation:
                intent = new Intent(getBaseContext(), GelstationActivity.class);
                break;
            case R.id.ih500:
                intent = new Intent(getBaseContext(), IH500Activity.class);
                break;
            case R.id.ih1000:
                intent = new Intent(getBaseContext(), IH1000Activity.class);
                break;
            case R.id.ib10:
                intent = new Intent(getBaseContext(), GeneralUseActivity.class);
                intent.putExtra("type",R.id.ib10);
                break;
            case R.id.pt10:
                intent = new Intent(getBaseContext(), GeneralUseActivity.class);
                intent.putExtra("type",R.id.pt10);
                break;
            case R.id.docureader:
                intent = new Intent(getBaseContext(), GeneralUseActivity.class);
                intent.putExtra("type",R.id.dr2);
                break;
            case R.id.edan:
                intent = new Intent(getBaseContext(), GeneralUseActivity.class);
                intent.putExtra("type",R.id.edan);
                break;
            case R.id.hc10:
                intent = new Intent(getBaseContext(), HC10Activity.class);
                break;
            case R.id.docon:
                intent = new Intent(getBaseContext(), DoconActivity.class);
                break;


                //////////////////////////////
            case R.id.test:
                //Crashlytics.getInstance().crash(); // Force a crash

                intent = new Intent(getBaseContext(), MultiLayoutActivity.class);
                break;
            default:
                intent = null;
                break;

        }
        if (intent != null) {
            intent.putExtra("cal", calibrationDevices);
            startActivityForResult(intent, 1);
        }else
            Toast.makeText(getApplicationContext(), R.string.noDevice, Toast.LENGTH_SHORT).show();





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode==RESULT_OK){
                Toast.makeText(getBaseContext(),R.string.pdfSuccess,Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getBaseContext(),R.string.pdfFailed,Toast.LENGTH_SHORT).show();

            }

        }


    }
}
