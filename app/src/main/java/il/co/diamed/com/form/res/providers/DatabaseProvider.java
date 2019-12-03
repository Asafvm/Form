package il.co.diamed.com.form.res.providers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.FieldDevice;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.data_objects.PrototypeDevice;
import il.co.diamed.com.form.data_objects.SubLocation;
import il.co.diamed.com.form.inventory.Part;
import il.co.diamed.com.form.inventory.InventoryUser;

public class DatabaseProvider {
    public static final String BROADCAST_USER_DB_READY = "database_provider_users_data_ready";
    public static final String BROADCAST_DB_READY = "database_provider_data_ready";
    public static final String BROADCAST_GLOBAL_PART_DB_READY = "database_provider_lab_data_ready";
    public static final String BROADCAST_TARGET_DB_READY = "database_provider_target_data_ready";
    public static final String BROADCAST_LOCDB_READY = "database_provider_location_data_ready";
    public static final String BROADCAST_APPVER = "database_app_ver";
    public static final String BROADCAST_FIREBASE_DIR = "database_provider_firebase_dir_ready";
    public static final String BROADCAST_REFRESH_ADAPTER = "refresh_adapter";
    private final String TAG = "Database";
    private static HashMap<String, Part> globalPartDB;
    private static HashMap<String, Part> personalDB;
    private static HashMap<String, String> personalInfo;   //contains personal info
    private static HashMap<String, String> personalEquipment;  //containg mesuring devices and possible lab devices
    private static HashMap<String, Part> targetDB;
    private static ArrayList<Location> locDB;
    private static ArrayList<PrototypeDevice> pDeviceDB;
    private static ArrayList<FieldDevice> fDeviceDB;
    private final DatabaseReference rootReference;

    private List<ArrayList<InventoryUser>> allUsers;
    private final String USER_DB = "Users/";
    private String DB = "Parts/";
    private String LOCATION_DB = "Location";
    private String DEVICEDB_PROTO = "Devices/Proto/";
    private String DEVICEDB_FIELD = "Devices/Field/";

    private Context context;
    private boolean globalPart_busy = false;
    private boolean personal_busy = false;
    private boolean target_busy = false;
    private String last_target = "";
    private boolean loc_busy;
    private boolean waitingToUploadDeviceFlag = false;
    private String holderLoc = "";
    private String holderSubloc = "";
    private FieldDevice holderDevice = null;
    private boolean pDevice_busy = false;
    private boolean fDevice_busy = false;


    public DatabaseProvider(Context context) {
        this.context = context;
        rootReference = FirebaseDatabase.getInstance().getReference().child("test");
    }

    public void getAppVer() {
        FirebaseDatabase.getInstance().getReference().child("AppVer").addListenerForSingleValueEvent(verListener);
    }

    private ValueEventListener verListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Intent intent = new Intent(BROADCAST_APPVER);
            intent.putExtra("AppVer", (String) dataSnapshot.getValue());
            context.sendBroadcast(intent);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public void getFirebaseDir(String path) {
        if (path.equals(""))
            rootReference.child("Files").addListenerForSingleValueEvent(pathListener);
        else
            rootReference.child(path).addListenerForSingleValueEvent(pathListener);
    }

