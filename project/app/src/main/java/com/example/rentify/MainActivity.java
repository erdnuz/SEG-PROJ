package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rentify.models.Lessor;
import com.example.rentify.util.QueryCallback;
import com.example.rentify.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        // Check user authentication
        checkUserAuthentication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserAuthentication();
    }

    /**
     * Checks if a user is authenticated and redirects them accordingly.
     */
    private void checkUserAuthentication() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, show a welcome message
            Log.d(TAG, "User is authenticated: " + currentUser.getEmail());
            db.userExists(currentUser.getEmail(), new QueryCallback() {
                @Override
                public void onSuccess() {
                    if ((boolean) results.get("status")) {
                        db.getUserFromEmail(currentUser.getEmail(), new QueryCallback() {
                            @Override
                            public void onSuccess() {
                                currUser = (User) results.get("user"); // Assuming results is accessible here
                                db.setCurrentUser(currUser);

                                if (currUser != null) {
                                    Log.d(TAG, "Redirecting user with role: " + currUser.getRole());
                                    redirectUserBasedOnRole();

                                } else {
                                    Log.e(TAG, "Failed to retrieve user from database.");
                                }
                            }

                            @Override
                            public void onError(Exception err) {
                                Log.e(TAG, "Email search error: " + err);
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Your account has a pending delete request.", Toast.LENGTH_SHORT).show();
                        deleteUser(currentUser); // Attempt to delete the user
                    }
                }

                @Override
                public void onError(Exception err) {
                    Log.e(TAG, "Error checking user existence: " + err);
                }
            });

            Toast.makeText(MainActivity.this, "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            // No user is signed in, redirect to ChooseActivity
            Log.d(TAG, "No user is signed in.");
            redirectToChooseActivity();
        }
    }


    private void deleteUser(FirebaseUser currentUser) {
        // Attempt to delete the user
        currentUser.delete()
                .addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        // User deletion successful
                        Toast.makeText(MainActivity.this, "Your account was deleted", Toast.LENGTH_SHORT).show();
                        redirectToChooseActivity();
                    } else {
                        // User deletion failed, log out and redirect
                        Log.e(TAG, "Failed to delete Firebase user: ", deleteTask.getException());
                        logOutAndRedirect();
                    }
                });
    }
    private void logOutAndRedirect() {
        mAuth.signOut(); // Sign out the user
        redirectToChooseActivity(); // Redirect to ChooseActivity
        Toast.makeText(MainActivity.this, "You have been logged out due to failed account deletion.", Toast.LENGTH_SHORT).show();
    }






    /**
     * Redirects the user based on their role.
     */
    private void redirectUserBasedOnRole() {
        if (currUser == null) {
            Log.e(TAG, "Cannot redirect; current user is null.");
            return; // Exit if currUser is null
        }

        logUserDetails(currUser);
        switch (currUser.getRole()) {
            case "LESSOR":
                redirectToActivity(MainActivityLessor.class, "LESSOR");
                break;
            case "RENTER":
                redirectToActivity(ListingViewHome.class, "RENTER");
                break;
            case "ADMIN":
                redirectToActivity(ListingViewHome.class, "ADMIN");
                break;
            default:
                Log.e(TAG, "Unknown user role: " + currUser.getRole());
                break;
        } finish();
    }



    /**
     * Redirects to the specified activity and finishes the current one.
     */
    private void redirectToActivity(Class<?> activityClass, String activity) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        if ("LESSOR".equals(activity)) {
            Lessor user = (Lessor) db.getCurrentUser();
            MainActivityLessor.setIntentUser(user);
        }

        startActivity(intent);
        finish();
        progressBar.setVisibility(View.GONE);// Close MainActivity to prevent returning without authentication
    }

    /**
     * Redirects the user to ChooseActivity if not authenticated.
     */
    private void redirectToChooseActivity() {

        redirectToActivity(ChooseActivity.class, "CHOOSE");
    }

    /**
     * Logs user details for debugging purposes.
     */
    private void logUserDetails(User user) {
        if (user != null) {
            Log.d(TAG, "Authenticated User: " + user.getEmail() + ", Role: " + user.getRole());
        } else {
            Log.d(TAG, "No user details available.");
        }
    }
}
