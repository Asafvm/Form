package il.co.diamed.com.form;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import il.co.diamed.com.form.devices.CentrifugeActivity;
import il.co.diamed.com.form.devices.DiacentActivity;
import il.co.diamed.com.form.devices.GelstationActivity;
import il.co.diamed.com.form.devices.GeneralUseActivity;
import il.co.diamed.com.form.devices.IncubatorActivity;
import il.co.diamed.com.form.devices.PlasmaThawerActivity;
import il.co.diamed.com.form.res.RecyclerActivity;
import il.co.diamed.com.form.res.SettingsActivity;

public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = "DeviceActivity";
    private DrawerLayout mDrawerLayout;
private Bundle calibrationDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);


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
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Log.e(TAG, "Drawer");
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });


        //Not mandatory
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        //SharedPreferences sp = getPreferences(MODE_PRIVATE);
                        String name = sp.getString("techName", "");
                        TextView et = findViewById(R.id.nav_header);
                        if (name != null || name != "") {
                            et.setText(getString(R.string.helloHeader) + " " + name);
                        } else {
                            et.setText(getString(R.string.navbar_header) + name);
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
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
            case R.id.idDiacent:
                intent = new Intent(getBaseContext(), DiacentActivity.class);
                break;
            case R.id.plasma:
                intent = new Intent(getBaseContext(), PlasmaThawerActivity.class);
                break;
            case R.id.idGelstation:
                intent = new Intent(getBaseContext(), GelstationActivity.class);
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

                //////////////////////////////
            case R.id.test:
                intent = new Intent(getBaseContext(), RecyclerActivity.class);
                break;
            default:
                intent = null;
                break;

        }
        if (intent != null) {
            intent.putExtra("cal", calibrationDevices);
            startActivityForResult(intent, 1);
        }else
            Toast.makeText(getApplicationContext(), R.string.noDevice, Toast.LENGTH_SHORT);

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
