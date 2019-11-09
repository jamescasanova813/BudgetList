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
        final TextView retailerEmail = findViewById(R.id.RetailerEmail);
        final TextView retailerPassword = findViewById(R.id.RetailerPassword);
        TextView retailerLoginBanner = findViewById(R.id.RetailerLoginBanner);
        TextView retailerSubmitButton = findViewById(R.id.RetailerSubmitButton);
        Button retailerCreateAccount = findViewById(R.id.RetailerCreateAccount);
        TextView retailerForgotPassword = findViewById(R.id.RetailerForgotPassword);
        database = FirebaseDatabase.getInstance().getReference();
        Button shopperLogin = findViewById(R.id.ShopperLogin);

        String retaileremail = "Email:";
        String retailerpassword = "Password:";
        String retailerloginbanner = "Retailer Login";
        String retailersubmitbutton = "Submit";
        String retailercreateaccount = "Create Account";
        String retailerforgotpassword = "Forgot Password";
        String shopperlogin = "Shoppers";

        retailerEmail.setText(retaileremail);
        retailerPassword.setText(retailerpassword);
        retailerLoginBanner.setText(retailerloginbanner);
        retailerSubmitButton.setText(retailersubmitbutton);
        retailerCreateAccount.setText(retailercreateaccount);
        retailerForgotPassword.setText(retailerforgotpassword);
        shopperLogin.setText(shopperlogin);

        // testing home page
        // homebutton.setText(homebutton);

        final TextView emailText = findViewById(R.id.EmailEditText);
        final TextView passwordText = findViewById(R.id.PasswordEditText);

        retailerCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RetailerLoginActivity.this, RetailerCreateAccountActivity.class));
            }
        });


        retailerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String retailerEmail = emailText.getText().toString();
                retailerEmail = retailerEmail.replaceAll("\\.", ",");
                Log.d("msg", retailerEmail);
                String pass = passwordText.getText().toString();

                retailerCheckEmail(retailerEmail, pass);
                startActivity(new Intent(RetailerLoginActivity.this, Home.class)); // Todo: Change to the home to retailer home
            }
        });

        shopperLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RetailerLoginActivity.this, MainActivity.class));
            }
        });
    }

    private void retailerCheckEmail(final String e, final String p){
        database.child("Retailers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    private void retailerCheckPassword(final String e, final String p){
        database.child("Retailers").child(e).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
