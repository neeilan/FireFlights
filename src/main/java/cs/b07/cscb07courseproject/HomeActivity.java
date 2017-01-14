package cs.b07.cscb07courseproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import controllers.Router;

public class HomeActivity extends AppCompatActivity {

    private ViewGroup manageClients;
    private ViewGroup manageFlights;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        manageClients = (LinearLayout) findViewById(R.id.manageClients);
        manageFlights = (LinearLayout) findViewById(R.id.manageFlights);
        if (!Router.isAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            manageClients.setVisibility(View.GONE);
            manageFlights.setVisibility(View.GONE);
        }

    }

    protected void openViewClients(View v){
        startActivity(new Intent(this, ViewClientsActivity.class));
    }
    protected void openViewFlights(View v){
        startActivity(new Intent(this, ViewFlightsActivity.class));
    }
    protected void openMyAccount(View v){
        startActivity(new Intent(this, BillingInfoActivity.class));
    }
    protected void openFindFlights(View v){
        startActivity(new Intent(this, SearchActivity.class));
    }
    protected void openMyItineraries(View v){
        startActivity(new Intent(this, UserItinerariesActivity.class));
    }
}
