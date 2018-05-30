package il.co.diamed.com.form.res.providers;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;

import il.co.diamed.com.form.res.ObjectLocation;

public class DatabaseProvider {
    private final String TAG = "Database";

    public DatabaseProvider(){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Single data changed ");
                Log.e(TAG,"Snapshot children count: "+dataSnapshot.getChildrenCount());
                printTree(dataSnapshot.getChildrenCount(),dataSnapshot);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Single Canceled");
            }
        };

        ChildEventListener listener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG,"Child added "+s);
                Log.e(TAG,"Child added "+dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG,"Child Changed");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Child Removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG,"Child Moved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Child - Canceled");
            }
        };

        OnCompleteListener completeListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.e(TAG, "Completed");
                mDatabase.addListenerForSingleValueEvent(listener);
            }
        };

        mDatabase.addListenerForSingleValueEvent(listener);
/*
        Log.e(TAG,"Before adding");
        ObjectLocation location = new ObjectLocation("medical","herzelia","Somemail@you.no","09-8348567");
        mDatabase.child("locations").child(location.getLocName()).setValue(location).addOnCompleteListener(completeListener);

        Log.e(TAG,"adding MH");
        location = new ObjectLocation("מעיני הישועה","בני ברק","Somemail@you.no","09-8333567");
        mDatabase.child("locations").child(location.getLocName()).setValue(location).addOnCompleteListener(completeListener);

        Log.e(TAG,"adding M");
        location = new ObjectLocation("מאיר","כפר סבא","Somemail@you.no","09-83489347");
        mDatabase.child("locations").child(location.getLocName()).setValue(location).addOnCompleteListener(completeListener);
*/

        //mDatabase.addListenerForSingleValueEvent(listener);
        //mDatabase.addChildEventListener(listener1);
    }

    private void printTree(long childrenCount, DataSnapshot dataSnapshot) {
        Log.e(TAG, "key: " + dataSnapshot.getKey()+"\ncount: "+dataSnapshot.getChildrenCount());


            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if(ds.hasChildren()){
                    printTree(ds.getChildrenCount(),ds);
                }else {
                    Log.e(TAG, "child: " + ds.getValue());
                }
            }


    }
}
