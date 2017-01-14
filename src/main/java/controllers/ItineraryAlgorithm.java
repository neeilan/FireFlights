package controllers;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.Flight;
import models.FlightGraph;
import models.Itinerary;

/**
 * The Itinerary algorithm.
 * @author Neeilan
 *
 */
/**
 * The Itinerary algorithm.
 * @author Neeilan
 *
 */
public class ItineraryAlgorithm {
  private String origin;
  private String destination;
  private FlightGraph graph;
  private List<Itinerary> returnList;
  private List<Itinerary> results;
  private Duration minLayover;
  private Duration maxLayover;
  private Date firstDepDate;


  /**
   * @param graph The Flightgraph with all the flights.
   * @param origin origin.
   * @param destination the dest.
   * @param firstDepDate the date first flight must leave on.
   * @param minLo minimum layover duration.
   * @param maxLo max layover duraiton.
   */
  public ItineraryAlgorithm(FlightGraph graph, String origin, String destination, Date firstDepDate,
                            Duration minLo, Duration maxLo) {
    this.graph = graph;
    this.origin = origin;
    this.destination = destination;
    this.minLayover = minLo;
    this.maxLayover = maxLo;
    this.firstDepDate = firstDepDate;

  }

  /**
   * Finds the itineraries matching the query.
   * @return the list of itineraries matching the query provided at initalization
   */
  public List<Itinerary> findItineraries() {
    this.returnList = new ArrayList<>();
    this.results = new ArrayList<>();
    populateItineraries(null, new TreeSet<String>());

    for (Itinerary it : results) {
      if (sameDay(it.getFlights().get(0).getDepartsAt(), firstDepDate)){
        returnList.add(it);
      }
    }
    return returnList;
  }

  private boolean sameDay(Date d1, Date d2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(d1);
    cal2.setTime(d2);
    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    return sameDay;
  }

  private Object populateItineraries(Flight currFlight, Set<String> visited) {
    if (currFlight == null) {
      // first call : find possible paths via all flights from the origin
      for (Flight flight : this.graph.getFlightsFromCity(origin)){
        populateItineraries(flight, new TreeSet<String>());
      }
    } else if (currFlight.getDestination().equals(destination)) {
      // this flight takes us to the right place
      Itinerary it = new Itinerary(currFlight);

      if (currFlight.getOrigin().equals(origin)) {
        // if direct flight add it in
        results.add(it);
      } else {
        // otherwise return the length 1 itinerary that goes to the correct place
        return it;
      }
    } else if (visited.contains(currFlight.getDestination())) {
      // don't want cycles
      return null;
    } else {
      // this isn't our destination, but one of the flights from here might lead us there
      visited.add(currFlight.getOrigin()); // to prevent cycles

      List<Flight> destinations = graph.getFlightsFromCity(currFlight.getDestination());
      for (Flight flight : destinations) {
        Itinerary returnedItinerary =
                (Itinerary) populateItineraries(flight, new TreeSet<String>(visited));

        if (returnedItinerary != null) {
          // if this is non-null, it means this itinerary takes us to
          // the destination. It may be of length 1 or higher

          Duration wait = Duration.between(Instant.ofEpochMilli(currFlight.getArrivesAt().getTime()),
                  Instant.ofEpochMilli(returnedItinerary.getFlights().get(0).getDepartsAt().getTime()));

          boolean returnedItineraryIsValid =
                  (!wait.minus(minLayover).isNegative() && !maxLayover.minus(wait).isNegative());

          Itinerary mergedItinerary =
                  mergeItineraries(new Itinerary(currFlight), returnedItinerary);
          if (returnedItineraryIsValid && mergedItinerary.getOrigin().equals(origin)
                  && mergedItinerary.getDestination().equals(destination)) {
            // great success
            results.add(mergedItinerary);
          } else if (returnedItineraryIsValid) {
            // we haven't traced our way back to the origin yet, but the last 1 or more flights are
            // valid
            // so pass it back to calling function to deal with it
            return mergedItinerary;
          }
        }
      }
    }
    return null; // default return value if no valid paths are found
  }

  private Itinerary mergeItineraries(Itinerary i1, Itinerary i2) {
    List<Flight> fl1 = i1.getFlights();
    fl1.addAll(i2.getFlights());
    Itinerary it = new Itinerary();
    for (Flight flight : fl1){
      it.addFlight(flight);
    }
    return it;
  }

}

