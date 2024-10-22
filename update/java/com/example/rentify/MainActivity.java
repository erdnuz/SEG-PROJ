package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Check if a user is currently signed in
        checkUserAuthentication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check the user's authentication state in onStart as well
        checkUserAuthentication();
    }

    /**
     * This method checks if a user is authenticated and handles redirection accordingly.
     */
    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, show a welcome message
            currentUser.getEmail();
            Toast.makeText(MainActivity.this, "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            // You can proceed with main activity logic here if needed
        } else {
            // No user is signed in, redirect to ChooseActivity
            redirectToChooseActivity();
        }
    }

    /**
     * Redirects the user to ChooseActivity if they are not authenticated.
     */
    private void redirectToChooseActivity() {
        Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity to prevent the user from returning without authentication
    }
}
