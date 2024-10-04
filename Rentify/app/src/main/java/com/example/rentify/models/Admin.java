package com.example.rentify.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The Admin class represents an admin user with additional privileges.
 */
public class Admin extends Account {

    // Static list to store categories shared across all Admin instances
    private static List<Category> categories = new ArrayList<>(List.of(Category.values())); // Initialize with enum values

    // Constructor
    public Admin(String email, String firstName, String lastName) {
        super(email, firstName, lastName);
    }

    /**
     * Method to add a new category.
     * @param category the category to add.
     */
    public static void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
            System.out.println("Category added: " + category);
        } else {
            System.out.println("Category already exists: " + category);
        }
    }

    /**
     * Method to remove a category.
     * @param category the category to remove.
     */
    public static void removeCategory(Category category) {
        if (categories.size() > 3 && categories.remove(category)) {
            System.out.println("Category removed: " + category);
        } else if (categories.size() <= 3) {
            System.out.println("Cannot remove category. At least 3 categories must remain.");
        } else {
            System.out.println("Category not found: " + category);
        }
    }

    /**
     * Method to list all categories.
     * @return the list of categories.
     */
    public static List<Category> listCategories() {
        return new ArrayList<>(categories); // Return a copy of the list
    }

    /**
     * Method to manage user accounts (e.g., delete or disable accounts).
     * @param userAccount the user account to be managed.
     * @param action the action to perform (delete, disable, or enable).
     */
    public void manageUserAccount(Account userAccount, String action) {
        switch (action.toLowerCase()) {
            case "delete":
                System.out.println("Deleting user account: " + userAccount.getEmail());
                // Implement deletion logic here
                break;
            case "disable":
                System.out.println("Disabling user account: " + userAccount.getEmail());
                // Implement disable logic here
                break;
            case "enable":
                System.out.println("Enabling user account: " + userAccount.getEmail());
                // Implement enable logic here
                break;
            default:
                System.out.println("Invalid action: " + action);
                break;
        }
    }

    /**
     * Method to delete a post (content moderation).
     * @param postId the ID of the post to delete.
     */
    public void deletePost(int postId) {
        System.out.println("Deleting post with ID: " + postId);
        // Implement post deletion logic here
    }

    @Override
    public String toString() {
        return "Administrator\n" + super.toString();
    }
}
