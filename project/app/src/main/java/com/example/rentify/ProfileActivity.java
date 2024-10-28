package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.rentify.models.Admin;
import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.models.Lessor;
import com.example.rentify.models.User;
import com.google.firebase.auth.FirebaseAuth;

/**
 * ProfileActivity manages the display and functionality of a user's profile.
 * It includes options for the current user to edit their profile, and for admins to manage other users' accounts.
 */
public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private static User intentUser;
    private ImageView avatarImageView;
    private TextView nameTextView, disabledTextView, roleTextView, emailTextView, idTextView;
    private Button editProfileButton, enableDisableButton, deleteButton, logOutButton, viewListings;
    private LinearLayout adminControlsLayout;

    protected static void setIntentUser(User intent) {
        intentUser = intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar();
        log('d', "Loading profile for intent user: " + intentUser);

        if (currentUser == null || intentUser == null) {
            Log.e(TAG, "Current user or intent user is null. Cannot proceed with profile setup.");
            return;
        }
        initializeViews();
        setupProfileData();

        avatarImageView = findViewById(R.id.profile_avatar);
        setUpAvatar(avatarImageView, intentUser);

        setupAdminControls(currentUser, intentUser);
        setupEditProfileButton(currentUser, intentUser);
        setupViewListingsButton();
        setUpToolbar();
    }

    private void setupViewListingsButton() {
        if ("LESSOR".equals(intentUser.getRole())) {
            viewListings.setVisibility(View.VISIBLE);
            viewListings.setOnClickListener(v -> {
                Lessor lessor = (Lessor) intentUser;
                MainActivityLessor.setIntentUser(lessor); // Set intent user in MainActivityLessor
                startActivity(new Intent(this, MainActivityLessor.class)); // Start the Lessor activity
            });
        } else {
            viewListings.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes views for user profile elements.
     */
    private void initializeViews() {
        nameTextView = findViewById(R.id.nameTextView);
        roleTextView = findViewById(R.id.roleTextView);
        emailTextView = findViewById(R.id.emailTextView);
        idTextView = findViewById(R.id.idTextView);
        editProfileButton = findViewById(R.id.editProfileButton);
        enableDisableButton = findViewById(R.id.enableDisableButton);
        deleteButton = findViewById(R.id.deleteButton);
        adminControlsLayout = findViewById(R.id.adminControlsLayout);
        disabledTextView = findViewById(R.id.disabledTextView);
        logOutButton = findViewById(R.id.logOutButton);
        viewListings = findViewById(R.id.viewListings);

    }

    /**
     * Sets example profile data. In production, this should be populated with real user data.
     */
    private void setupProfileData() {
        nameTextView.setText(intentUser.getFirstName() + " " + intentUser.getLastName());
        roleTextView.setText(intentUser.getRole());
        emailTextView.setText(intentUser.getEmail());
        idTextView.setText(intentUser.getId());
        if (!intentUser.getEnabled()) {
            disabledTextView.setVisibility(View.VISIBLE);
        } else {
            disabledTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Configures admin controls for enabling/disabling or deleting a user.
     * Only visible if the current user is an admin.
     */
    private void setupAdminControls(User currentUser, User intentUser) {
        if ("ADMIN".equals(currentUser.getRole()) && !currentUser.equalTo(intentUser)) {
            adminControlsLayout.setVisibility(View.VISIBLE);
            Admin admin = (Admin) currentUser;
            idTextView.setVisibility(View.VISIBLE);
            emailTextView.setVisibility(View.VISIBLE);
            setupEnableDisableButton(admin, intentUser);
            setupDeleteButton(admin, intentUser);
        } else {
            idTextView.setVisibility(View.GONE);
            adminControlsLayout.setVisibility(View.GONE);
            Log.d(TAG, "Admin controls hidden as the current user is not an admin.");
        }
    }

    /**
     * Configures the enable/disable button based on the user state.
     */
    private void setupEnableDisableButton(Admin admin, User intentUser) {
        if (intentUser.getEnabled()) {
            enableDisableButton.setText("Disable");
            enableDisableButton.setOnClickListener(v -> {
                logToast('d', "Disabling user: " + intentUser.getEmail());
                admin.manageUserAccount(intentUser, "disable");
                setupProfileData();
                setupEnableDisableButton(admin, intentUser);
            });
        } else {
            enableDisableButton.setText("Enable");
            enableDisableButton.setOnClickListener(v -> {
                logToast('d', "Enabling user: " + intentUser.getEmail());
                admin.manageUserAccount(intentUser, "enable");
                setupProfileData();
                setupEnableDisableButton(admin, intentUser);
            });
        }
    }

    /**
     * Sets up the delete button to allow an admin to delete a user.
     */
    private void setupDeleteButton(Admin admin, User intentUser) {
        deleteButton.setOnClickListener(v -> {
            logToast('d', "Deleting user: " + intentUser.getEmail());
            admin.manageUserAccount(intentUser, "delete");
            startActivity(new Intent(this, ListingViewHome.class));
            finish();
        });
    }

    /**
     * Configures the edit profile button. This button is only visible for the current user viewing their own profile.
     */
    private void setupEditProfileButton(User currentUser, User intentUser) {
        if (currentUser.getEmail().equals(intentUser.getEmail())) {
            editProfileButton.setVisibility(View.VISIBLE);
            emailTextView.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.VISIBLE);
            logOutButton.setOnClickListener(v -> logout());
            editProfileButton.setOnClickListener(v -> openEditProfileDialog());
        } else {
            editProfileButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.GONE);
            log('d', "Edit profile button hidden as the current user is viewing another user's profile.");
        }
    }

    private void logout() {
        // Log out from Firebase
        FirebaseAuth.getInstance().signOut();
        logToast('d', "Logged out.");

        // Set current user to null in the database
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.setCurrentUser(null);

        // Navigate to ChooseActivity
        Intent intent = new Intent(this, ChooseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
        log('d', "Navigating to ChooseActivity.");
        finish(); // Optional: Call finish to close ProfileActivity
    }

    /**
     * Opens a dialog for editing the user's profile.
     */
    private void openEditProfileDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.edit_profile_dialog, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Initialize dialog views
        EditText editFirst = dialogView.findViewById(R.id.editFirstNameEditText);
        EditText editLast = dialogView.findViewById(R.id.editLastNameEditText);
        Button saveChangesButton = dialogView.findViewById(R.id.saveChangesButton);
        ImageView avatarDialogImageView = dialogView.findViewById(R.id.dialog_avatar); //TODO: Add the onclick method to upload photos and then upload them to firestore

        // Populate the fields with current user data
        editFirst.setText(intentUser.getFirstName());
        editLast.setText(intentUser.getLastName());

        // Set up save changes button listener
        saveChangesButton.setOnClickListener(v -> {
            String newFirst = editFirst.getText().toString();
            String newLast = editLast.getText().toString();

            // Validate input
            if (newFirst.isEmpty() || newLast.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            intentUser.setFirstName(newFirst);
            intentUser.setLastName(newLast);
            intentUser.pushUpdates();
            setupProfileData();
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }
}
