package com.example.rentify.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * The Availability class represents a collection of available time slots for a specific listing.
 * It includes methods to check availability and reserve slots.
 */
public class Availability implements Serializable {

    private List<Slot> availability; // List of time slots for this availability instance
    private Listing listing;         // Listing associated with the availability
    private String id;               // Unique ID for the availability record

    /**
     * Default constructor that initializes the availability list with an open-ended slot
     * starting from today's midnight to infinity.
     */
    public Availability() {
        availability = new ArrayList<>();

        // Get current date at midnight as a long timestamp (in milliseconds)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        long startOfDay = Long.parseLong(dateFormat.format(calendar.getTime()));

        // Add the default Slot: start from today's midnight to "infinity" (Long.MAX_VALUE)
        availability.add(new Slot(startOfDay, Long.MAX_VALUE));
    }

    /**
     * Constructor for initializing availability with a specific listing and list of slots.
     *
     * @param listing      The listing associated with this availability.
     * @param availability List of pre-defined slots for this availability.
     */
    public Availability(Listing listing, List<Slot> availability) {
        this.listing = listing;
        this.availability = availability;
    }

    /**
     * Parcelable Constructor
     */
    protected Availability(Parcel in) {
        availability = new ArrayList<>();
        in.readList(availability, Slot.class.getClassLoader());
        listing = in.readParcelable(Listing.class.getClassLoader());
        id = in.readString();
    }

    /**
     * Checks if the specified time range is available within any of the existing slots.
     *
     * @param other the time slot to check
     * @return True if the time range is available, false otherwise.
     */
    public boolean isAvailable(Slot other) {
        for (Slot slot : availability) {
            if (slot.contains(other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reserves a specific time range by splitting the existing slot that contains the specified range.
     * This effectively removes the requested time range from availability.
     *
     * @param other the timeslot to reserve
     */
    public void reserve(Slot other) {
        Iterator<Slot> iterator = availability.iterator();

        // Get current date formatted as yyyyMMdd
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        long currentDate = Long.parseLong(dateFormat.format(calendar.getTime()));

        while (iterator.hasNext()) {
            Slot slot = iterator.next();

            // Remove slots whose end date has passed
            if (slot.getEnd() < currentDate) {
                iterator.remove();
                continue; // Skip to next slot
            }

            // Reserve the slot by splitting if it contains the desired range
            if (slot.contains(other)) {
                iterator.remove(); // Remove the containing slot
                availability.addAll(slot.split(other)); // Add split slots
                break; // Stop after the first containing slot is split
            }
        }
    }

    // Getters and Setters
    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Slot> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Slot> availability) {
        this.availability = availability;
    }
}
