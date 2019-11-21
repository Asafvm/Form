package il.co.diamed.com.form.res.providers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.Device;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.data_objects.SubLocation;
import il.co.diamed.com.form.inventory.InventoryUser;
import il.co.diamed.com.form.inventory.Part;

public class DatabaseProviderService extends Service {
    public static final String BROADCAST_USER_DB_READY = "database_provider_users_data_ready";
    public static final String BROADCAST_DB_READY = "database_provider_data_ready";
    public static final String BROADCAST_LABDB_READY = "database_provider_lab_data_ready";
    public static final String BROADCAST_TARGET_DB_READY = "database_provider_target_data_ready";
    public static final String BROADCAST_LOCDB_READY = "database_provider_location_data_ready";
    public static final String BROADCAST_APPVER = "database_app_ver";
    private final String TAG = "Database";
    private static HashMap<String, Part> labDB;
    private static HashMap<String, Part> myDB;
    private static HashMap<String, Part> targetDB;
    private List<ArrayList<InventoryUser>> allUsers;
    private final String USER_DB = "Users/";
    private Context context;
    private boolean lab_busy = false;
    private boolean my_busy = false;
    private String DB = "Parts/";
    private boolean target_busy = false;
    private String last_target = "";
    private boolean loc_busy;
    private String LOCATION_DB = "Location";
    private static ArrayList<Location> locDB;
    private boolean waitingToUploadDeviceFlag = false;
    private String holderLoc = "";
    private String holderSubloc = "";
    private Device holderDevice = null;
    private static String appVer = "";


    public DatabaseProviderService(Context context) {
        this.context = context;

    }

    public String getAppVer() {
        if(appVer.equals("")) {
            FirebaseDatabase.getInstance().getReference().child("AppVer").addListenerForSingleValueEvent(verListener);
            return "";
        }else {
            return appVer;
        }
    }
    private ValueEventListener verListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Intent intent = new Intent(BROADCAST_APPVER);
            appVer = (String)dataSnapshot.getValue();
            intent.putExtra("AppVer",appVer);
            context.sendBroadcast(intent);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public List<Part> getLabInv() {
        if (labDB != null && !lab_busy) {
            List<Part> labDBsorted = new ArrayList<>(labDB.values());
            Collections.sort(labDBsorted);
            return labDBsorted;
        } else {
            if (!lab_busy) {
                downloadLabDB();
            }
        }
        return null;
    }

    public List<ArrayList<InventoryUser>> getUsersInv() {
        if (allUsers != null) {
            return allUsers;
        } else {
            if (labDB != null) {
                orginizeUsersData();
            } else
                getLabInv();
        }
        return null;
    }

    public List<Part> getMyInv() {
        if (myDB != null && !my_busy) {
            return new ArrayList<>(myDB.values());
        } else {
            if (!my_busy) {
                downloadMyDB();
            }
        }
        return null;
    }

    public List<Part> getTargetInv(String target) {
        if (!last_target.equals(target)) {
            last_target = target;
            targetDB = null;
        }

        if (targetDB != null && !target_busy) {
            return new ArrayList<>(targetDB.values());
        } else {
            if (!target_busy) {
                downloadTargetDB(target);
            }
        }
        return null;
    }

    private void downloadMyDB() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            myDB = null;
            my_busy = true;
            FirebaseDatabase.getInstance().getReference()
                    .child(USER_DB).child(user.getUid()).child(DB).addValueEventListener(myListener);

        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

    private void downloadTargetDB(String target) {
        target_busy = true;
        myDB = null;
        my_busy = true;
        FirebaseDatabase.getInstance().getReference()
                .child(USER_DB).child(target).child(DB).addValueEventListener(targetListener);
    }


    private void downloadLabDB() {
        lab_busy = true;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(DB);
        mDatabase.addValueEventListener(labListener);

    }


