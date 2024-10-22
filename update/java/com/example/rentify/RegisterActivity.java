package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rentify.models.DatabaseHelper;
import com.example.rentify.models.QueryCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstName, mLastName, mEmail, mPassword, mPhoneNumber;
    private CheckBox mTermsCheckBox;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        mPhoneNumber = findViewById(R.id.phonenumber);
        mTermsCheckBox = findViewById(R.id.register_checkbox_tos);
        mRegisterBtn = findViewById(R.id.register);

        // Register button click listener
        mRegisterBtn.setOnClickListener(v -> registerUser());

        // Already have an account? Log in
        findViewById(R.id.Already_have_an_account_Log_in).setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();

        // Validation checks
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Name is required.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required.");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError("Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special symbol.");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber.setError("Phone Number is required.");
            return;
        }

        if (!mTermsCheckBox.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Please agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already exists by email or phone number
        checkUserExists(email, phoneNumber);
    }

    private void checkUserExists(String email, String phoneNumber) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance();

        dbHelper.userExists(email, phoneNumber, new QueryCallback() {
            @Override
            public void onSuccess() {

                if ((boolean) this.results.get("status")) {
                    // Display the message indicating which field (email or phone) is already in use
                    Toast.makeText(RegisterActivity.this, (String) results.get("message"), Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with registration if neither email nor phone number are in use
                    registerNewUser(email, phoneNumber);
                }
            }

            @Override
            public void onError(Exception err) {
                // Handle the error (e.g., show a Toast with the error message)
                Toast.makeText(RegisterActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void registerNewUser(String email, String phoneNumber) {
        String password = mPassword.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, createTask -> {
                    if (createTask.isSuccessful()) {
                        // Registration successful
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Registration failed. " + createTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
