package cs.b07.cscb07courseproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ViewFlightsActivity extends AppCompatActivity {

    private DatabaseReference flightsReference;
    private ListView flightsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flights);

        flightsReference = FirebaseDatabase.getInstance().getReference().child("Flights");
        flightsList = (ListView) findViewById(R.id.flightsList);
        populateFlightsList();
    }

    private void populateFlightsList(){
        flightsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,HashMap<String, String>> dataMap = (HashMap)dataSnapshot.getValue();
                ArrayList<String> flightNums = new ArrayList<String>();
                if (dataMap == null) {
                    Toast.makeText(getApplicationContext(), "No clients found.", Toast.LENGTH_LONG);
                    return;}

                for (String flightNum : dataMap.keySet()) {
                    flightNums.add(flightNum);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, flightNums) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.DKGRAY);
                        return view;
                    }
                };
                flightsList.setAdapter(adapter);
                flightsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String flightNum = (String) flightsList.getItemAtPosition(position);
                        // start the flight info editing activity
                        Intent intent = new Intent(getApplicationContext(), FlightInfoActivity.class);
                        intent.putExtra("flightNum", flightNum);
                        startActivity(intent);
                        finish();
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        flightsList = (ListView) findViewById(R.id.flightsList);
    }

    public void onAddFlightClicked(View v){
        Intent intent = new Intent(this, UploadClientsActivity.class); // actually for uploading both
        startActivity(intent);
    }

}
