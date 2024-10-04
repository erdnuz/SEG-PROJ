package com.example.rentify.models;

import java.time.LocalDate;

/**
 * The Renter class represents a user who rents items from the platform.
 */
public class Renter extends Account {

    // Constructor
    public Renter(String email, String firstName, String lastName) {
        super(email, firstName, lastName);
    }

    /**
     * Method to search for items available for rent.
     * @param category the category to search for.
     */
    public void searchItems(Category category) {
        System.out.println("Searching for items in category: " + category);
        // Implement search logic here
    }

    /**
     * Method to submit a rental request.
     * @param itemId the ID of the item to rent.
     * @param startRental the start date of the rental.
     * @param endRental the end date of the rental.
     */
    public void submitRentalRequest(int itemId, LocalDate startRental, LocalDate endRental) {
        System.out.println("Submitting rental request for item ID: " + itemId +
                           " from " + startRental + " to " + endRental);
        // Implement rental request logic here
    }

    @Override
    public String toString() {
        return "Renter\n" + super.toString();
    }
}
