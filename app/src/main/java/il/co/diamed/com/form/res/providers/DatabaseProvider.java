package il.co.diamed.com.form.res.providers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;

import il.co.diamed.com.form.inventory.InventoryItem;
import il.co.diamed.com.form.inventory.InventoryUser;

public class DatabaseProvider {
    public static final String BROADCAST_USER_DB_READY = "database_provider_users_data_ready";
    public static final String BROADCAST_DB_READY = "database_provider_data_ready";
    public static final String BROADCAST_LABDB_READY = "database_provider_lab_data_ready";
    public static final String BROADCAST_TARGET_DB_READY = "database_provider_target_data_ready";
    private final String TAG = "Database";
    private static HashMap<String, InventoryItem> labDB = null;
    private static HashMap<String, InventoryItem> myDB = null;
    private static HashMap<String, InventoryItem> targetDB = null;
    private List<ArrayList<InventoryUser>> allUsers = null;
    private final String USER_DB = "Users/";
    private Context context;
    private boolean lab_busy = false;
    private boolean my_busy = false;
    private String DB = "Parts/";
    private boolean target_busy = false;
    private String last_target = "";

    public DatabaseProvider(Context context) {
        this.context = context;

    }

    public List<InventoryItem> getLabInv() {
        if (labDB != null && !lab_busy) {
            List<InventoryItem> labDBsorted = new ArrayList<>(labDB.values());
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
        if (allUsers!=null) {
            return allUsers;
        } else {
            if(labDB!=null){
                orginizeUsersData();
            }else
                getLabInv();
        }
        return null;
    }

    public List<InventoryItem> getMyInv() {
        if (myDB != null && !my_busy) {
            return new ArrayList<>(myDB.values());
        } else {
            if (!my_busy) {
                downloadMyDB();
            }
        }
        return null;
    }

    public List<InventoryItem> getTargetInv(String target) {
        if(!last_target.equals(target)) {
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
            for (InventoryItem labItem : labDB.values()) {    //for every part in the lab
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


    private HashMap<String, InventoryItem> getInv(DataSnapshot dataSnapshot) {
        HashMap<String, InventoryItem> tempValues = new HashMap<>();
        if (dataSnapshot.getValue() == null) {//root
            Log.e(TAG, "Cannot browse root folder");
        } else {
            if (dataSnapshot.getValue() == null) {//root
                Log.e(TAG, "Database " + dataSnapshot.getKey() + " does not exists");
            } else {  //Parts
                for (DataSnapshot ds : dataSnapshot.getChildren()) { //Serial numbers
                    InventoryItem temp = ds.getValue(InventoryItem.class);
                    if (temp != null) {
                        temp.setId(ds.getKey());
                        tempValues.put(temp.getId(), temp);
                    }else
                        Log.e(TAG, "Invalid item recieved on getInv()");
                }
            }
        }
        return tempValues;
    }


    /**
     * @param currentList update remote personal database
     */
    public void uploadMyInv(List<InventoryItem> currentList) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(USER_DB).child(user.getUid()).child(DB);
            if (currentList != null) {
                for (InventoryItem item : currentList) {
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

    public List<InventoryItem> getMissingInv() {
        if (myDB != null) {
            List<InventoryItem> missingInv = new ArrayList<>();
            for (InventoryItem item : myDB.values()) {
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
        for (InventoryItem item : labDB.values())
            if (item.getSerial().equals(serial))
                return item.getDescription();
        return "";
    }

    public boolean addMyDB(String serial, Integer inStock) {
        InventoryItem target = null;
        if (myDB != null) {
            for (InventoryItem myItem : myDB.values()) {    //search local
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
                for (InventoryItem item : labDB.values())   //serach remote
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
                ArrayList<InventoryItem> labDBsorted = new ArrayList<>(labDB.values());
                Collections.sort(labDBsorted);
                for (InventoryItem item : labDBsorted){//labDB.values()){
                    //HashMap<String,String> holders = new HashMap<>();
                    ArrayList<InventoryUser> holders = new ArrayList<>();
                    for (DataSnapshot users : dataSnapshot.getChildren()) {    //gor every user, get data base1
                        String id = users.getKey();
                        String name = (String)users.child("Info").child("techName").getValue();
                        HashMap<String,String> stock = ((HashMap<String,String>)users.child("Parts").child(item.getId()).getValue());
                        String inStock = stock == null ? "0" : stock.get("inStock");
                        InventoryUser user = new InventoryUser(id, name, inStock);
                        //HashMap<String,String> userInv = ;
                        //holders.put(name != null ? name : id, inStock == null ? "0" : inStock);
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

    public void uploadUserData(HashMap<String, String> userInfo) {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.getUid().equals("")) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = mDatabase.child(USER_DB).child(user.getUid()).child("Info");
            for(String key : userInfo.keySet()){
                databaseReference.child(key).setValue(userInfo.get(key));
            }
        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

}
