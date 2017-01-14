package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import models.Flight;

public class FlightGraph {
	private TreeSet<String> cities = new TreeSet<>();
	private HashMap<String, List<Flight>> departures = new HashMap<>();
	
	public FlightGraph(){
		super();
	}
		
	public void addFlight(Flight flight){	
		String origin = flight.getOrigin();
		String destination = flight.getDestination();
		cities.add(origin);
		cities.add(destination);
		if (!departures.containsKey(origin)){
			departures.put(origin, new ArrayList<Flight>());
		}
		departures.get(origin).add(flight);
	}
	
	public List<Flight> getFlightsFromCity(String city){
		if (departures.containsKey(city)){
			return departures.get(city);
		}
		else{
			return new ArrayList<Flight>();
		}
	}

}
