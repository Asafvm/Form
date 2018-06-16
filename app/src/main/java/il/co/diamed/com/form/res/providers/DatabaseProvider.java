package il.co.diamed.com.form.res.providers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import il.co.diamed.com.form.inventory.InventoryItem;

public class DatabaseProvider {
    private final String TAG = "Database";
    private HashMap<String, InventoryItem> labInv = null;
    private HashMap<String, InventoryItem> myInv = null;
    private HashMap<String, HashMap<String, InventoryItem>> allUsers = null;
    private boolean labFlag = false, myFlag = false;
    private DatabaseReference myDatabaseRef;
    private DatabaseReference labDatabaseRef;
    private final String USER_DB = "Users/";


    public DatabaseProvider() {

    }

    public List<InventoryItem> getLabInv() {
        if (labFlag) {
            return new ArrayList<>(labInv.values());
        } else {
            return null;
        }
    }

    public List<InventoryItem> getMyInv() {
        if (myFlag) {
            return new ArrayList<>(myInv.values());
        } else {
            return null;
        }
    }


    public void initializeDatabase() {
        myFlag = false;
        labFlag = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String userId = user.getUid();
            if (!userId.equals("")) {
                String DB = "Parts/";
                myDatabaseRef = mDatabase.child(USER_DB).child(userId).child(DB);
                myDatabaseRef.addListenerForSingleValueEvent(myListener);
                DatabaseReference labDatabaseRef = mDatabase.child(DB);
                labDatabaseRef.addListenerForSingleValueEvent(labListener);
            } else {
                Log.e(TAG, "No valid user mail");
            }
        } else {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener); //wait for user to log in
        }
    }

    private FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)        //if user logged
            initializeDatabase();

    };


    private ValueEventListener labListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            labInv = getInv(dataSnapshot);
            labFlag = true;
            if (myFlag) {
                refreshDatabase();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            labFlag = true;
        }
    };


    private ValueEventListener myListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            myInv = getInv(dataSnapshot);
            myFlag = true;
            if (labFlag) {
                refreshDatabase();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            myFlag = true;
        }
    };


    private void refreshDatabase() {
        for (InventoryItem labItem : labInv.values()) {    //for every part in the lab
            if (myInv.containsKey(labItem.getId())) {        // if it exists already
                myInv.get(labItem.getId()).setMinimum(labItem.getMinimum()); //update minimum (just in case)

            } else {
                try {
                    if (Integer.valueOf(labItem.getMinimum()) > 0) { //check if it's mandatory part
                        labItem.setinStock("0");                       //update to 0 in stock
                        myInv.put(labItem.getId(), labItem);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, labItem.getDescription() + " has invalid minimum of: " + labItem.getMinimum());
                }
            }
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
                    if (temp != null)
                        tempValues.put(temp.getId(), temp);
                    else
                        Log.e(TAG, "Invalid item recieved on getInv()");
                }
            }
        }
        return tempValues;
    }


    /**
     * @param currentList update remote personal database
     */
    public void updateRemoteInv(List<InventoryItem> currentList) {
        if (currentList != null) {
            for (InventoryItem item : currentList) {
                if (item.getInStock().equals("0") && (item.getMinimum().equals("0") || item.getMinimum().equals("00")))
                    myDatabaseRef.child(item.getId()).removeValue().addOnSuccessListener(aVoid -> Log.d(TAG, "Unused part " + item.getDescription() + " removed from list"));
                else
                    myDatabaseRef.child(item.getId()).setValue(item);
            }
            Log.d(TAG,"Finished updating inventory");
        }
    }

    public List<InventoryItem> getMissingInv() {
        if (myFlag) {
            List<InventoryItem> missingInv = new ArrayList<>();
            for (InventoryItem item : myInv.values()) {
                if (item.getInStock().compareTo(item.getMinimum()) <= 0)
                    missingInv.add(myInv.get(item.getId()));
            }

            return missingInv;
        } else
            return null;
    }

    public String[] getLabParts() {
        if (labFlag) {
            ArrayList<String> serials = new ArrayList<>();

            Set<String> ids = labInv.keySet();
            for (String id : ids) {
                serials.add(labInv.get(id).getId());
            }
            return serials.toArray(new String[serials.size()]);
        } else
            return null;
    }

    public String getPartInfo(String serial) {
        return (labInv.keySet().contains(serial)) ? labInv.get(serial).getDescription() : "";
    }

    public boolean addToMyLocalInventory(String serial, Integer inStock) {
        if (myInv.containsKey(serial)) {
            InventoryItem temp = myInv.get(serial);
            int current = Integer.valueOf(temp.getInStock());
            temp.setinStock(String.valueOf(inStock + current));
            myInv.put(serial, temp);
            return true;
        } else {
            InventoryItem item = labInv.get(serial);
            item.setinStock(String.valueOf(inStock));
            myInv.put(serial, item);
            return true;
        }
    }

    private void getUsersInventory() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = mDatabase.child(USER_DB);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers = new HashMap<>();
                for (DataSnapshot users : dataSnapshot.getChildren()) {    //gor every user, get data base
                    allUsers.put(users.getKey(), getInv(users.child(users.getKey())));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
