package models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cs.b07.cscb07courseproject.Utils;

/**
 * This is the Flight class that will be used to reading the database and
 * creating Flight objects to be used for searching itineraries.
 * @author Talha
 *
 */
public class Flight implements Serializable {
	private static DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private String origin;
	private String destination;
	private Date arrivesAt;
	private Date departsAt;
	private double cost;
	private String flightNumber;
	private String airline;
	private int numSeats;
	
	/**
	 * Constructor for creating the Flight object.
	 */
	public Flight(String flightNumber, Date departsAt, Date arrivesAt, String airline,
			String origin, String destination, double cost, int numSeats) {
		// use instance variables for creating components for Flight object
		this.flightNumber = flightNumber;
		this.departsAt = departsAt;
		this.arrivesAt = arrivesAt;
		this.airline = airline;
		this.origin = origin;
		this.destination = destination;
		this.cost = cost;
		this.numSeats = numSeats;
	}
	
	// alternate constructor
	public Flight(String flightNumber, String departsAt, String arrivesAt, String airline,
			String origin, String destination, double cost) {
		// use instance variables for creating components for Flight object
		this.flightNumber = flightNumber;
		try {
			this.departsAt = dateTime.parse(departsAt);
			this.arrivesAt = dateTime.parse(arrivesAt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.airline = airline;
		this.origin = origin;
		this.destination = destination;
		this.cost = cost;
	}
	
	
	/**
	 * This method will be used for calculating the travel time of the flight.
	 */
	public double travelTimeMins() {
		long time = this.arrivesAt.getTime() - this.departsAt.getTime();
		long longMinutes = TimeUnit.MILLISECONDS.toMinutes(time);
		return (double)longMinutes;
	}

	public static FlightGraph createFlightGraphFromRawData(HashMap<String, HashMap<String, Object>> flights){
		FlightGraph flightGraph = new FlightGraph();
		for (String flightNum : flights.keySet()) {
			HashMap<String,Object> flightData = flights.get(flightNum);
			Date departsAt = null;
			Date arrivesAt = null;
			int numSeats = 0;
			try {
				departsAt = Utils.dateTimeFormat.parse(flightData.get("departsAt").toString());
				arrivesAt = Utils.dateTimeFormat.parse(flightData.get("arrivesAt").toString());
				numSeats = Integer.parseInt(flightData.get("numSeats").toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Flight f = new Flight(flightNum, departsAt, arrivesAt, flightData.get("airline").toString(),
					flightData.get("origin").toString(),
					flightData.get("destination").toString(),
					Double.parseDouble(flightData.get("cost").toString()),
					(int) numSeats);
			flightGraph.addFlight(f);
		}
		return flightGraph;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @return the arrivesAt
	 */
	public Date getArrivesAt() {
		return arrivesAt;
	}

	/**
	 * @return the departsAt
	 */
	public Date getDepartsAt() { return departsAt; }

	/**
	 * @return the numSeats
	 */
	public double getNumSeats() {
		return numSeats;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}


	/**
	 * @return the flightNumber
	 */
	public String getFlightNumber() {
		return flightNumber;
	}

	/**
	 * @return the airline
	 */
	public String getAirline() {
		return airline;
	}
	
	
	public String toCsv(){
		return String.format("%s;%s;%s;%s;%s;%s;%.2f", 
				flightNumber, dateTime.format(departsAt), dateTime.format(arrivesAt),
		        airline, origin, destination, cost);
	}

	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}