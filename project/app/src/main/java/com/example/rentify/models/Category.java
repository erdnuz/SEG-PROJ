package com.example.rentify.models;

import java.io.Serializable;

/**
 * The Category class represents a category with a name and description.
 * This class is used to categorize listings within the Rentify application.
 * It implements the Serializable interface to allow it to be passed between activities or saved to a file.
 */
public class Category implements Serializable {
    private String id;
    private String name;
    private String description;

    /**
     * Default constructor for the Category class.
     * Initializes the category with no name or description.
     */
    public Category() {
    }

    /**
     * Parameterized constructor for the Category class.
     *
     * @param name        The name of the category.
     * @param description The description of the category.
     */
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if this category is equal to another category based on the description.
     *
     * @param other The other category to compare with.
     * @return true if the descriptions are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Category category = (Category) other;
        return description != null && description.equals(category.getDescription());
    }

    /**
     * Returns a string representation of the category.
     *
     * @return A string that represents the category name and description.
     */
    @Override
    public String toString() {
        return name;
    }
}
