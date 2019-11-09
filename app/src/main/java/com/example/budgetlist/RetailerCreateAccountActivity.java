package com.example.budgetlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RetailerCreateAccountActivity extends AppCompatActivity {

    TextView retailerReEnterPassword;
    TextView retailerPasswordHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_create_account);

        retailerReEnterPassword = findViewById(R.id.ReEnterPassword);
        retailerPasswordHint = findViewById(R.id.RetailerPasswordHint);

        String retailerreenterpassword = "Re-Enter Password";
        String retailerpasswordhint = "Password Hint";

        retailerReEnterPassword.setHint(retailerreenterpassword);
        retailerPasswordHint.setHint(retailerpasswordhint);
    }
}
