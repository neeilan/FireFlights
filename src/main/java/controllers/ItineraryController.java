package controllers;

import android.renderscript.Sampler;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.Duration;
import java.util.Date;
import java.util.List;

import cs.b07.cscb07courseproject.Utils;
import models.DataStore;
import models.Flight;
import models.FlightGraph;
import models.Itinerary;

public class ItineraryController {
  //  private DataStore<Itinerary> itineraryData;
  private FlightGraph flightGraph = new FlightGraph();
  private Duration minLayover = Duration.ofMinutes(0);
  private Duration maxLayover = Duration.ofMinutes(60 * 24);

  public ItineraryController() {
    super();
  }

  public void setDataSource(DataStore<Itinerary> dataStore) {
    //    this.itineraryData = dataStore;
  }

  public void setMinLayover(Duration minLayover) {
    this.minLayover = minLayover;
  }

  public void setMaxLayover(Duration maxLayover) {
    this.maxLayover = maxLayover;
  }

  /**
   * Searches for itineraries matching given params.
   * @param flightController the flightController
   * @param origin the origin
   * @param destination the destination
   * @param date the date first flight should depart on
   * @return the list of matching itineraries.
   */
  public List<Itinerary> search(FlightController flightController, String origin,
      String destination, Date date) {
    for (Flight flight : flightController.searchFlights()) {
      this.flightGraph.addFlight(flight);
    }
    ItineraryAlgorithm algorithm =
        new ItineraryAlgorithm(this.flightGraph, origin, destination, date, minLayover, maxLayover);
    return algorithm.findItineraries();

  }

  public void retrieveBookedItinerariesForUser(String currentUserEmail, ValueEventListener callback){
    FirebaseDatabase.getInstance().getReference().child("BookedItineraries")
            .child(Utils.getDbKeyFromEmail(currentUserEmail))
            .addListenerForSingleValueEvent(callback);
  }

}
