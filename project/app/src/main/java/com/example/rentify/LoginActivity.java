package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.util.QueryCallback;
import com.example.rentify.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLoginButton, mRegisterButton;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mRegisterButton = findViewById(R.id.signup); // Ensure ProgressBar exists in the layout

        // Set up click listeners
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                loginUser();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Register button clicked");
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    /**
     * Redirects the user to the MainActivity.
     */
    private void redirectChoose() {
        Log.d(TAG, "Redirecting to MainActivity");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles user login by validating credentials, signing in with Firebase,
     * and checking if the user's account exists in the DatabaseHelper.
     */
    private void loginUser() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        DatabaseHelper db = DatabaseHelper.getInstance();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required.");
            Log.w(TAG, "Email field is empty");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required.");
            Log.w(TAG, "Password field is empty");
            return;
        }
        Log.d(TAG, "Logging in with Firebase");

        // Login with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide progress bar
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        FirebaseUser user = mAuth.getCurrentUser();
                        db.userExists(email, new QueryCallback() {
                            @Override
                            public void onSuccess() {
                                boolean exists = (boolean) results.get("status");
                                Log.d(TAG, "User existence check success: " + exists);

                                if (exists) {
                                    db.getUserFromEmail(email, new QueryCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "User retrieved from database");
                                            db.setCurrentUser((User) results.get("user"));
                                            redirectChoose();
                                        }

                                        @Override
                                        public void onError(Exception err) {
                                            Log.e(TAG, "Error fetching user from database", err);
                                            redirectChoose();
                                        }
                                    });
                                } else {
                                    Log.w(TAG, "User account not found, deleting Firebase account");
                                    user.delete();
                                    Toast.makeText(LoginActivity.this, "Your account was deleted. Please create a new account.", Toast.LENGTH_LONG).show();
                                    redirectChoose();
                                }
                            }

                            @Override
                            public void onError(Exception err) {
                                Log.e(TAG, "User existence check failed", err);
                                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
