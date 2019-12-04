package com.example.budgetlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPassword extends AppCompatActivity {

    EditText emailEditText;
    Button emailSubmitButton;

    TextView securityQuestion;

    LinearLayout showSecurityAnswerLayout;
    EditText securityAnswer;
    Button securityAnswerButton;

    LinearLayout showCreateNewPasswordLayout;
    EditText createNewPassword;
    Button submitNewPasswordButton;

    DatabaseReference database;

    boolean fromSettings;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        fromSettings = getIntent().getExtras().getBoolean("FromSettings");

        emailEditText = findViewById(R.id.resetpassword_email);
        emailSubmitButton = findViewById(R.id.resetpassword_submitemail);

        securityQuestion = findViewById(R.id.resetpassword_securityquestion);

        showSecurityAnswerLayout = findViewById(R.id.resetpassword_showsecurityquestion);
        securityAnswer = findViewById(R.id.resetpassword_securityanswer);
        securityAnswerButton = findViewById(R.id.resetpassword_submitsecurityanswer);

        showCreateNewPasswordLayout = findViewById(R.id.resetpassword_shownewpassword);
        createNewPassword = findViewById(R.id.resetpassword_newpassword);
        submitNewPasswordButton = findViewById(R.id.resetpassword_submitnewpassword);

        database = FirebaseDatabase.getInstance().getReference();

        SetupButtons();

        if(fromSettings){
            AlreadyLoggedIn();
            showSecurityAnswerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void AlreadyLoggedIn(){
        SharedPreferences preferences = getSharedPreferences("SavedEmail", Context.MODE_PRIVATE);
        email = preferences.getString("Email", "");
        emailEditText.setText(email);

        GetSecurityQuestion();
    }

    private void GetSecurityQuestion(){
        database.child("Users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("SecurityQuestion")){
                    SetSecurityQuestion(dataSnapshot.child("SecurityQuestion").getValue().toString());
                }
                else{
                    Toast.makeText(ResetPassword.this, "No Security Question Was Set", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetSecurityQuestion(String question){
        securityQuestion.setText(question);
        showSecurityAnswerLayout.setVisibility(View.VISIBLE);
    }

    private void SetupButtons(){
        emailSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEmail();
            }
        });

        securityAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSecurityAnswer(securityAnswer.getText().toString());
            }
        });

        submitNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });
    }

    private void CheckEmail(){
        if(!fromSettings){
            email = emailEditText.getText().toString();
        }
        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(email)){
                    GetSecurityQuestion();
                }
                else{
                    Toast.makeText(ResetPassword.this, "Email Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckSecurityAnswer(final String answer){
        database.child("Users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("SecurityAnswer").getValue().equals(answer)){
                    ShowResetPassword();
                }
                else{
                    Toast.makeText(ResetPassword.this, "Incorrect Security Answer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ShowResetPassword(){
        showCreateNewPasswordLayout.setVisibility(View.VISIBLE);
    }

    private void UpdatePassword(){
        database.child("Users").child(email).child("Password").setValue(createNewPassword.getText().toString());

        if(fromSettings) {
            Intent intent = new Intent(ResetPassword.this, Home.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(ResetPassword.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
