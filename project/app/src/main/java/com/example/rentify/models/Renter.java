package com.example.rentify.models;

import android.util.Log;
import android.widget.Toast;

import java.time.LocalDate;

/**
 * The Renter class represents a user who rents items from the platform.
 * This class extends the User class and includes methods specific to searching for items
 * and submitting rental requests.
 */
public class Renter extends User {

    private static final String TAG = "Renter";

    /**
     * Default constructor for Renter, initializes role as "RENTER".
     */
    public Renter() {
        super();
        this.setRole("RENTER");
        Log.d(TAG, "Renter instance created with default constructor");
    }

    /**
     * Parameterized constructor to initialize Renter with specific details.
     *
     * @param email     the email of the renter.
     * @param firstName the first name of the renter.
     * @param lastName  the last name of the renter.
     * @param enabled   the enabled status of the renter.
     */
    public Renter(String email, String firstName, String lastName, boolean enabled) {
        super(email, firstName, lastName, enabled, "RENTER");
        Log.d(TAG, "Renter instance created with parameters: " + toString());
    }

    public void createRequest(Listing listing) {
        Request requests = new Request();

    }



    /**
     * Provides a string representation of the Renter object, including the role and name.
     *
     * @return a string describing the renter.
     */
    @Override
    public String toString() {
        return "Renter {" +
                "Name='" + getFirstName() + " " + getLastName() + '\'' +
                ", Email='" + getEmail() + '\'' +
                ", Role='" + getRole() + '\'' +
                ", Enabled=" + getEnabled() +
                '}';
    }
}
