package com.example.budgetlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetailerLoginActivity extends AppCompatActivity {

    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_login);

        //Gets the database reference
        database = FirebaseDatabase.getInstance().getReference();

        //Setting the reference variables for each of the UI elements
        final TextView retailerEmail = findViewById(R.id.RetailerEmail);
        final TextView retailerPassword = findViewById(R.id.RetailerPassword);
        TextView retailerLoginBanner = findViewById(R.id.RetailerLoginBanner);
        Button retailerSubmitButton = findViewById(R.id.RetailerSubmitButton);
        Button retailerCreateAccount = findViewById(R.id.RetailerCreateAccount);
        TextView retailerForgotPassword = findViewById(R.id.RetailerForgotPassword);
        Button shopperLogin = findViewById(R.id.ShopperLogin);

        //Setting the text for the ui elements
        final String retaileremail = "Email:";
        String retailerpassword = "Password:";
        String retailerloginbanner = "Retailer Login";
        String retailersubmitbutton = "Submit";
        String retailercreateaccount = "Create Account";
        String retailerforgotpassword = "Forgot Password";
        String shopperlogin = "Shoppers";

        retailerEmail.setHint(retaileremail);
        retailerPassword.setHint(retailerpassword);
        retailerLoginBanner.setText(retailerloginbanner);
        retailerSubmitButton.setText(retailersubmitbutton);
        retailerCreateAccount.setText(retailercreateaccount);
        retailerForgotPassword.setText(retailerforgotpassword);
        shopperLogin.setText(shopperlogin);

        //Setting the edit text references
        final TextView emailText = findViewById(R.id.EmailEditText);
        final TextView passwordText = findViewById(R.id.PasswordEditText);

        //Setting up the create account button listener to change activities for the retailer
        retailerCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RetailerLoginActivity.this, RetailerCreateAccountActivity.class));
            }
        });

        //Setting up the retailer login button
        retailerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The database is unable to hold periods and therefore must be replaced with commas
                String retEmail = retailerEmail.getText().toString();
                retEmail = retEmail.replaceAll("\\.", ",");
                String pass = retailerPassword.getText().toString();

                retailerCheckEmail(retEmail, pass);
                startActivity(new Intent(RetailerLoginActivity.this, Retailer_Home.class));
            }
        });

        //Setting up the shopper button, that changes to the shopper login activity
        shopperLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RetailerLoginActivity.this, MainActivity.class));
            }
        });
    }

    //Verifies that the emails exists in the database
    private void retailerCheckEmail(final String e, final String p){
        database.child("Retailers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If the email exists, it checks the password
                if(dataSnapshot.hasChild(e)){
                    retailerCheckPassword(e, p);
                }
                else{
                    Log.d("msg", "Invalid Email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Verifies that the password associated with the given email is correct
    private void retailerCheckPassword(final String e, final String p){
        database.child("Retailers").child(e).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If the password is correct it logs the user in
                if(dataSnapshot.child("Password").getValue().equals(p)){
                    Log.d("msg", "Login Successful");
                }
                else{
                    Log.d("msg", "Invalid Password");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
