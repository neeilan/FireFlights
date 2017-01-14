package models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Itinerary implements Serializable {
	private static DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private List<Flight> flights = new ArrayList<Flight>();
	//private User owner;
	
	public Itinerary() {
		super();
	}
	
	public Itinerary(Flight flight) {
		super();
		this.addFlight(flight);
	}
	
	public void addFlight(Flight flight) {
		//add flight to itinerary flight list.
		if (flight!=null) {
			flights.add(flight);
		}
	}
	
	public double travelTimeMins() {

		//add up flight travel times
		return Duration.between(Instant.ofEpochMilli(flights.get(0).getDepartsAt().getTime()),
                Instant.ofEpochMilli(flights.get(flights.size()-1).getArrivesAt().getTime()))
                .toMinutes();
	}
	
	public double travelCost() {
		double totalTravelCost = 0;
		//add up flight costs
		for (Flight i : flights) {
			totalTravelCost += i.getCost();
		}
		return totalTravelCost;
	}
	
	public static List<Itinerary> sortByTravelTime(List<Itinerary> itineraryList) {
		itineraryList = new ArrayList<Itinerary>(itineraryList);
		//sort the list of flights by travel times
		Collections.sort(itineraryList, getTimeComparator());
		return itineraryList;
	}
	
	public static List<Itinerary> sortByCost(List<Itinerary> itineraryList) {
		//sort the list of flights by travel cost
		Collections.sort(itineraryList, getCostComparator());
		return itineraryList;
	}
	
	private static Comparator<Itinerary> getCostComparator() {
		return new Comparator<Itinerary>() {
			public int compare(Itinerary i1, Itinerary i2) {
				return (int)(i1.travelCost() - i2.travelCost());
			}
		};
	}

	private static Comparator<Itinerary> getTimeComparator() {
		return new Comparator<Itinerary>() {
			public int compare(Itinerary i1, Itinerary i2) {
				return (int)(i1.travelTimeMins() - i2.travelTimeMins());
			}
		};
	}

	public String summaryString(){
		String result = "";
		if (flights.size() == 0) {
			return result;
		}
		result = result.concat(flights.get(0).getFlightNumber());
		if (flights.size()>1) {
			for (int i = 1; i < flights.size(); i++ ) {
				Flight flight = flights.get(i);
				result = result.concat("_" + flight.getFlightNumber());
			}
		}
		return result;
	}

	public boolean seatsAvailable(){
		for (Flight flight : flights) {
			if (flight.getNumSeats() <= 0){
				return false;
			}
		}
		return true;
	}

	public String toCsv() {
		String result = "";
		for (Flight flight : flights){
			result = result.concat(createFlightString(flight)+"\n");
		}
		result+= String.format("%.2f", this.travelCost());
		result += String.format("\n%.2f", this.travelTimeMins()/60.0);
		return result;
	}
	
	private String createFlightString(Flight flight){
		return String.format("%s;%s;%s;%s;%s;%s", 
		        flight.getFlightNumber(), dateTime.format(flight.getDepartsAt()), dateTime.format(flight.getArrivesAt()),
		        flight.getAirline(), flight.getOrigin(), flight.getDestination());
	}

	public static String[] getFlightNumsFromSummaryString(String summaryString){
		return summaryString.split("_");
	}
	public List<Flight> getFlights(){
		return flights;
	}
	
	public String getOrigin(){
		return flights.get(0).getOrigin();
	}
	
	public String getDestination(){
		return flights.get(flights.size()-1).getDestination();
	}
	
	public int getNumFlights(){
		return flights.size();
	}

	public static List<Itinerary> getBookableItineraries(List<Itinerary> matchingItineraries, HashMap<String, String> usersItineraries){
		ArrayList<Itinerary> itinerariesToDisplay = new ArrayList<Itinerary>();
		if (usersItineraries != null) {
			for (Itinerary matchingItinerary : matchingItineraries) {
				if (matchingItinerary.seatsAvailable()
						&& !usersItineraries.containsValue(matchingItinerary.summaryString())) {
					itinerariesToDisplay.add(matchingItinerary);
				}
			}
			return itinerariesToDisplay;
		}
		else{
			for (Itinerary matchingItinerary : matchingItineraries) {
				if (matchingItinerary.seatsAvailable()) {
					itinerariesToDisplay.add(matchingItinerary);
				}
			}
			return itinerariesToDisplay;
		}
	}
	
	
	
}