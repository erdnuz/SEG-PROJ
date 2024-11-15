package com.example.rentify.models;

import java.time.LocalDate;

/**
 * The Lessor class represents a user who offers items for rent.
 */
public class Lessor extends User {

    // Constructor
    public Lessor() {
        super();
    }

    public Lessor(String id, String email, String phone, String firstName, String lastName, boolean enabled) {
        super(id, email, phone, firstName, lastName, enabled);
    }

    /**
     * Method to create a listing for an item.
     * @param itemName the name of the item.
     * @param description a description of the item.
     * @param category the category of the item.
     * @param rentalFee the rental fee for the item.
     */
    public void createListing(String itemName, String description, String category, double rentalFee) {
        System.out.println("Creating listing for item: " + itemName +
                           ", Description: " + description +
                           ", Category: " + category +
                           ", Rental Fee: " + rentalFee);
        // Implement listing creation logic here
    }

    /**
     * Method to manage the availability of an item.
     * @param itemId the ID of the item.
     * @param availableFrom the start date of availability.
     * @param availableUntil the end date of availability.
     */
    public void manageAvailability(int itemId, LocalDate availableFrom, LocalDate availableUntil) {
        System.out.println("Managing availability for item ID: " + itemId +
                           ", Available from: " + availableFrom +
                           ", Available until: " + availableUntil);
        // Implement availability management logic here
    }

    /**
     * Method to accept or decline a rental request.
     * @param requestId the ID of the rental request.
     * @param accept true to accept, false to decline.
     */
    public void respondToRentalRequest(int requestId, boolean accept) {
        String response = accept ? "accepted" : "declined";
        System.out.println("Rental request ID: " + requestId + " has been " + response);
        // Implement rental request response logic here
    }

    @Override
    public String toString() {
        return "Lessor " + super.toString();
    }
}
