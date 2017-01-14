package cs.b07.cscb07courseproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import models.Flight;
import models.Itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Neeilan on 11/28/2016.
 */
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>{


    List<Itinerary> itineraries = new ArrayList<>();
    Context parentContext;

    public ItineraryAdapter(Context parentContext, List<Itinerary> itineraries){
        this.itineraries = itineraries;
        this.parentContext = parentContext;
    }

    @Override
    public ItineraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_itinerary,parent, false);
        ItineraryViewHolder holder = new ItineraryViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItineraryViewHolder holder, int position) {
        Itinerary it = itineraries.get(position);
        List<Flight> flights = it.getFlights();
        if (flights.isEmpty()) {return;}
        Flight firstFlight = flights.get(0);
        Flight lastFlight = flights.get(flights.size()-1);
        String[] departureDateTime = Utils.dateTimeFormat.format(firstFlight.getDepartsAt()).split(" ");
        String[] arrivalDateTime = Utils.dateTimeFormat.format(lastFlight.getArrivesAt()).split(" ");
        String departureDate = departureDateTime[0];
        String departureTime = departureDateTime[1];
        String arrivalDate = arrivalDateTime[0];
        String arrivalTime = arrivalDateTime[1];
        String itineraryDuration = String.format("%.2f hours", it.travelTimeMins()/60.0);
        String stopsString = flights.size() == 2 ? "1 stop" : Integer.toString(flights.size()-1)+" stops";

        holder.itineraryOrigin.setText(flights.get(0).getOrigin());
        holder.itineraryDestination.setText(flights.get(flights.size()-1).getDestination());
        holder.itineraryCost.setText(String.format("$%.2f",it.travelCost()));
        holder.itineraryDepartureDate.setText(departureDate);
        holder.itineraryArrivalDate.setText(arrivalDate);
        holder.itineraryNumStops.setText(stopsString);
        holder.itineraryDepartureTime.setText(departureTime);
        holder.itineraryArrivalTime.setText(arrivalTime);
        holder.itineraryDuration.setText(itineraryDuration);

        for (Flight flight : flights) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Flights").child(flight.getFlightNumber())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,Object> newFlight = (HashMap) dataSnapshot.getValue();
                            if (newFlight == null || !newFlight.containsKey("numSeats")) {return;}
                            double cost =  Double.parseDouble(newFlight.get("cost").toString());
                            int seatsLeft = Integer.parseInt(newFlight.get("numSeats").toString());
                            flight.setCost(cost);
                            holder.itineraryCost.setText(String.format("$%.2f",it.travelCost()));

//                            if (parentContext instanceof  SearchActivity){
//                                ((SearchActivity)parentContext).onSearchFlightsClicked(null);
//                            }

                        }




                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }


        holder.card.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentContext, BookItineraryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Itinerary", itineraries.get(position));
                parentContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
    }

    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        ViewGroup card;
        TextView itineraryOrigin, itineraryDestination, itineraryDepartureDate,
        itineraryDepartureTime, itineraryArrivalDate, itineraryArrivalTime, itineraryNumStops,
        itineraryDuration,itineraryCost;
        public ItineraryViewHolder(View v){
            super(v);
            card = (ViewGroup) v.findViewById(R.id.itineraryCard);
            itineraryOrigin = (TextView) v.findViewById(R.id.itineraryOrigin);
            itineraryDestination = (TextView) v.findViewById(R.id.itineraryDestination);
            itineraryDepartureDate = (TextView) v.findViewById(R.id.itineraryDepartureDate);
            itineraryDepartureTime = (TextView) v.findViewById(R.id.itineraryDepartureTime);
            itineraryArrivalDate = (TextView) v.findViewById(R.id.itineraryArrivalDate);
            itineraryArrivalTime = (TextView) v.findViewById(R.id.itineraryArrivalTime);
            itineraryNumStops = (TextView) v.findViewById(R.id.itineraryNumStops);
            itineraryDuration = (TextView) v.findViewById(R.id.itineraryDuration);
            itineraryCost = (TextView) v.findViewById(R.id.itineraryCost);
        }
    }
}
