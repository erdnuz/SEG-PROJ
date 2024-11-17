package com.example.rentify.models;

import android.util.Log;

import com.example.rentify.util.DatabaseHelper;

import java.time.LocalDate;

/**
 * The Lessor class represents a user who offers items for rent.
 * This class extends the User class with additional methods specific to item rental functionality,
 * such as creating listings, managing item availability, and responding to rental requests.
 */
public class Lessor extends User {

    private static final String TAG = "Lessor";

    /**
     * Default constructor for Lessor, initializes role as "LESSOR".
     */
    public Lessor() {
        super();
        this.setRole("LESSOR");
        Log.d(TAG, "Lessor instance created with default constructor");
    }

    /**
     * Parameterized constructor to initialize Lessor with specific details.
     *
     * @param email     the email of the lessor.
     * @param firstName the first name of the lessor.
     * @param lastName  the last name of the lessor.
     * @param enabled   the enabled status of the lessor.
     */
    public Lessor(String email, String firstName, String lastName, boolean enabled) {
        super(email, firstName, lastName, enabled, "LESSOR");
        Log.d(TAG, "Lessor instance created with parameters: " + toString());
    }

    /**
     * Creates a new listing for an item.
     *
     * @param itemName    the name of the item.
     * @param description a description of the item.
     * @param category    the category of the item.
     * @param rentalFee   the rental fee for the item.
     * @return the newly created Listing object.
     */
    public Listing createListing(String itemName, String description, Category category, double rentalFee, Long startDate, Long endDate) {
        Listing newListing = new Listing(category, description, itemName, this, rentalFee, startDate, endDate);
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.createListing(newListing);
        Log.d(TAG, "New listing created: " + newListing);
        return newListing;
    }

    public void approveRequest(Request r) {
        r.setStatus(1);
        DatabaseHelper.getInstance().updateRequest(r);
    }

    public void rejectRequest(Request r) {
        r.setStatus(-1);
        DatabaseHelper.getInstance().updateRequest(r);
    }

    /**
     * Deletes a listing for content moderation purposes.
     *
     * @param listing the listing to delete.
     */
    public void deleteListing(Listing listing) {
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.deleteListing(listing);
        Log.d(TAG, "Listing deleted: " + listing.getTitle());
    }

    /**
     * Responds to a rental request by accepting or declining it.
     *
     * @param requestId the ID of the rental request.
     * @param accept    true to accept the request, false to decline.
     */
    public void respondToRentalRequest(int requestId, boolean accept) {
        String response = accept ? "accepted" : "declined";
        Log.d(TAG, "Rental request ID: " + requestId + " has been " + response);
        // Implement rental request response logic here
    }

    /**
     * Provides a string representation of the Lessor object, including user role and name.
     *
     * @return a string describing the lessor.
     */
    @Override
    public String toString() {
        return "Lessor {" +
                "Name='" + getFirstName() + " " + getLastName() + '\'' +
                ", Email='" + getEmail() + '\'' +
                ", Role='" + getRole() + '\'' +
                ", Enabled=" + getEnabled() +
                '}';
    }
}
