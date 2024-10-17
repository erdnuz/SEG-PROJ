package com.example.rentify.models;

/**
 * The User class serves as a base class for different types of user accounts.
 */
public class User {
    private static long nextId = 0;
    private final long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;

    /**
     * Method to construct user from id, by fetching user data from database.
     * @param id the user's id
     */
    public User(long id) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The database has not been connected.");
    }

    public User(String email, String firstName, String lastName) {
        this.id = nextId++;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = true;
    }

    /**
     * Method to enable a user.
     * @return false if the user is already enabled, true otherwise
     */
    public boolean enable() {
        if (this.enabled) {
            return false;
        } 
        this.enabled = true;
        return true;
    }

    /**
     * Method to disable a user.
     * @return false if the user is already disabled, true otherwise
     */
    public boolean disable() {
        if (!this.enabled) {
            return false;
        } 
        this.enabled = false;
        return true;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        //update in db
    }


    public String toString() {
        return "ID: " + id + "\nName: " + getName() + "\nEmail: " + email;
    }
}
