package com.example.rentify.models;

import com.example.rentify.util.DatabaseHelper;

import java.io.Serializable;

/**
 * The User class serves as a base class for different types of user accounts.
 */
public class User implements Serializable {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private String role;

    public User() {
        this.enabled=true;
    }

    public User(String email, String firstName, String lastName, boolean enabled, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.role = role;
    }

    // Setters and Getters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String fullName() {
        return firstName + " " + lastName;
    }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public void pushUpdates() {
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.updateUser(this);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) return false;
        User u = (User) other;
        return u.getId().equals(id);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + fullName() + "\nEmail: " + email;
    }
}
