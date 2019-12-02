package com.example.budgetlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the reference variables for each of the UI elements
        final TextView Email = findViewById(R.id.Email);
        final TextView Password = findViewById(R.id.password);
        TextView budgetList = findViewById(R.id.budgetList);
        Button submitButton = findViewById(R.id.SubmitButton);
        Button createAccount = findViewById(R.id.CreateAccount);
        TextView forgotPassword = findViewById(R.id.ForgotPassword);
        Button retailerLoginAct_ = findViewById(R.id.RetailerLoginAct_);

        database = FirebaseDatabase.getInstance().getReference();

        //Setting the text for the ui elements
        String email = "Email:";
        String password = "Password:";
        String budgetlist = "BudgetList";
        String submitbutton = "Submit";
        String createaccount = "Create Account";
        String forgotpassword = "Forgot Password";
        String retailerloginact_ = "Retailers";

        Email.setText(email);
        Password.setText(password);
        budgetList.setText(budgetlist);
        submitButton.setText(submitbutton);
        createAccount.setText(createaccount);
        forgotPassword.setText(forgotpassword);
        retailerLoginAct_.setText(retailerloginact_);

        //Setting the edit text references
        final TextView emailText = findViewById(R.id.EmailEditText);
        final TextView passwordText = findViewById(R.id.PasswordEditText);

        //Setting up the create account button listener to change activities
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAccount.class));
            }
        });

        //Setting up the submit account button listener, it retrieves the username and password
        //from the edit texts and checks it against the database
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailText.getText().toString();
                userEmail = userEmail.replaceAll("\\.", ",");
                Log.d("msg", userEmail);
                String pass = passwordText.getText().toString();

                CheckEmail(userEmail, pass);
            }
        });

        //Setting up the retailer login button listener to change activities
        retailerLoginAct_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RetailerLoginActivity.class));
            }
        });
    }

    //Checks in the database whether the email exists
    private void CheckEmail(final String e, final String p){
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(e)){
                    Log.d("msg", "Found Email");
                    CheckPassword(e, p);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    Log.d("msg", "Invalid Email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msg", databaseError.toString());
            }
        });
    }

    //Checks in the database whether the password associated with the email is correct
    //If correct it logs the user in and changes the activity
    private void CheckPassword(final String e, final String p){
        database.child("Users").child(e).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Password").getValue().equals(p)){
                    Log.d("msg", "Login Successful");
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    Log.d("msg", "Invalid Password");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msg", databaseError.toString());
            }
        });
    }
}
