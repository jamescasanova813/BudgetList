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

public class CreateAccount extends AppCompatActivity {

    TextView firstName;
    TextView lastName;
    TextView emailText;
    TextView passwordText;
    TextView ReEnterPassword;
    DatabaseReference database;
    TextView securityQuestion;
    TextView securityAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Gets the database reference
        database = FirebaseDatabase.getInstance().getReference();

        //Setting the reference variables for each of the UI elements
        firstName = findViewById(R.id.FirstName);
        lastName = findViewById(R.id.LastName);
        emailText = findViewById(R.id.Email);
        passwordText = findViewById(R.id.Password);
        securityQuestion = findViewById(R.id.SecurityQuestion);
        securityAnswer = findViewById(R.id.SecurityAnswer);
        ReEnterPassword = findViewById(R.id.ReEnterPassword);
        Button createAccount = findViewById(R.id.createAccountButton);

        //Sets up the create account button listener
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Firebase can't have periods, and therefore they are replaced with commas
                String email = emailText.getText().toString();
                email = email.replaceAll("\\.", ",");

                if(ValidInputs()) {
                    CheckUsername(email);
                }
            }
        });
    }

    //Adds the account using user inputted information
    private void AddAccount(String email){
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String password = passwordText.getText().toString();

        database.child("Users").child(email).child("First Name").setValue(fName);
        database.child("Users").child(email).child("Last Name").setValue(lName);
        database.child("Users").child(email).child("Password").setValue(password);
        database.child("Users").child(email).child("SecurityQuestion").setValue(securityQuestion.getText().toString());
        database.child("Users").child(email).child("SecurityAnswer").setValue(securityAnswer.getText().toString());

        //Changes activities back to the login activity
        startActivity(new Intent(CreateAccount.this, MainActivity.class));
    }

    //Verifies that that none of the text edits are empty
    private boolean ValidInputs(){
        if(firstName.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(lastName.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(emailText.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(securityQuestion.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(securityAnswer.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(passwordText.getText().toString().isEmpty()){
            InvalidInputs("Empty");
            return false;
        }
        if(!passwordText.getText().toString().equals(ReEnterPassword.getText().toString())){
            InvalidInputs("Password");
            return false;
        }
        return true;
    }

    private void InvalidInputs(String error){
        if(error.equals("Password")){
            Toast.makeText(this, "Passwords Don't Match", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Please Fill Out All Fields", Toast.LENGTH_LONG).show();
        }
    }

    //Checks the database to make sure that the email is unique
    private void CheckUsername(final String email){
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(email)){
                    AddAccount(email);
                }
                else{
                    Toast.makeText(CreateAccount.this, "Email Already Exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
