package com.example.budgetlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
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
    TextView passwordHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        database = FirebaseDatabase.getInstance().getReference();

        firstName = findViewById(R.id.FirstName);
        lastName = findViewById(R.id.LastName);
        emailText = findViewById(R.id.Email);
        passwordText = findViewById(R.id.Password);
        passwordHint = findViewById(R.id.PasswordHint);
        ReEnterPassword = findViewById(R.id.ReEnterPassword);
        Button createAccount = findViewById(R.id.createAccountButton);


        String enterpassword = "Enter Password";
        String enteremail = "Enter Email";
        String reenterpassword = "Re-Enter Password";
        String passwordhint = "Password Hint";
        passwordText.setHint(enterpassword);
        emailText.setHint(enteremail);
        ReEnterPassword.setHint(reenterpassword);
        passwordHint.setHint(passwordhint);


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                email = email.replaceAll("\\.", ",");

                if(ValidInputs()) {
                    CheckUsername(email);
                }
            }
        });
    }

    private void AddAccount(String email){
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String password = passwordText.getText().toString();

        database.child("Users").child(email).child("First Name").setValue(fName);
        database.child("Users").child(email).child("Last Name").setValue(lName);
        database.child("Users").child(email).child("Password").setValue(password);

        startActivity(new Intent(CreateAccount.this, MainActivity.class));
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
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
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
