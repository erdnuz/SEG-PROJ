package com.example.rentify.models;

import java.util.ArrayList;

/**
 * The Admin class represents an admin user with additional privileges.
 */
public class Admin extends User {
    public Admin(String email, String firstName, String lastName) {
        super(email, firstName, lastName);
    }

    /**
     * Method to add a new category.
     * @param category the category to add.
     * @return true if the category is added successfully, false otherwise
     */
    public boolean addCategory(String category) {
        if (!Listing.categories.contains(category)) {
            Listing.categories.add(category);
            return true;
        }
        return false;
    }

    /**
     * Method to remove a category.
     * @param category the category to remove.
     * @return true if the category is removed successfully, false otherwise
     */
    public boolean removeCategory(String category) {
        if (Listing.categories.size()<4) { //Ensure there are always at least three categories
            return false;
        }
        if (Listing.categories.remove(category)) {
            //Delete all posts from that category
            throw new UnsupportedOperationException("Database has not been connected.");
        }
        return false;
    }

    /**
     * Method to manage a users account.
     * @param user the user to manage
     * @param action the action to perform on user ("enable", "disable", or "delete")
     * @return true if the action is executed successfully, false otherwise
     */
    public boolean manageUserAccount(User user, String action) {
        long userId = user.getId();
        switch (action.toLowerCase()) {
            case "delete":
                throw new UnsupportedOperationException("Database not connected.");
            case "disable":
                return user.disable();
            case "enable":
                return user.enable();
            default:
                return false;
        }
    }

    /**
     * Method to delete a listing (content moderation).
     * @param listingId the ID of the listing to delete.
     * @return true if the operation is successful, false otherwise
     */
    public boolean deleteListing(int listingId) {
        throw new UnsupportedOperationException("Database not connected.");
    }

    @Override
    public String toString() {
        return "Administrator\n" + super.toString();
    }
}
