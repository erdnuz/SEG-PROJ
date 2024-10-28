package com.example.rentify.models;

import android.util.Log;

import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.util.QueryCallback;

/**
 * The Admin class represents an admin user with additional privileges, such as
 * category management, user account management, and listing deletion.
 */
public class Admin extends User {

    private static final String TAG = "Admin";
    private DatabaseHelper db = DatabaseHelper.getInstance();

    /**
     * Default constructor for Admin, initializes the role as "ADMIN".
     */
    public Admin() {
        super();
        this.setRole("ADMIN");
        Log.d(TAG, "Admin instance created with default constructor");
    }

    /**
     * Parameterized constructor to initialize Admin with specific details.
     *
     * @param email     the email of the admin.
     * @param firstName the first name of the admin.
     * @param lastName  the last name of the admin.
     * @param enabled   the enabled status of the admin.
     */
    public Admin(String email, String firstName, String lastName, boolean enabled) {
        super(email, firstName, lastName, enabled, "ADMIN");
        Log.d(TAG, "Admin instance created with parameters: " + toString());
    }

    /**
     * Adds a new category to the platform.
     *
     * @param category the category to add.
     */
    public void addCategory(Category category, QueryCallback callback) {
        db.addCategory(category, callback);
        Log.d(TAG, "Category added: " + category);
    }

    /**
     * Removes an existing category from the platform.
     *
     * @param category the category to remove.
     */
    public void removeCategory(Category category, QueryCallback callback) {
        db.removeCategory(category, callback);
        Log.d(TAG, "Category removed: " + category);
    }

    /**
     * Manages a user's account based on the specified action.
     *
     * @param user   the user to manage.
     * @param action the action to perform on the user; can be "enable", "disable", or "delete".
     * @throws IllegalArgumentException if the action is not recognized.
     */
    public void manageUserAccount(User user, String action) {
        switch (action.toLowerCase()) {
            case "delete":
                db.deleteUser(user);
                Log.d(TAG, "User deleted: " + user.getEmail());
                break;
            case "disable":
                user.setEnabled(false);
                db.updateUser(user);
                Log.d(TAG, "User disabled: " + user.getEmail());
                break;
            case "enable":
                user.setEnabled(true);
                db.updateUser(user);
                Log.d(TAG, "User enabled: " + user.getEmail());
                break;
            default:
                Log.e(TAG, "Invalid action: " + action);
                throw new IllegalArgumentException("Please choose one of 'enable', 'disable', or 'delete'");
        }
    }

    /**
     * Deletes a listing for content moderation purposes.
     *
     * @param listing the listing to delete.
     */
    public void deleteListing(Listing listing) {
        db.deleteListing(listing);
        Log.d(TAG, "Listing deleted: " + listing.getTitle());
    }

    /**
     * Provides a string representation of the Admin object, including its role and details.
     *
     * @return a string describing the administrator.
     */
    @Override
    public String toString() {
        return "Administrator {" +
                "Name='" + getFirstName() + " " + getLastName() + '\'' +
                ", Email='" + getEmail() + '\'' +
                ", Role='" + getRole() + '\'' +
                ", Enabled=" + getEnabled() +
                '}';
    }
}
