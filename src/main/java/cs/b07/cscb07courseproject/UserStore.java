package cs.b07.cscb07courseproject;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Neeilan on 11/27/2016.
 */
public class UserStore {
    private DatabaseReference UsersRef;

    public UserStore(){
        UsersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
    }

    public void forEach(final Utils.Callback<String> func){
        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                func.call(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void performActionOnAll(){

    }

    public void performActionOnUser(String email, ValueEventListener listener) {
        String userNodeKey = Utils.getDbKeyFromEmail(email);
        UsersRef.child(userNodeKey).addListenerForSingleValueEvent(listener);
    }
}
