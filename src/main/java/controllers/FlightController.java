package controllers;

import android.renderscript.Sampler;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs.b07.cscb07courseproject.Utils;
import models.DataStore;
import models.Flight;
import models.FlightFile;

public class FlightController {
  private DataStore<Flight> flightData;

  public FlightController() {
    super();
  }

  /**
   * Takes in the origin, destination, and date to return a List matching parameters set by
   * user.
   * 
   * @param origin
   * @param destination
   * @param date
   * @return List<Flights> specified
   */
  public List<Flight> searchFlights(String origin, String destination, Date date) {

    // create an instance of a new (empty) list of flights
    List<Flight> listOfFlights = new ArrayList<>();
    // create an instance of a list containing all flights
    List<Flight> allFlights = flightData.findAll();
    // iterate over all flights and see which ones match specified by user
    for (Flight flight : allFlights) {
      if (flight.getOrigin().equals(origin) && flight.getDestination().equals(destination)
          && sameDay(flight.getDepartsAt(), date))
        listOfFlights.add(flight);
    }
    return listOfFlights;
  }

  /**
   * This method, unlike the one above, takes no parameters and returns a list of all flights.
   * 
   * @return a list with all flights.
   */
  public List<Flight> searchFlights() {
    return flightData.findAll();
  }

  /**
   * Helper method used by searchFlights(origin, destination, date)
   * 
   * @param d1 the first date..
   * @param d2 the second date.
   * @return boolean whether they are on the same day.
   */
  @SuppressWarnings("deprecation")
  private boolean sameDay(Date d1, Date d2) {
    return d1.getMonth() == d2.getMonth() && d1.getYear() == d2.getYear()
        && d1.getDate() == d2.getDate();
  }

  public void retrieveAllFlights(ValueEventListener callback){
    FirebaseDatabase.getInstance().getReference().child("Flights")
        .addListenerForSingleValueEvent(callback);
  }

  public void setDataSource(FlightFile flightFile) {
    this.flightData = flightFile;
  }
}
