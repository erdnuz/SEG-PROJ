package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseActivity extends AppCompatActivity {

    private Button mLogin, mRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            navigateToMainActivity();
            return; // Exit the onCreate method
        }

        // Bind buttons to class variables
        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.signup);

        // Set onClick listeners for the buttons
        mLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        mRegister.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close ChooseActivity to prevent returning to it
    }
}
