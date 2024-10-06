package com.example.rentify.models;

/**
 * The User class serves as a base class for different types of user accounts.
 */
public class User {
    private static long nextId = 1;
    private final long id;
    private String userName;
    private String firstName;
    private String lastName;

    public User(String userName, String firstName, String lastName) {
        //Check if username is taken DB
        this.id = nextId++;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getUserName() {
        return userName;
    }

    // Setters DB implementation missing
    public void setUserName(String userName) {
        //update in db
        this.userName = userName;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        //update in db
    }


    public String toString() {
        return "ID: " + id + "\nName: " + getName() + "\nUsername: " + userName;
    }
}