    private ValueEventListener pathListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Intent intent = new Intent(BROADCAST_FIREBASE_DIR);
            HashMap<String, String> files = new HashMap<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if (ds.hasChild("type")) {
                    //is file
                    files.put((String) ds.child("name").getValue(), (String) ds.child("file_location").getValue());
                } else {
                    //is dir
                    files.put(ds.getKey(), "");
                }
            }

            intent.putExtra("filenameArray", files);
            context.sendBroadcast(intent);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public List<Part> getGlobalInv() {
        if (globalPartDB != null && !globalPart_busy) {
            List<Part> globalDBsorted = new ArrayList<>(globalPartDB.values());
            Collections.sort(globalDBsorted);
            return globalDBsorted;
        } else {
            if (!globalPart_busy) {
                downloadGlobalPartDB();
            }
        }
        return null;
    }

    public List<ArrayList<InventoryUser>> getAllUsersInv() {
        if (allUsers != null) {
            return allUsers;
        } else {
            if (globalPartDB != null) {
                orginizeUsersData();
            } else
                getGlobalInv();
        }
        return null;
    }

    public List<Part> getPersonalInv() {
        if (personalDB != null && !personal_busy) {
            return new ArrayList<>(personalDB.values());
        } else {
            if (!personal_busy) {
                downloadPersonalDB();
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

    private void downloadPersonalDB() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isUserLogged()) {
            personalDB = null;
            personal_busy = true;
            rootReference
                    .child(USER_DB).child(user.getUid()).child(DB).addValueEventListener(myListener);

        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

    private void downloadTargetDB(String target) {
        target_busy = true;
        personalDB = null;
        personal_busy = true;
        rootReference
                .child(USER_DB).child(target).child(DB).addValueEventListener(targetListener);
    }


    private void downloadGlobalPartDB() {
        globalPart_busy = true;
        DatabaseReference mDatabase = rootReference.child(DB);
        mDatabase.addValueEventListener(globalPartDBListener);

    }


    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null)        //if user logged
                DatabaseProvider.this.downloadPersonalDB();
            else
                personalDB = null;

        }
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
            personal_busy = false;
        }
    };


    private ValueEventListener globalPartDBListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            globalPartDB = getInv(dataSnapshot);

            globalPart_busy = false;
            Intent intent = new Intent(BROADCAST_GLOBAL_PART_DB_READY);
            context.sendBroadcast(intent);
            refreshDatabase();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            personal_busy = false;
        }
    };


    private ValueEventListener myListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            personalDB = getInv(dataSnapshot);
            personal_busy = false;
            refreshDatabase();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            personal_busy = false;
        }
    };


    private void refreshDatabase() {
        if (globalPartDB != null & personalDB != null) {
            for (Part item : globalPartDB.values()) {    //for every part in the main part db
                if (personalDB.containsKey(item.getId())) {        // if it exists already
                    personalDB.get(item.getId()).setMinimum_car(item.getMinimum_car()); //update minimum (just in case)
                } else {
                    try {
                        if (Integer.valueOf(item.getMinimum_car()) > 0) { //check if it's mandatory part
                            item.setinStock("0");                       //update to 0 in stock
                            personalDB.put(item.getId(), item);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, item.getDescription() + " has invalid minimum of: " + item.getMinimum_car());
                    }
                }
            }
            Intent intent = new Intent(BROADCAST_DB_READY);
            context.sendBroadcast(intent);
        } else {
            if (globalPartDB == null && !globalPart_busy)
                downloadGlobalPartDB();
            if (personalDB == null && !personal_busy)
                downloadPersonalDB();
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
        if (isUserLogged()) {
            DatabaseReference mDatabase = rootReference.child(USER_DB).child(user.getUid()).child(DB);
            if (currentList != null) {
                for (Part item : currentList) {
                    if (item.getInStock().equals("0") && (item.getMinimum_car().equals("0") || item.getMinimum_car().equals("00")))
                        mDatabase.child(item.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Unused part " + item.getDescription() + " removed from list");
                            }
                        });
                    else
                        mDatabase.child(item.getId()).setValue(item);
                }
                Log.d(TAG, "Finished updating inventory");

                downloadPersonalDB();
            }
        }
    }

    public List<Part> getMissingInv() {
        if (personalDB != null) {
            List<Part> missingInv = new ArrayList<>();
            for (Part item : personalDB.values()) {
                if (item.getInStock().compareTo(item.getMinimum_car()) <= 0)
                    missingInv.add(personalDB.get(item.getId()));
            }
            return missingInv;
        } else if (!personal_busy) {
            downloadPersonalDB();
        }
        return null;
    }


    public String getPartInfo(String serial, String desc) {
        if (desc.equals("")) {
            for (Part item : globalPartDB.values())
                if (item.getSerial().equals(serial))
                    return item.getDescription();
        } else if (serial.equals("")) {
            for (Part item : globalPartDB.values())
                if (item.getDescription().equals(desc))
                    return item.getSerial();
        }
        return "";
    }

    public boolean addMyDB(String serial, Integer inStock) {
        Part target = null;
        if (personalDB != null) {
            for (Part myItem : personalDB.values()) {    //search local
                if (myItem.getSerial().equals(serial)) {
                    target = myItem;
                }
            }
            if (target != null) {
                int current = Integer.valueOf(target.getInStock());
                target.setinStock(String.valueOf(inStock + current));
                personalDB.put(target.getId(), target);
                uploadMyInv(new ArrayList<>(personalDB.values()));
                return true;
            } else {
                for (Part item : globalPartDB.values())   //serach remote
                    if (item.getSerial().equals(serial))
                        target = item;
                if (target != null) {
                    target.setinStock(String.valueOf(inStock));
                    personalDB.put(target.getId(), target);
                    uploadMyInv(new ArrayList<>(personalDB.values()));
                    return true;
                }
            }
            return false;
        } else {
            downloadPersonalDB();
            return false;
        }
    }

    private void orginizeUsersData() {
        DatabaseReference databaseReference = rootReference.child(USER_DB);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers = new ArrayList<>();
                ArrayList<Part> labDBsorted = new ArrayList<>(globalPartDB.values());
                Collections.sort(labDBsorted);
                for (Part item : labDBsorted) {//globalPartDB.values()){
                    //HashMap<String,String> holders = new HashMap<>();
                    ArrayList<InventoryUser> holders = new ArrayList<>();
                    for (DataSnapshot users : dataSnapshot.getChildren()) {    //gor every user, get data base1
                        String id = users.getKey();
                        String name = (String) users.child("Info").child("techName").getValue();
                        HashMap<String, String> stock = ((HashMap<String, String>) users.child("Parts").child(item.getId()).getValue());
                        String inStock = (stock == null) ? "0" : stock.get("inStock");
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
        getGlobalInv();
        getPersonalInv();
        getLocDB();
        getPDeviceDB();
        getFDeviceDB();
        getUserInfo();
    }


    /***************************** My Database *************************************/


    private void getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            rootReference.child(USER_DB).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.getKey() != null) {
                            switch (d.getKey()) {
                                case "Info": {
                                    GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                                    };
                                    personalInfo = d.getValue(genericTypeIndicator);
                                    break;
                                }
                                case "Parts": {
                                    GenericTypeIndicator<HashMap<String, Part>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Part>>() {
                                    };
                                    personalDB = d.getValue(genericTypeIndicator);
                                    break;
                                }
                                case "Tools": {
                                    GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                                    };
                                    personalEquipment = d.getValue(genericTypeIndicator);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    public void uploadUserData(HashMap<String, String> userInfo, String folder) {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isUserLogged()) {
            DatabaseReference databaseReference = rootReference.child(USER_DB).child(user.getUid()).child(folder);
            for (String key : userInfo.keySet()) {
                databaseReference.child(key).setValue(userInfo.get(key));
            }
        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

    public void downloadUserData(HashMap<String, String> userInfo, String folder) {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isUserLogged()) {
            DatabaseReference databaseReference = rootReference.child(USER_DB).child(user.getUid()).child(folder);
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
        DatabaseReference mDatabase = rootReference.child(LOCATION_DB);
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
            personal_busy = false;
        }
    };

    /**
     * This funcion looks up a specific device at a specific location
     * if found, update or move it as needed. delete original if device moved
     *
     * @param loc
     * @param subLoc
     * @param device
     * @return
     */
    public boolean updateLocation(String loc, String subLoc, FieldDevice device) {
        if (locDB != null && !loc_busy) {
            waitingToUploadDeviceFlag = false;
            boolean deviceFound = false;
            Location foundLoc = null;
            SubLocation foundSubloc = null;
            Location targetLoc = null;
            SubLocation targetSubloc = null;
            FieldDevice foundDev = null;

            DatabaseReference databaseReference = rootReference.child(LOCATION_DB);

            //searching location and sublocation exists
            for (Location location : locDB) {
                if (location.getName().equals(loc)) {
                    targetLoc = location;
                }
                for (SubLocation subLocation : location.getSubLocation()) {
                    if (subLocation.getName().equals(subLoc)) {
                        targetSubloc = subLocation;
                    }
                    if (subLocation.getDevices() != null) {
                        for (FieldDevice labDevice : subLocation.getDevices().values()) {
                            if (device.getDev_identifier().equals(labDevice.getDev_identifier()) && device.getDev_serial().equals(labDevice.getDev_serial())) {
                                deviceFound = true;
                                foundDev = labDevice;
                                foundLoc = location;
                                foundSubloc = subLocation;
                                foundDev.setDev_next_maintenance(device.getDev_next_maintenance());
                                //found device somewhere
                            }
                        }
                    }
                }
            }

            if (deviceFound) {
                boolean delete = true;
                if (foundLoc == targetLoc && foundSubloc == targetSubloc) {
                    //same location and sub location
                    //update
                    int index = targetLoc.getSubLocation().indexOf(targetSubloc);

                    databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(index))
                            .child("devices").child(foundDev.getDev_serial()).setValue(foundDev).addOnSuccessListener(refreshListener);
                    delete = false;
                } else if (foundLoc == targetLoc) {
                    //boolean move = confirmMove(loc,subLoc,foundLoc.getName(),foundSubloc.getName());
                    //same location, different sub location
                    //set device at new location
                    if (targetSubloc == null) {
                        //if sublocation not exists
                        targetSubloc = new SubLocation(subLoc, "");
                        targetSubloc.addDevice(foundDev);
                        databaseReference.child(targetLoc.getName()).child("subLocation")
                                .child(String.valueOf(targetLoc.getSubLocation().size())).setValue(targetSubloc).addOnSuccessListener(refreshListener);
                    } else {
                        //if sublocation exists
                        int index = 0;
                        for (SubLocation subLocation : targetLoc.getSubLocation())
                            if (subLocation.getName().equals(subLoc))
                                index = targetLoc.getSubLocation().indexOf(subLocation);

                        databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(index))
                                .child("devices").child(foundDev.getDev_serial()).setValue(foundDev).addOnSuccessListener(refreshListener);
                    }


                } else {
                    //different location
                    foundDev.setDev_install_date(device.getDev_install_date());
                    foundDev.setEnd_of_warranty(device.getEnd_of_warranty());
                    foundDev.setDev_under_warranty(device.isDev_under_warranty());
                    if (targetLoc == null) {
                        //new location
                        targetLoc = new Location(loc, new Address("", "", "", "", ""), "");
                        targetSubloc = new SubLocation(subLoc, "");
                        targetSubloc.addDevice(foundDev);
                        targetLoc.addSublocation(targetSubloc);
                        databaseReference.child(targetLoc.getName()).setValue(targetLoc).addOnSuccessListener(refreshListener);

                    } else if (targetSubloc == null) {
                        //new sublocation at existing location
                        targetSubloc = new SubLocation(subLoc, "");
                        targetSubloc.addDevice(foundDev);
                        databaseReference.child(targetLoc.getName()).child("subLocation")
                                .child(String.valueOf(targetLoc.getSubLocation().size())).setValue(targetSubloc).addOnSuccessListener(refreshListener);

                    } else {
                        //move to existing location and sublocation
                        int index = 0;
                        for (SubLocation subLocation : targetLoc.getSubLocation())
                            if (subLocation.getName().equals(targetSubloc.getName()))
                                index = targetLoc.getSubLocation().indexOf(subLocation);
                        databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(index))
                                .child("devices").child(foundDev.getDev_serial()).setValue(foundDev).addOnSuccessListener(refreshListener);
                    }
                }
                //delete original if needed
                if (delete) {
                    int index = foundLoc.getSubLocation().indexOf(foundSubloc);
                    databaseReference.child(foundLoc.getName()).child("subLocation").child(String.valueOf(index))
                            .child("devices").child(device.getDev_serial()).setValue(null).addOnSuccessListener(refreshListener);
                }
                Intent intent = new Intent(BROADCAST_REFRESH_ADAPTER);
                context.sendBroadcast(intent);
                return true;
            } else {    //new device
                if (targetLoc == null) {
                    //new location
                    targetLoc = new Location(loc, new Address("", "", "", "", ""), "");
                    targetSubloc = new SubLocation(subLoc, "");
                    targetSubloc.addDevice(device);
                    targetLoc.addSublocation(targetSubloc);
                    databaseReference.child(targetLoc.getName()).setValue(targetLoc).addOnSuccessListener(refreshListener);

                } else if (targetSubloc == null) {
                    //new sublocation at existing location
                    targetSubloc = new SubLocation(subLoc, "");
                    targetSubloc.addDevice(device);
                    databaseReference.child(targetLoc.getName()).child("subLocation")
                            .child(String.valueOf(targetLoc.getSubLocation().size())).setValue(targetSubloc).addOnSuccessListener(refreshListener);

                } else {
                    //move to existing location and sublocation
                    int index = 0;
                    for (SubLocation subLocation : targetLoc.getSubLocation())
                        if (subLocation.getName().equals(targetSubloc.getName()))
                            index = targetLoc.getSubLocation().indexOf(subLocation);
                    databaseReference.child(targetLoc.getName()).child("subLocation").child(String.valueOf(index))
                            .child("devices").child(device.getDev_serial()).setValue(device).addOnSuccessListener(refreshListener).addOnSuccessListener(refreshListener);
                }
                return true;
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

    public FieldDevice getDevice(String serial, String codeName) {
        for (Location location : locDB) {
            for (SubLocation subLocation : location.getSubLocation()) {
                if (subLocation.getDevices() != null) {
                    for (FieldDevice labDevice : subLocation.getDevices().values()) {
                        if (labDevice.getDev_identifier().equals(codeName) && labDevice.getDev_serial().equals(serial)) {
                            return labDevice;
                        }
                    }
                }
            }
        }
        return null;
    }


    public boolean updateLocation(Location location) {
        if (locDB != null && !loc_busy) {
            DatabaseReference databaseReference = rootReference.child(LOCATION_DB);
            databaseReference.child(location.getName()).setValue(location);
            return true;
        } else {
            return false;
        }
    }


    public void updateDevice(String serial, String codeName, long ins, long eow, boolean under_warranty, String comments) {
        DatabaseReference databaseReference = rootReference.child(LOCATION_DB);
        for (Location location : locDB) {
            for (SubLocation subLocation : location.getSubLocation()) {
                if (subLocation.getDevices() != null) {
                    for (FieldDevice labDevice : subLocation.getDevices().values()) {
                        if (labDevice.getDev_identifier().equals(codeName) && labDevice.getDev_serial().equals(serial)) {
                            labDevice.setDev_under_warranty(under_warranty);
                            labDevice.setDev_install_date(new Date(ins));
                            labDevice.setEnd_of_warranty(new Date(eow));
                            labDevice.setDev_comments(comments);
                            int index = location.getSubLocation().indexOf(subLocation); //problem with index

                            databaseReference.child(location.getName()).setValue(location);
                            //child("subLocation").child(String.valueOf(index)).child("devices").child(labDevice.getDev_serial()).setValue(labDevice).addOnSuccessListener(refreshListener);

                            Intent intent = new Intent(BROADCAST_REFRESH_ADAPTER);
                            context.sendBroadcast(intent);
                        }
                    }
                }
            }
        }
    }

    public void deleteDevice(String serial, String codeName) {
        DatabaseReference databaseReference = rootReference.child(LOCATION_DB);
        for (Location location : locDB) {
            for (SubLocation subLocation : location.getSubLocation()) {
                if (subLocation.getDevices() != null) {
                    for (FieldDevice labDevice : subLocation.getDevices().values()) {
                        if (labDevice.getDev_identifier().equals(codeName) && labDevice.getDev_serial().equals(serial)) {
                            int index = location.getSubLocation().indexOf(subLocation);

                            databaseReference.child(location.getName()).child("subLocation").child(String.valueOf(index))
                                    .child("devices").child(labDevice.getDev_serial()).setValue(null).addOnSuccessListener(refreshListener);


                        }
                    }
                }
            }
        }
    }

    private OnSuccessListener<Void> refreshListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Intent intent = new Intent(BROADCAST_REFRESH_ADAPTER);
            context.sendBroadcast(intent);
        }
    };

    public HashMap<String, String> getPersonalInfo() {
        return personalInfo;
    }

    public HashMap<String, String> getPersonalEquipment() {
        return personalEquipment;
    }

    public void uploadNewLocation(Location l) {
        if (isUserLogged()) {
            DatabaseReference mDatabase = rootReference.child(LOCATION_DB).child(l.getName());

            mDatabase.setValue(l).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "Uploaded: " + l.getName()));
        }
    }

    public void uploadPrototypeDevice(PrototypeDevice device) {
        if (isUserLogged()) {
            rootReference.child(DEVICEDB_PROTO)
                    .child(device.getDev_manufacturer())
                    .child(device.getDev_codeNumber())
                    .setValue(device)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Uploaded Proto: " + device.getDev_codeName()));

        }
    }

    public ArrayList<PrototypeDevice> getPDeviceDB() {
        if (pDeviceDB != null) {
            return pDeviceDB;
        } else if (!pDevice_busy) {
            downloadPrototypeDevice();
        }
        return null;

    }
    public ArrayList<FieldDevice> getFDeviceDB() {
        if (fDeviceDB != null) {
            return fDeviceDB;
        } else if (!fDevice_busy) {
            downloadFieldDevice();;
        }
        return null;

    }
    public void downloadPrototypeDevice() {
        pDevice_busy = true;
        rootReference.child(DEVICEDB_PROTO).addValueEventListener(listener_prototypedevice);
    }

    private ValueEventListener listener_prototypedevice = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (pDeviceDB != null)
                pDeviceDB.clear();
            else
                pDeviceDB = new ArrayList<>();
            for (DataSnapshot d1 : dataSnapshot.getChildren())
                for (DataSnapshot d2 : d1.getChildren())
                    pDeviceDB.add(d2.getValue(PrototypeDevice.class));

            pDevice_busy = false;

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void uploadFieldDevice(FieldDevice fDevice) {
        if (isUserLogged()) {
            rootReference.child(DEVICEDB_FIELD)
                    .child(fDevice.getDev_identifier())
                    .child(fDevice.getDev_serial())
                    .setValue(fDevice)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Uploaded Field: " + fDevice.getDev_identifier()+" - "+fDevice.getDev_serial()));

        }
    }

    private boolean isUserLogged() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user!=null;
    }


    public void downloadFieldDevice() {
        fDevice_busy = true;
        rootReference.child(DEVICEDB_FIELD).addValueEventListener(listener_Fielddevice);
    }

    private ValueEventListener listener_Fielddevice = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (fDeviceDB != null)
                fDeviceDB.clear();
            else
                fDeviceDB = new ArrayList<>();
            for (DataSnapshot d1 : dataSnapshot.getChildren())
                for (DataSnapshot d2 : d1.getChildren())
                    fDeviceDB.add(d2.getValue(FieldDevice.class));

            fDevice_busy = false;

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
