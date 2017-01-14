package cs.b07.cscb07courseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import controllers.Router;
import models.Flight;
import models.Itinerary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class BookItineraryActivity extends AppCompatActivity {
    private Itinerary itinerary;
    private EditText editTextClientEmail;
    private TextView itineraryCost;
    private Button bookButton;
    private String currentUserEmail;
    private String clientEmail;
    private DatabaseReference databaseReference;
    private RecyclerView flightsRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_itinerary);


        itinerary = (Itinerary) getIntent().getSerializableExtra("Itinerary");
        List<Flight> flights = itinerary.getFlights();
        editTextClientEmail = (EditText) findViewById(R.id.bookClientEmail);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        flightsRecyclerView = (RecyclerView) findViewById(R.id.flightsRecyclerView);
        bookButton = (Button) findViewById(R.id.buttonBookItinerary);
        flightsRecyclerView.setLayoutManager(layoutManager);
        flightsRecyclerView.setHasFixedSize(true);
        adapter = new FlightAdapter(this, flights);
        itineraryCost = (TextView) findViewById(R.id.itineraryCost);
        refreshItineraryCost();
        flightsRecyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (!Router.isAdmin(currentUserEmail)){
            // only admins can book for other clients
            editTextClientEmail.setVisibility(View.GONE);
            hideBookButtonIfBooked();

        }
    }

    private void hideBookButtonIfBooked(){
        databaseReference.child("BookedItineraries")
        .child(Utils.getDbKeyFromEmail(currentUserEmail))
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> usersItineraries = (HashMap) dataSnapshot.getValue();
                if (!Router.isAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        && usersItineraries != null
                        && usersItineraries.containsValue(itinerary.summaryString())
                        ){
                    bookButton.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void onBookButtonClicked (View v){
        String editTextEmail = editTextClientEmail.getText().toString().trim();
        if (Router.isAdmin(currentUserEmail) && !TextUtils.isEmpty(editTextEmail)){
            clientEmail = editTextEmail;
        }
        else{
            clientEmail = currentUserEmail;
        }
        databaseReference.child("BookedItineraries")
            .child(Utils.getDbKeyFromEmail(clientEmail))
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> usersItineraries = (HashMap) dataSnapshot.getValue();
                    if (usersItineraries != null && usersItineraries.containsValue(itinerary.summaryString())){

                        Toast.makeText(getApplicationContext(),
                                "Itinerary has been booked for this user previously",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    else if (itinerary.seatsAvailable()){
                        // Itinerary not previously booked and has seats
                        bookItinerary(clientEmail, itinerary);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Sorry... one or more of the flights is sold out",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

    }

    private void bookItinerary(String email, Itinerary itinerary){
        String summaryString = itinerary.summaryString();
        String emailKey = Utils.getDbKeyFromEmail(clientEmail);
        String key = databaseReference.child("BookedItineraries").child(emailKey)
                .push().getKey();
        databaseReference.child("BookedItineraries")
            .child(emailKey)
            .child(key)
            .setValue(summaryString)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        for (Flight flight : itinerary.getFlights()) {
                            decrementSeatNum(flight.getFlightNumber());
                        }
                        bookButton.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Booking successful!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), UserItinerariesActivity.class);
                        intent.putExtra("userEmail", email);
                        finish();
                        startActivity(intent);
                    }
                }
            });
    }

    public void refreshItineraryCost(){
        if (this.itinerary != null) {
            itineraryCost.setText(String.format("$%.2f", itinerary.travelCost()));
        }
    }

    private void decrementSeatNum(String flightNum){
        databaseReference.child("Flights")
            .child(flightNum)
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String,Object> flight = (HashMap) dataSnapshot.getValue();
                    long oldNumSeats = (long) flight.get("numSeats");
                    databaseReference.child("Flights")
                            .child(flightNum)
                            .child("numSeats")
                            .setValue(oldNumSeats-1);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
        });
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
