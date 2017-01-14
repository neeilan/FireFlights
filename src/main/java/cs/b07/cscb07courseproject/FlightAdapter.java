package cs.b07.cscb07courseproject;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import models.Flight;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Neeilan on 11/28/2016.
 */
public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder>{

    List<Flight> flights = new ArrayList<>();
    Context parentContext;

    public FlightAdapter(Context parentContext, List<Flight> itineraries){
        this.parentContext = parentContext;
        this.flights = itineraries;
    }

    @Override
    public FlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_flight,parent, false);
        FlightViewHolder holder = new FlightViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        String[] departureDateTime = Utils.dateTimeFormat.format(flight.getDepartsAt()).split(" ");
        String[] arrivalDateTime = Utils.dateTimeFormat.format(flight.getArrivesAt()).split(" ");
        String departureDate = departureDateTime[0];
        String departureTime = departureDateTime[1];
        String arrivalDate = arrivalDateTime[0];
        String arrivalTime = arrivalDateTime[1];
        String flightNum = flight.getFlightNumber();

        holder.flightOrigin.setText(flight.getOrigin());
        holder.flightDestination.setText(flight.getDestination());
        holder.flightDepartureDate.setText(departureDate);
        holder.flightArrivalDate.setText(arrivalDate);
        holder.flightDepartureTime.setText(departureTime);
        holder.flightArrivalTime.setText(arrivalTime);
        holder.flightAirline.setText(flight.getAirline());
        holder.flightNum.setText(flightNum);
        holder.flightNumSeats.setText(Integer.toString((int)flight.getNumSeats())+ " seats left");


        if (position == 0){
            holder.flightOrigin.setTextColor(Color.parseColor("#28B463"));
        }
        if (position == flights.size()-1){
            holder.flightDestination.setTextColor(Color.parseColor("#28B463"));
        }

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Flights")
                .child(flightNum)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> newFlight = (HashMap) dataSnapshot.getValue();
                if (newFlight == null || !newFlight.containsKey("numSeats")) {return;}
                int seatsLeft = (int)(long) newFlight.get("numSeats");
                double cost =  Double.parseDouble(newFlight.get("cost").toString());
                holder.flightNumSeats.setText(Integer.toString(seatsLeft)+ " seats left");

                flights.get(position).setNumSeats(seatsLeft);
                flights.get(position).setCost(cost);
                if (parentContext instanceof  BookItineraryActivity){
                    ((BookItineraryActivity) parentContext).refreshItineraryCost();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {
        ViewGroup card;
        TextView flightOrigin, flightDestination, flightDepartureDate,
        flightDepartureTime, flightArrivalDate, flightArrivalTime, flightNumSeats,
                flightNum,flightAirline;
        public FlightViewHolder(View v){
            super(v);
            card = (ViewGroup) v.findViewById(R.id.flightCard);
            flightOrigin = (TextView) v.findViewById(R.id.flightOrigin);
            flightDestination = (TextView) v.findViewById(R.id.flightDestination);
            flightDepartureDate = (TextView) v.findViewById(R.id.flightDepartureDate);
            flightDepartureTime = (TextView) v.findViewById(R.id.flightDepartureTime);
            flightArrivalDate = (TextView) v.findViewById(R.id.flightArrivalDate);
            flightArrivalTime = (TextView) v.findViewById(R.id.flightArrivalTime);
            flightNumSeats = (TextView) v.findViewById(R.id.flightSeatsNum);
            flightNum = (TextView) v.findViewById(R.id.flightFlightNum);
            flightAirline = (TextView) v.findViewById(R.id.flightAirline);
        }
    }
}
