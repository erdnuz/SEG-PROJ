package com.example.rentify.models;

import java.util.ArrayList;

/**
 * The Admin class represents an admin user with additional privileges.
 */
public class Admin extends User {
    private DatabaseHelper db = DatabaseHelper.getInstance();

    public Admin(String email, String phone, String firstName, String lastName, boolean enabled) {
        super(email, phone, firstName, lastName, enabled);
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
            DatabaseHelper db = DatabaseHelper.getInstance();
            db.deleteListingByCategory(category);
            return true;
        }
        return false;
    }

    /**
     * Method to manage a users account.
     * @param id the user to manage
     * @param action the action to perform on user ("enable", "disable", or "delete")
     */
    public void manageUserAccount(String id, String action) {
        switch (action.toLowerCase()) {
            case "delete":
                db.deleteUser(id);
            case "disable":
                db.enableUser(id, false);
            case "enable":
                db.enableUser(id, true);
            default:
                throw new IllegalArgumentException("Please choose one of 'enable', 'disable', or 'delete'");
        }
    }

    /**
     * Method to delete a listing (content moderation).
     * @param id the ID of the listing to delete.
     */
    public void deleteListing(String id) {
        db.deleteListing(id);
    }

    @Override
    public String toString() {
        return "Administrator\n" + super.toString();
    }
}
