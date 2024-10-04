package com.example.rentify.models;

/**
 * Enum representing the different categories of items available for rent.
 */
public enum Category {
    ELECTRONICS("Electronics"),
    VEHICLES("Vehicles"),
    TOOLS("Tools"),
    FURNITURE("Furniture"),
    CLOTHING("Clothing"),
    SPORTS("Sports"),
    GARDENING("Gardening Equipment"),
    OFFICE("Office Supplies"),
    MUSIC("Musical Instruments"),
    CAMPING("Camping Gear"),
    APPLIANCES("Home Appliances"),
    DECOR("Home Decor");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
