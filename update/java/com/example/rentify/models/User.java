package com.example.rentify.models;

import java.util.HashMap;
import java.util.Map;

/**
 * The User class serves as a base class for different types of user accounts.
 */
public abstract class User {
    private String id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private Boolean enabled;

    public User() {
    }

    public User(String email, String phone, String firstName, String lastName, boolean enabled) {
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    // Getters and Setters
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public boolean isEnabled() {return enabled;}
    public void setEnabled(boolean enabled) {this.enabled = enabled;}


    public String getRole() {
        if (this instanceof Lessor) {
            return "LESSOR";
        } else if (this instanceof Renter) {
            return "RENTER";
        } else if (this instanceof Admin) {
            return "ADMIN";
        } else {
            return "UNKNOWN";
        }
    }


    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("enabled", enabled);
        userMap.put("role", this.getRole());

        return userMap;
    }

    public void pushUpdates() {
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.updateUser(this);
    }





    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + getFirstName() + " " + getLastName() + "\nEmail: " + email;
    }
}
