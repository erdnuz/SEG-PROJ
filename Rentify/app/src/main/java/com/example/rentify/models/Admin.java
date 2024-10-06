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
     */
    public boolean addCategory(String category) {
        // Check if the category can be added through CategoryManager
        if (CategoryManager.addCategory(category)) {
            System.out.println("Category added: " + category);
            return true;
        } else {
            System.out.println("Category already exists: " + category);
            return false;
        }
    }

    /**
     * Method to remove a category.
     * @param category the category to remove.
     */
    public boolean removeCategory(String category) {
        // Check if the category can be removed through CategoryManager
        if (CategoryManager.removeCategory(category)) {
            System.out.println("Category removed: " + category);
            return true;
        } else {
            return false; // This could be because of the minimum category rule
        }
    }

    public User getUser(int userId) {
        // DATABASE LOGIC
        return null; // Placeholder
    }

    public boolean manageUserAccount(User user, String action) {
        long userId = user.getId();
        switch (action.toLowerCase()) {
            case "delete":
                //DB logic
                return false;
            case "disable":
                //DB logic disable
                return false;
            case "enable":
                //DB logic enable
                return false;
            default:
                return false;
        }
    }

    public Post getPost(int listingId) {
        // DATABASE LOGIC
        return null; // Placeholder
    }

    /**
     * Method to delete a post (content moderation).
     * @param postId the ID of the post to delete.
     */
    public void deletePost(int postId) {
        // DATABASE LOGIC
    }

    @Override
    public String toString() {
        return "Administrator\n" + super.toString();
    }
}
