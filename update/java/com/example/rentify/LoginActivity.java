package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLoginButton, mRegisterButton;
    private ProgressBar mProgressBar;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure the layout file name matches

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mRegisterButton = findViewById(R.id.signup);// Ensure ProgressBar exists in the layout

        // Set up click listeners
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required.");
            return;
        }

        // Show progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // Login with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Sign in success, navigate to main activity
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Redirect to MainActivity or appropriate activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }
}
