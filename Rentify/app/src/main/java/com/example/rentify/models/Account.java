package com.example.rentify.models;

/**
 * The Account class serves as a base class for different types of user accounts.
 */
abstract class Account {
    // Tracks the next ID to be used by account creation
    private static int NEXT_ID = 0;

    // Account's unique identifier
    private final int id;

    // Account holder's email
    private String email;

    // Account holder's first name
    private String firstName;

    // Account holder's last name
    private String lastName;

    // Constructor
    public Account(String email, String firstName, String lastName) {
        this.id = NEXT_ID++;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Method to display account information.
     * @return a string representation of the account information.
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + getName() + "\nEmail: " + email;
    }
}
