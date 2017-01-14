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
import android.widget.Toast;

import cs.b07.cscb07courseproject.R;
import cs.b07.cscb07courseproject.SignInActivity;
import cs.b07.cscb07courseproject.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class FlightInfoActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText editTextAirline;
    private EditText editTextOrigin;
    private EditText editTextDestination;
    private EditText editTextDeparture;
    private EditText editTextArrival;
    private EditText editTextCost;
    private EditText editTextNumSeats;


    private int year, month, day;
    private DatePickerDialog datePickerDialog;


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String flightNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);

        saveButton = (Button) findViewById(R.id.buttonSaveFlightInfo);
        editTextAirline = (EditText) findViewById(R.id.editTextAirline);

        editTextOrigin = (EditText) findViewById(R.id.editTextOrigin);
        editTextDestination = (EditText) findViewById(R.id.editTextDestination);
        editTextArrival = (EditText) findViewById(R.id.editTextArrival);
        editTextCost = (EditText) findViewById(R.id.editTextCost);
        editTextDeparture = (EditText) findViewById(R.id.editTextDeparture);
        editTextNumSeats = (EditText) findViewById(R.id.editTextNumSeats);
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
        else {
            flightNum = getIntent().getStringExtra("flightNum");
            loadFlightInfo(flightNum);

        }
    }

    private void loadFlightInfo(String userNodeKey){
        if (flightNum == null) {return;}
        databaseReference.child("Flights").child(flightNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> flightInfo = (HashMap<String, Object>) dataSnapshot.getValue();

                if (flightInfo == null) return;

                String origin = flightInfo.get("origin").toString();
                String airline = flightInfo.get("airline").toString();
                String destination = flightInfo.get("destination").toString();
                String departsAt = flightInfo.get("departsAt").toString();
                String arrivesAt = flightInfo.get("arrivesAt").toString();
                String cost = flightInfo.get("cost").toString();
                String numSeats = flightInfo.get("numSeats").toString()  ;

                editTextOrigin.setText(origin);
                editTextAirline.setText(airline);
                editTextDestination.setText(destination);
                editTextDeparture.setText(departsAt);
                editTextArrival.setText(arrivesAt);
                editTextCost.setText(cost);
                editTextNumSeats.setText(numSeats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void saveFlightInfo(String airline, String origin, String destination, String departsAt, String arrivesAt, String cost, String numSeats){
        if (TextUtils.isEmpty(airline) || TextUtils.isEmpty(origin) || TextUtils.isEmpty(destination) || TextUtils.isEmpty(departsAt)
                || TextUtils.isEmpty(arrivesAt) || TextUtils.isEmpty(cost) || TextUtils.isEmpty(numSeats)){
            Toast.makeText(this, "Please ensure all fields are filled", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            Utils.dateTimeFormat.setLenient(false);
            Utils.dateTimeFormat.parse(departsAt);
            Utils.dateTimeFormat.parse(arrivesAt);

        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(),"Please check that the flight dates are valid (yyyy-MM-dd HH:mm format)", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        HashMap<String, Object> flightInfo = new HashMap<>();
        flightInfo.put("airline", airline);
        flightInfo.put("origin", origin);
        flightInfo.put("destination", destination);
        flightInfo.put("departsAt", departsAt);
        flightInfo.put("arrivesAt", arrivesAt);
        flightInfo.put("cost", Double.parseDouble(cost));
        flightInfo.put("numSeats", (int) Double.parseDouble(numSeats));


        progressDialog.setMessage("Saving...");
        progressDialog.show();

        databaseReference.child("Flights")
                .child(flightNum)
                .setValue(flightInfo)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Flight info saved", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();
                    }
                });
    }

    public void onSaveFlightInfoClicked(View v) {
        String airline = editTextAirline.getText().toString().trim();
        String origin  = editTextOrigin.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();
        String departsAt = editTextDeparture.getText().toString().trim();
        String arrivesAt = editTextArrival.getText().toString().trim();
        String cost = editTextCost.getText().toString().trim();
        String numSeats = editTextCost.getText().toString().trim();

        saveFlightInfo(airline, origin, destination, departsAt, arrivesAt, cost, numSeats);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

}