    private FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)        //if user logged
            downloadMyDB();
        else
            myDB = null;

    };

    private ValueEventListener targetListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            targetDB = getInv(dataSnapshot);

            target_busy = false;
            Intent intent = new Intent(BROADCAST_TARGET_DB_READY);
            context.sendBroadcast(intent);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            my_busy = false;
        }
    };


    private ValueEventListener labListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            labDB = getInv(dataSnapshot);

            lab_busy = false;
            Intent intent = new Intent(BROADCAST_LABDB_READY);
            context.sendBroadcast(intent);
            refreshDatabase();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            my_busy = false;
        }
    };


    private ValueEventListener myListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            myDB = getInv(dataSnapshot);
            my_busy = false;
            refreshDatabase();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            my_busy = false;
        }
    };


    private void refreshDatabase() {
        if (labDB != null & myDB != null) {
            for (Part labItem : labDB.values()) {    //for every part in the lab
                if (myDB.containsKey(labItem.getId())) {        // if it exists already
                    myDB.get(labItem.getId()).setMinimum_car(labItem.getMinimum_car()); //update minimum (just in case)
                } else {
                    try {
                        if (Integer.valueOf(labItem.getMinimum_car()) > 0) { //check if it's mandatory part
                            labItem.setinStock("0");                       //update to 0 in stock
                            myDB.put(labItem.getId(), labItem);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, labItem.getDescription() + " has invalid minimum of: " + labItem.getMinimum_car());
                    }
                }
            }
            Intent intent = new Intent(BROADCAST_DB_READY);
            context.sendBroadcast(intent);
        } else {
            if (labDB == null && !lab_busy)
                downloadLabDB();
            if (myDB == null && !my_busy)
                downloadMyDB();
        }
    }


    private HashMap<String, Part> getInv(DataSnapshot dataSnapshot) {
        HashMap<String, Part> tempValues = new HashMap<>();
        if (dataSnapshot.getValue() == null) {//root
            Log.e(TAG, "Cannot browse root folder");
        } else {
            if (dataSnapshot.getValue() == null) {//root
                Log.e(TAG, "Database " + dataSnapshot.getKey() + " does not exists");
            } else {  //Parts
                for (DataSnapshot ds : dataSnapshot.getChildren()) { //Serial numbers
                    Part temp = ds.getValue(Part.class);
                    if (temp != null) {
                        temp.setId(ds.getKey());
                        tempValues.put(temp.getId(), temp);
                    } else
                        Log.e(TAG, "Invalid item recieved on getInv()");
                }
            }
        }
        return tempValues;
    }


    /**
     * @param currentList update remote personal database
     */
    public void uploadMyInv(List<Part> currentList) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(USER_DB).child(user.getUid()).child(DB);
            if (currentList != null) {
                for (Part item : currentList) {
                    if (item.getInStock().equals("0") && (item.getMinimum_car().equals("0") || item.getMinimum_car().equals("00")))
                        mDatabase.child(item.getId()).removeValue().addOnSuccessListener(aVoid -> Log.d(TAG, "Unused part " + item.getDescription() + " removed from list"));
                    else
                        mDatabase.child(item.getId()).setValue(item);
                }
                Log.d(TAG, "Finished updating inventory");

                downloadMyDB();
            }
        }
    }

    public List<Part> getMissingInv() {
        if (myDB != null) {
            List<Part> missingInv = new ArrayList<>();
            for (Part item : myDB.values()) {
                if (item.getInStock().compareTo(item.getMinimum_car()) <= 0)
                    missingInv.add(myDB.get(item.getId()));
            }
            return missingInv;
        } else if (!my_busy) {
            downloadMyDB();
        }
        return null;
    }


    public String getPartInfo(String serial) {
        for (Part item : labDB.values())
            if (item.getSerial().equals(serial))
                return item.getDescription();
        return "";
    }

    public boolean addMyDB(String serial, Integer inStock) {
        Part target = null;
        if (myDB != null) {
            for (Part myItem : myDB.values()) {    //search local
                if (myItem.getSerial().equals(serial)) {
                    target = myItem;
                }
            }
            if (target != null) {
                int current = Integer.valueOf(target.getInStock());
                target.setinStock(String.valueOf(inStock + current));
                myDB.put(target.getId(), target);
                uploadMyInv(new ArrayList<>(myDB.values()));
                return true;
            } else {
                for (Part item : labDB.values())   //serach remote
                    if (item.getSerial().equals(serial))
                        target = item;
                if (target != null) {
                    target.setinStock(String.valueOf(inStock));
                    myDB.put(target.getId(), target);
                    uploadMyInv(new ArrayList<>(myDB.values()));
                    return true;
                }
            }
            return false;
        } else {
            downloadMyDB();
            return false;
        }
    }

    private void orginizeUsersData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = mDatabase.child(USER_DB);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers = new ArrayList<>();
                ArrayList<Part> labDBsorted = new ArrayList<>(labDB.values());
                Collections.sort(labDBsorted);
                for (Part item : labDBsorted) {//labDB.values()){
                    //HashMap<String,String> holders = new HashMap<>();
                    ArrayList<InventoryUser> holders = new ArrayList<>();
                    for (DataSnapshot users : dataSnapshot.getChildren()) {    //gor every user, get data base1
                        String id = users.getKey();
                        String name = (String) users.child("Info").child("techName").getValue();
                        HashMap<String, String> stock = ((HashMap<String, String>) users.child("Parts").child(item.getId()).getValue());
                        String inStock = stock == null ? "0" : stock.get("inStock");
                        InventoryUser user = new InventoryUser(id, name, inStock);
                        holders.add(user);
                    }
                    allUsers.add(holders);
                }
                Intent intent = new Intent(BROADCAST_USER_DB_READY);
                context.sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void initialize() {
        getLabInv();
        getMyInv();
    }


    /***************************** My Database *************************************/


    public void uploadUserData(HashMap<String, String> userInfo) {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = mDatabase.child(USER_DB).child(user.getUid()).child("Info");
            for (String key : userInfo.keySet()) {
                databaseReference.child(key).setValue(userInfo.get(key));
            }
        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

    /***************************** LabDatabase *************************************/


    /***************************** Target User Database *************************************/


    /***************************** LOCATION *************************************/


    public ArrayList<Location> getLocDB() {
        if (locDB != null) {
            return locDB;
        } else if (!loc_busy) {
            downloadLocDB();
        }
        return null;

    }

    private void downloadLocDB() {
        loc_busy = true;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(LOCATION_DB);
        mDatabase.addValueEventListener(locListener);


    }

    private ValueEventListener locListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            ArrayList<Location> locations = new ArrayList<>();
            for (DataSnapshot locationChild : dataSnapshot.getChildren()) {
                locations.add(locationChild.getValue(Location.class));
            }

            locDB = locations;
            locDB.removeAll(Collections.singleton(null));
            for (Location location : locDB) {
                location.getSubLocation().removeAll(Collections.singleton(null));
            }
            loc_busy = false;
            Intent intent = new Intent(BROADCAST_LOCDB_READY);
            context.sendBroadcast(intent);

            if (waitingToUploadDeviceFlag) {
                updateLocation(holderLoc, holderSubloc, holderDevice);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            my_busy = false;
        }
    };




    public boolean updateLocation(String loc, String subLoc, Device device) {
        if (locDB != null && !loc_busy) {
            waitingToUploadDeviceFlag = false;
            boolean deviceFound = false;
            Location targetLoc = null;
            SubLocation targetSubloc = null;
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = mDatabase.child(LOCATION_DB);

            for (Location location : locDB) {
                if (location.getName().equals(loc)) {
                    targetLoc = location;
                    for (SubLocation subLocation : location.getSubLocation()) {
                        if (subLocation.getName().equals(subLoc)) {
                            targetSubloc = subLocation;
                            int index = location.getSubLocation().indexOf(subLocation);
                            if (subLocation.getDevices() != null) {
                                for (Device labDevice : subLocation.getDevices().values()) {
                                    if (device.getDev_codename().equals(labDevice.getDev_codename()) && device.getDev_serial().equals(labDevice.getDev_serial())) {
                                        deviceFound = true;
                                        if (loc.equals(location.getName())) {
                                            //device exists at location, can update
                                            if (labDevice.getDev_install_date() != null) {
                                                device.setDev_install_date(labDevice.getDev_install_date());
                                            }
                                            databaseReference.child(location.getName()).child("subLocation").child(String.valueOf(index))
                                                    .child("devices").child(device.getDev_serial()).setValue(device);
                                            return true;

                                        } else {
                                            // device exists somewhere else, move or cancel
                                            Toast.makeText(context, "Device exists at " + location.getName() + " - " + subLocation.getName(), Toast.LENGTH_SHORT).show();
                                            return false;

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

            //device not found, create new device
            if (targetLoc == null) {
                Location location = new Location(loc, new Address("","","", "", ""), "");
                SubLocation subLocation = new SubLocation(subLoc, "");
                subLocation.addDevice(device);
                location.addSublocation(subLocation);
                databaseReference.child(location.getName()).setValue(location);
                return true;
            } else if (targetSubloc == null) {
                SubLocation subLocation = new SubLocation(subLoc, "");
                subLocation.addDevice(device);
                databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(targetLoc.getSubLocation().size()+1)).setValue(subLocation);
                return true;
            }else if(!deviceFound){
                databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(targetLoc.getSubLocation().indexOf(targetSubloc)))
                        .child("devices").child(device.getDev_serial()).setValue(device);
            }


        } else {
            holderLoc = loc;
            holderSubloc = subLoc;
            holderDevice = device;
            waitingToUploadDeviceFlag = true;
            downloadLocDB();
        }
        return false;
    }


    public boolean updateLocation(Location location) {
        if (locDB != null && !loc_busy) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = mDatabase.child(LOCATION_DB);
            databaseReference.child(location.getName()).setValue(location);
            return true;
        } else {
            return false;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
