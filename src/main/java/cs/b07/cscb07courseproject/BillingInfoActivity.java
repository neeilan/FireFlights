package cs.b07.cscb07courseproject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import controllers.Router;

public class BillingInfoActivity extends AppCompatActivity {



    private Button saveButton;
    private EditText editTextDisplayEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextAddress;
    private EditText editTextCCNum;
    private EditText editTextCCExpiry;
    private Button buttonViewUserItineraries;
    private Button buttonSignOut;

    private int year, month, day;
    private DatePickerDialog datePickerDialog;


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onResume(){
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
        else if (!Router.isAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            buttonViewUserItineraries = (Button) findViewById(R.id.buttonViewUserItineraries);
            buttonViewUserItineraries.setText("View my itineraries");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_info);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        saveButton = (Button) findViewById(R.id.buttonSaveBillingInfo);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextCCNum = (EditText) findViewById(R.id.editTextCCNum);
        editTextCCExpiry = (EditText) findViewById(R.id.editTextCCExpiry);
        progressDialog = new ProgressDialog(this);
        editTextDisplayEmail = (EditText) findViewById(R.id.editTextDisplayEmail);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
        else if (getIntent().hasExtra("userNodeKey")){ // billing info for a different user
            String key = getIntent().getStringExtra("userNodeKey");
            loadBillingInfo(key);
            buttonSignOut.setVisibility(View.GONE);
            editTextDisplayEmail.setText(Utils.getEmailFromDbKey(key), EditText.BufferType.NORMAL);
            editTextDisplayEmail.setFocusable(false);
        }
        else {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            editTextDisplayEmail.setText(user.getEmail(), EditText.BufferType.NORMAL);
            editTextDisplayEmail.setFocusable(false);
            String userNodeKey = Utils.getDbKeyFromEmail(firebaseAuth.getCurrentUser().getEmail()); // edit our own info
            loadBillingInfo(userNodeKey);
        }


    }



    private void loadBillingInfo(String userNodeKey){
        databaseReference.child("Users").child(userNodeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> userInfo = (HashMap<String, String>) dataSnapshot.getValue();

                if (userInfo == null) return;
                String address = "";
                String firstName = userInfo.get("firstName").toString();
                String lastName = userInfo.get("lastName").toString();
                String ccNum = userInfo.get("ccNum").toString();
                String ccExpiry = userInfo.get("ccExpiry").toString();
                if (userInfo.containsKey("address")) {
                    address = userInfo.get("address").toString();

                }

                editTextFirstName.setText(firstName);
                editTextLastName.setText(lastName);
                editTextCCNum.setText(ccNum);
                editTextCCExpiry.setText(ccExpiry);
                editTextAddress.setText(address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void saveBillingInfo(String firstName, String lastName, String ccNum, String ccExpiry, String address){
        if (TextUtils.isEmpty(firstName)){
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lastName)){
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ccNum)){
            Toast.makeText(this, "Please enter credit card number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ccExpiry)){
            Toast.makeText(this, "Please enter card expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return;
        }



        String userNodeKey;
        if (getIntent().hasExtra("userNodeKey")){
            userNodeKey = getIntent().getStringExtra("userNodeKey");
        }
        else {
            userNodeKey = Utils.getDbKeyFromEmail(firebaseAuth.getCurrentUser().getEmail());
        }
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("ccNum", ccNum);
        userInfo.put("ccExpiry", ccExpiry);
        userInfo.put("address", address);

        progressDialog.setMessage("Saving...");
        progressDialog.show();

        databaseReference.child("Users")
                .child(userNodeKey)
                .setValue(userInfo)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "User info updated", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();
                    }
                });




    }

    public void onSaveBillingInfoClicked(View v) {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String CCNum = editTextCCNum.getText().toString().trim();
        String CCExpiry = editTextCCExpiry.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(CCNum)
                || TextUtils.isEmpty(CCExpiry) || TextUtils.isEmpty(address)){
            Toast.makeText(getApplicationContext(), "Please ensure all fields are filled ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            Utils.dateFormat.setLenient(false);
            Date expiryDate = Utils.dateFormat.parse(CCExpiry);
            if (expiryDate.before(new Date())){
                Toast.makeText(getApplicationContext(),"Credit card is expired", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(),"Please check that the expiry date is valid (yyyy-MM-dd)", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        saveBillingInfo(firstName, lastName, CCNum, CCExpiry, address);

    }

    public void onSignOutClicked(View v){
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    public void onViewUserItinerariesClicked(View v){
        if (getIntent().hasExtra("userNodeKey")){
            Intent intent = new Intent(getApplicationContext(),UserItinerariesActivity.class);
            String userEmail = Utils.getEmailFromDbKey(getIntent().getStringExtra("userNodeKey"));
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        }
        else{
            startActivity(new Intent(getApplicationContext(), UserItinerariesActivity.class));
        }
    }

}
