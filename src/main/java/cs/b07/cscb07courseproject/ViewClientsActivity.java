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

import java.util.ArrayList;
import java.util.HashMap;

public class ViewClientsActivity extends AppCompatActivity {

    private DatabaseReference UsersReference;
    private ListView clientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_clients);

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        clientsList = (ListView) findViewById(R.id.clientsList);
        populateClientsList();

    }

    private void populateClientsList(){
        UsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,HashMap<String, String>> dataMap = (HashMap)dataSnapshot.getValue();
                ArrayList<String> userEmails = new ArrayList<String>();

                if (dataMap == null) {
                    Toast.makeText(getApplicationContext(), "No clients found.", Toast.LENGTH_LONG);
                    return;}

                for (String email : dataMap.keySet()) {
                    String emailStr = email.replaceAll("DOT",".");
                    userEmails.add(emailStr);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, userEmails) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.DKGRAY);
                        return view;
                    }
                };
                clientsList.setAdapter(adapter);
                clientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String email = (String) clientsList.getItemAtPosition(position);
                        // start the billing info editing activity
                        String userNodeKey = Utils.getDbKeyFromEmail(email);
                        Intent intent = new Intent(getApplicationContext(), BillingInfoActivity.class);
                        intent.putExtra("userNodeKey", userNodeKey);
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

    public void onAddClientClicked(View v){
        Intent intent = new Intent(this, UploadClientsActivity.class);
        startActivity(intent);
    }
}
