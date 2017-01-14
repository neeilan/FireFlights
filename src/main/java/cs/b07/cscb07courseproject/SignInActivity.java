package cs.b07.cscb07courseproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private Button signInButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            finish();
            Intent intent = new Intent(getApplicationContext(), BillingInfoActivity.class);
            startActivity(intent);
            //  user already signed in - start next activity
        }

        signInButton = (Button) findViewById(R.id.buttonSignUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void onDontHaveAccountClicked(View v){
        finish();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onSignInClicked(View v){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        signInUser(email, password);
    }

    private void signInUser(String email, String password){
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.hide();
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()){
                            // user signed in successfully

                            String userNodeKey = Utils.getDbKeyFromEmail(firebaseAuth.getCurrentUser().getEmail());
                            databaseReference.child("Users").child(userNodeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null){
                                        Toast.makeText(getApplicationContext(), "Please set your billing information to continue", Toast.LENGTH_LONG).show();
                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), BillingInfoActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class); //change this
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                        else {
                            Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
