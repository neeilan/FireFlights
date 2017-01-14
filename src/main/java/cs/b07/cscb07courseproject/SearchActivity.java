package cs.b07.cscb07courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import controllers.FlightController;
import controllers.ItineraryAlgorithm;
import controllers.Router;
import models.Flight;
import models.FlightGraph;
import controllers.ItineraryController;
import models.Itinerary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearchOrigin;
    private EditText editTextSearchDestination;
    private DatePicker editTextSearchDate;
    private DatabaseReference databaseReference;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RadioButton radioSortCost;
    private RadioButton radioSortTime;
    private CheckBox checkboxDirect;
    private ItineraryController itineraryControlller;
    private FlightController flightController;
    private List<Itinerary> renderedResults = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextSearchOrigin = (EditText) findViewById(R.id.editTextSearchOrigin);
        editTextSearchDestination = (EditText) findViewById(R.id.editTextSearchDestination);
        editTextSearchDate = (DatePicker) findViewById(R.id.editTextSearchDate);
        searchResultsRecyclerView = (RecyclerView) findViewById(R.id.searchResultsRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        radioSortCost = (RadioButton) findViewById(R.id.radioSortCost);
        radioSortTime = (RadioButton) findViewById(R.id.radioSortTime);
        checkboxDirect = (CheckBox) findViewById(R.id.checkboxDirect);
        itineraryControlller = new ItineraryController();
        flightController = new FlightController();

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }else{
            onSearchFlightsClicked(null);
        }
    }

    public void onSearchFlightsClicked(View v){
        String origin = editTextSearchOrigin.getText().toString();
        String destination = editTextSearchDestination.getText().toString();
        String departureDateStr = String.format("%s-%s-%s", String.valueOf(editTextSearchDate.getYear()),
                        String.valueOf(editTextSearchDate.getMonth()+1),
                String.valueOf(editTextSearchDate.getDayOfMonth()));

        if (TextUtils.isEmpty(origin) || TextUtils.isEmpty(destination)
                || TextUtils.isEmpty(departureDateStr)){
            return;
        }

        flightController.retrieveAllFlights(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> flights = (HashMap) dataSnapshot.getValue();
                if (flights == null) {return;}
                FlightGraph flightGraph = Flight.createFlightGraphFromRawData(flights);
                // Find matching itineraries
                ItineraryAlgorithm iAlg = null;
                try {
                    Date departureDate = Utils.dateFormat.parse(departureDateStr);
                    iAlg = new ItineraryAlgorithm(flightGraph, origin, destination,
                            departureDate, Utils.MIN_LAYOVER, Utils.MAX_LAYOVER);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                List<Itinerary> matchingItineraries = iAlg.findItineraries();

                if (matchingItineraries == null ||matchingItineraries.size() == 0 ){
                    notifyNoFlightsFound();
                    return;
                }

                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (Router.isAdmin(currentUserEmail)) {
                    renderResults(matchingItineraries);
                    return;
                }

                //If user isn't an admin don't show booked itineraries in results
                itineraryControlller.retrieveBookedItinerariesForUser(currentUserEmail, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> usersItineraries = (HashMap) dataSnapshot.getValue();
                        List<Itinerary> itinerariesToDisplay =
                                Itinerary.getBookableItineraries(matchingItineraries, usersItineraries);
                        renderResults(itinerariesToDisplay);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {notifyNoFlightsFound();}});
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {notifyNoFlightsFound();}
        });
    }

    private void notifyNoFlightsFound(){
        Toast.makeText(getApplicationContext(), "Sorry... no flights were found", Toast.LENGTH_LONG)
                .show();
    }

    private void renderResults(List<Itinerary> results){
        renderedResults = results;
        if (results.size() == 0) {
            notifyNoFlightsFound();
        }

        if (radioSortCost.isChecked()) {
            results = Itinerary.sortByCost(results);
        }
        else if (radioSortTime.isChecked()) {
            results = Itinerary.sortByTravelTime(results);
        }

        if (checkboxDirect.isChecked()) {
            List<Itinerary> newResults = new ArrayList<>();
            for (Itinerary i : results) {
                if (i.getNumFlights() == 1) {
                    newResults.add(i);
                }
            }
            results = newResults;
        }

        layoutManager = new LinearLayoutManager(getApplicationContext());
        searchResultsRecyclerView.setLayoutManager(layoutManager);
        searchResultsRecyclerView.setHasFixedSize(true);
        adapter = new ItineraryAdapter(getApplicationContext(), results);
        searchResultsRecyclerView.setAdapter(adapter);
    }

    public void refreshResults(View view ) {
        if (renderedResults != null){
            renderResults(renderedResults);
        }
    }

}
