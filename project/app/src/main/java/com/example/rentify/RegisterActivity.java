package com.example.rentify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import for logging
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.models.Lessor;
import com.example.rentify.util.QueryCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentify.models.User;
import com.example.rentify.models.Renter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName, mLastName, mEmail, mPassword;
    private CheckBox mTermsCheckBox;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Spinner spinner;

    // Logging tag for this class
    private static final String TAG = "RegisterActivity";

    // Regex for a secure password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind XML elements to class variables
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mTermsCheckBox = findViewById(R.id.register_checkbox_tos);
        mRegisterBtn = findViewById(R.id.register);

        // Spinner setup
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);

        // Register button click listener
        mRegisterBtn.setOnClickListener(v -> registerUser());

        // Log the creation of the activity
        Log.d(TAG, "onCreate: RegisterActivity initialized.");

        // Navigate to login if the user already has an account
        findViewById(R.id.Already_have_an_account_Log_in).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigating to LoginActivity.");
        });
    }

    /**
     * Method to handle user registration.
     * Performs validation checks and proceeds with registration if valid.
     */
    private void registerUser() {
        Log.d(TAG, "registerUser: Attempting to register user.");

        // Get user inputs
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String role = spinner.getSelectedItem().toString().trim();


        // Validation checks with logging
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Name is required.");
            Log.e(TAG, "registerUser: First name is empty.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required.");
            Log.e(TAG, "registerUser: Email is empty.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email address.");
            Log.e(TAG, "registerUser: Invalid email format.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required.");
            Log.e(TAG, "registerUser: Password is empty.");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError("Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special symbol.");
            Log.e(TAG, "registerUser: Password does not meet criteria.");
            return;
        }

        if (!mTermsCheckBox.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Please agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "registerUser: Terms and Conditions not agreed to.");
            return;
        }

        // Role-based user creation with logging
        if (role.equals("LESSOR")) {
            User user = new Lessor(email, firstName, lastName, true);
            Log.d(TAG, "registerUser: Creating Lessor user.");
            checkUserExists(user);
        } else if (role.equals("RENTER")) {
            User user = new Renter(email, firstName, lastName, true);
            Log.d(TAG, "registerUser: Creating Renter user.");
            checkUserExists(user);
        } else {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select a valid role.");
            Log.e(TAG, "registerUser: Invalid role selected.");
        }
    }

    /**
     * Method to check if the user already exists in the database.
     * @param user The user object to check.
     */
    private void checkUserExists(User user) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance();
        Log.d(TAG, "checkUserExists: Checking if user exists.");

        dbHelper.userExists(user.getEmail(), new QueryCallback() {
            @Override
            public void onSuccess() {
                if ((boolean) this.results.get("status")) {
                    Log.d(TAG, "checkUserExists: User already exists.");
                    Toast.makeText(RegisterActivity.this, (String) results.get("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "checkUserExists: User does not exist. Proceeding with registration.");
                    registerNewUser(user);
                }
            }

            @Override
            public void onError(Exception err) {
                Log.e(TAG, "checkUserExists: Error checking user existence: " + err.toString());
                Toast.makeText(RegisterActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to register a new user in Firebase Auth and Firestore.
     * @param user The user object to register.
     */
    private void registerNewUser(User user) {
        String email = user.getEmail();
        DatabaseHelper db = DatabaseHelper.getInstance();
        String password = mPassword.getText().toString().trim();

        Log.d(TAG, "registerNewUser: Registering new user.");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, createTask -> {
                    if (createTask.isSuccessful()) {
                        Log.d(TAG, "registerNewUser: Registration successful.");
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        db.createUser(user);
                        navigateToMainActivity();
                    } else {
                        Log.e(TAG, "registerNewUser: Registration failed. " + createTask.getException().getMessage());
                        Toast.makeText(RegisterActivity.this, "Registration failed. " + createTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Method to navigate to the MainActivity after successful registration.
     */
    private void navigateToMainActivity() {
        Log.d(TAG, "navigateToMainActivity: Navigating to MainActivity.");
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
