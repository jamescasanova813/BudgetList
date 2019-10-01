package com.example.budgetlist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView Email = findViewById(R.id.Email);
        TextView Password = findViewById(R.id.password);
        TextView budgetList = findViewById(R.id.budgetList);
        TextView submitButton = findViewById(R.id.SubmitButton);
        TextView createAccount = findViewById(R.id.CreateAccount);
        TextView forgotPassword = findViewById(R.id.ForgotPassword);

        String email = "Email:";
        String password = "Password:";
        String budgetlist = "BudgetList";
        String submitbutton = "Submit";
        String createaccount = "Create Account";
        String forgotpassword = "Forgot Password";

        Email.setText(email);
        Password.setText(password);
        budgetList.setText(budgetlist);
        submitButton.setText(submitbutton);
        createAccount.setText(createaccount);
        forgotPassword.setText(forgotpassword);


    }
}
