package cs.b07.cscb07courseproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Flight;
import models.Itinerary;

public class UserItinerariesActivity extends AppCompatActivity {
    private Itinerary itinerary;
    private String currentUserEmail;
    private TextView textViewClientEmail;
    private DatabaseReference databaseReference;
    private RecyclerView userItinerariesRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_itineraries);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userItinerariesRecyclerView = (RecyclerView) findViewById(R.id.userItinerariesRecyclerView);
        textViewClientEmail = (TextView) findViewById(R.id.textViewClientEmail);

        if (getIntent().hasExtra("userEmail")){
            currentUserEmail = getIntent().getStringExtra("userEmail");
        }
        else{
           currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        textViewClientEmail.setText(currentUserEmail);
        populateUserItineraries();
    }

    private void populateUserItineraries(){
        databaseReference.child("BookedItineraries")
        .child(Utils.getDbKeyFromEmail(currentUserEmail))
        .addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               HashMap<String,String> itineraries = (HashMap) dataSnapshot.getValue();
               if (itineraries == null || itineraries.values().size() <=  0) {return;}

               // user has booked itineraries
               databaseReference.child("Flights").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       HashMap<String, Flight> allFlights = new HashMap<>();
                       HashMap<String, HashMap<String, Object>> flights = (HashMap) dataSnapshot.getValue();
                       for (String flightNum : flights.keySet()) {
                           HashMap<String,Object> flightData = flights.get(flightNum);
                           Date departsAt = null;
                           Date arrivesAt = null;
                           long numSeats = 0;
                           try {
                               departsAt = Utils.dateTimeFormat.parse(flightData.get("departsAt").toString());
                               arrivesAt = Utils.dateTimeFormat.parse(flightData.get("arrivesAt").toString());
                               numSeats = (Long) flightData.get("numSeats");
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }
                           Flight f = new Flight(flightNum, departsAt, arrivesAt, flightData.get("airline").toString(),
                                   flightData.get("origin").toString(),
                                   flightData.get("destination").toString(),
                                   Double.parseDouble(flightData.get("cost").toString()),
                                   (int) numSeats);
                           allFlights.put(flightNum,f);
                       }

                       // render cards

                        ArrayList<Itinerary> itinerariesToRender = new ArrayList<>();
                       for (String itinerarySummary : itineraries.values()) {
                           Itinerary bookedItinerary = new Itinerary();
                           String[] flightNums = Itinerary.getFlightNumsFromSummaryString(itinerarySummary);
                           for (String flightNum : flightNums) {
                               bookedItinerary.addFlight(allFlights.get(flightNum));
                           }
                           if (!itinerariesToRender.contains(bookedItinerary))
                                itinerariesToRender.add(bookedItinerary);
                       }
                       renderResults(itinerariesToRender);
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }

    private void renderResults(List<Itinerary> results){
        if (results.size() == 0){
            Toast.makeText(getApplicationContext(), "User has no booked itineraries", Toast.LENGTH_LONG)
                    .show();
        }
        layoutManager = new LinearLayoutManager(getApplicationContext());
        userItinerariesRecyclerView.setLayoutManager(layoutManager);
        userItinerariesRecyclerView.setHasFixedSize(true);
        adapter = new ItineraryAdapter(getApplicationContext(), results);
        userItinerariesRecyclerView.setAdapter(adapter);
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
