package controllers;

import java.text.ParseException;
import org.threeten.bp.Duration;
import java.util.Date;
import java.util.List;

import exceptions.UserNotFoundException;
import models.Flight;
import models.FlightFile;
import models.Itinerary;
import models.UserFile;

public class Router {
  private UserController userController;
  private FlightController flightController;
  private ItineraryController itineraryController;
  private static String[] adminEmails = {"neeilans@live.com", "tito@ku.com"};

  /**
   * A main router class that coordinates the controllers.
   */
  public Router() {
    userController = new UserController();
    flightController = new FlightController();
    itineraryController = new ItineraryController();
  }

  public UserController getUserController() {
    return userController;
  }

  public FlightController getFlightController() {
    return flightController;
  }

  public ItineraryController getItineraryController() {
    return itineraryController;
  }


  // required delegate methods for phase 2
  public void uploadUserInfo(String path) {
    userController.setDataSource(new UserFile(path));
  }

  public void uploadFlightInfo(String path) {
    flightController.setDataSource(new FlightFile(path));
  }

  public static boolean isAdmin(String email){
    for (int i = 0; i < adminEmails.length; i++){
      if (adminEmails[i].equals(email))
        return true;
    }
    return false;
  }

  /**
   * Gets a users string.
   * 
   * @param email the email.
   * @return the user's string representation.
   */
  public String getUser(String email) {
    try {
      return userController.findByEmail(email);
    } catch (UserNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<Flight> getFlights(Date date, String origin, String destination)
      throws ParseException {
    return flightController.searchFlights(origin, destination, date);
  }

  public List<Itinerary> getItineraries(Date date, String origin, String destination) {
    return itineraryController.search(flightController, origin, destination, date);
  }

  /**
   * Just what it sounds like.
   * 
   * @param date date.
   * @param origin origin.
   * @param destination destination.
   * @return list of itineraries in stirng representation.
   */
  public List<Itinerary> getItinerariesSortedByCost(Date date, String origin, String destination) {
    return Itinerary
        .sortByCost(itineraryController.search(flightController, origin, destination, date));
  }

  /**
   * Just what it sounds like.
   * 
   * @param date date.
   * @param origin origin.
   * @param destination destination.
   * @return list of itineraries in stirng representation.
   */
  public List<Itinerary> getItinerariesSortedByTime(Date date, String origin, String destination) {
    return Itinerary
        .sortByTravelTime(itineraryController.search(flightController, origin, destination, date));
  }

  public void setMinLayover(Duration lo) {
    itineraryController.setMinLayover(lo);
  }

  public void setMaxLayover(Duration lo) {
    itineraryController.setMaxLayover(lo);
  }
}
