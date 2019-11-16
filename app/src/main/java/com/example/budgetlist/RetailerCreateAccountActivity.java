package com.example.budgetlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetailerCreateAccountActivity extends AppCompatActivity {

    TextView firstName;
    TextView lastName;
    TextView emailText;
    TextView passwordText;
    TextView retailerReEnterPassword;
    TextView retailerPasswordHint;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_create_account);

        database = FirebaseDatabase.getInstance().getReference();

        firstName = findViewById(R.id.RetailerFirstName);
        lastName = findViewById(R.id.RetailerLastName);
        emailText = findViewById(R.id.RetailerEmail);
        passwordText = findViewById(R.id.RetailerPassword);
        retailerReEnterPassword = findViewById(R.id.ReEnterPassword);
        retailerPasswordHint = findViewById(R.id.RetailerPasswordHint);
        Button retailerCreateAccount = findViewById(R.id.RetailerCreateAccountButton);

        String enterpassword = "Enter Password";
        String enteremail = "Enter Email";
        String retailerreenterpassword = "Re-Enter Password";
        String retailerpasswordhint = "Password Hint";
//        passwordText.setHint(enterpassword);
//        emailText.setHint(enteremail);
//        retailerReEnterPassword.setHint(retailerreenterpassword);
//        retailerPasswordHint.setHint(retailerpasswordhint);

        retailerCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                email = email.replaceAll("\\.", ",");

                if (ValidInputs()) {
                    CheckUsername(email);
                }
            }
        });

    }

    private void AddAccount(String email){
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String password = passwordText.getText().toString();

        database.child("Retailers").child(email).child("First Name").setValue(fName);
        database.child("Retailers").child(email).child("Last Name").setValue(lName);
        database.child("Retailers").child(email).child("Password").setValue(password);

        startActivity(new Intent(RetailerCreateAccountActivity.this, RetailerLoginActivity.class));
    }

    private boolean ValidInputs(){
        if(firstName.getText().toString().isEmpty()){
            return false;
        }
        if(lastName.getText().toString().isEmpty()){
            return false;
        }
        if(emailText.getText().toString().isEmpty()){
            return false;
        }
        return !passwordText.getText().toString().isEmpty();
    }

    private void CheckUsername(final String email){
        database.child("Retailers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(email)){
                    AddAccount(email);
                }
                else{
                    //Email found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
