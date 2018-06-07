package il.co.diamed.com.form.res.providers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.NumberPicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.inventory.InventoryItem;

public class DatabaseProvider {
    private final String TAG = "Database";
    private HashMap<String,InventoryItem> labInv = null;
    private HashMap<String,InventoryItem> myInv = null;
    private boolean labFlag = false, myFlag = false;
    private DatabaseReference myDatabaseRef;
    private DatabaseReference labDatabaseRef;

    public DatabaseProvider(){

    }

    public List<InventoryItem> getLabInv(){
        if(labFlag) {
            return new ArrayList<>(labInv.values());
        }else{
            return null;
        }
    }

    public List<InventoryItem> getMyInv() {
        if(myFlag) {
            return new ArrayList<>(myInv.values());
        }else{
            return null;
        }
    }

    public void initializeDatabase(){
        myFlag = false;
        labFlag = false;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            myDatabaseRef = mDatabase.child("Parts/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            labDatabaseRef = mDatabase.child("Parts/Lab");

            myDatabaseRef.addListenerForSingleValueEvent(myListener);
            labDatabaseRef.addListenerForSingleValueEvent(labListener);
        }else{

            FirebaseAuth.getInstance().addAuthStateListener(authStateListener);

        }

    }
    private FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            initializeDatabase();

    };


    private ValueEventListener labListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            labInv = getInv(dataSnapshot);
            labFlag = true;
            if(myFlag){
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
            if(labFlag){
                refreshDatabase();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            myFlag = true;
        }
    };

    public void refreshDatabase() {
        for(InventoryItem labItem : labInv.values()){    //for every part in the lab
            if(myInv.containsKey(labItem.getSerial())){        // if it exists already
                myInv.get(labItem.getSerial()).setMinimum(labItem.getMinimum()); //update minimum (just in case)

            }else{
                try {
                    if (Integer.valueOf(labItem.getMinimum()) > 0) { //check if it's mandatory part
                        labItem.setinStock("0");                       //update to 0 in stock
                        myInv.put(labItem.getSerial(),labItem);
                    }
                }catch (NumberFormatException e){
                    Log.e(TAG, labItem.getDescription() +" has invalid minimum of: "+labItem.getMinimum());
                }
            }
        }

    }


    private HashMap<String,InventoryItem> getInv(DataSnapshot dataSnapshot) {
        HashMap<String,InventoryItem> tempValues = new HashMap<>();
        if(dataSnapshot.getValue()==null) {//root
            Log.e(TAG, "Cannot browse root folder");
        }else {
            if (dataSnapshot.getValue() == null) {//root
                Log.e(TAG, "Database " + dataSnapshot.getKey() + " does not exists");
            } else {  //Parts
                for (DataSnapshot ds : dataSnapshot.getChildren()) { //Serial numbers
                    InventoryItem temp = new InventoryItem(ds.getValue(InventoryItem.class));
                    tempValues.put(temp.getSerial(),temp);
                }
            }
        }
        return tempValues;
    }


    public void updateDatabase(List<InventoryItem> currentList){
        for(InventoryItem item : currentList)
            myDatabaseRef.child(item.getSerial()).setValue(item);
    }

    public List<InventoryItem> getMissingInv() {
        if(myFlag) {
            List<InventoryItem> missingInv = new ArrayList<>();
            for (InventoryItem item : myInv.values()) {
                if (item.getInStock().compareTo(item.getMinimum()) < 0 || item.getInStock().equals("0"))
                    missingInv.add(myInv.get(item.getSerial()));
            }

            return missingInv;
        }else
            return null;
    }

    public String[] getLabParts() {
        if(labFlag) {
            Set<String> serials = labInv.keySet();
            return serials.toArray(new String[serials.size()]);
        }else
            return null;
    }

    public String getPartInfo(String serial) {
        return (labInv.keySet().contains(serial)) ? labInv.get(serial).getDescription() : "";
    }

    public void addToMyInventory(String serial, Integer inStock) {
        InventoryItem item = labInv.get(serial);
        item.setinStock(String.valueOf(inStock));
        myInv.put(serial,item);
        //refreshDatabase();


    }
}
