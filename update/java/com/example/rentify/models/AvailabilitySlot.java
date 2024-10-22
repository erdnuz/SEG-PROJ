package com.example.rentify.models;

import java.util.HashMap;
import java.util.Map;

/**
 * The AvailabilitySlot class represents a time slot available for rent.
 */
public class AvailabilitySlot {
    private String start;   // Start time of the availability slot
    private String end;     // End time of the availability slot
    private Renter renter;  // Renter associated with the slot
    private Listing listing;
    private String id;// Listing associated with the slot

    // Constructor
    public AvailabilitySlot(String start, String end, String renterId, String listingId) {
        this.start = start;
        this.end = end;


        fetchRenter(renterId);
        fetchListing(listingId);
    }

    // Method to fetch Renter by ID
    private void fetchRenter(String renterId) {
        DatabaseHelper.getInstance().getUser(renterId, new QueryCallback() {
            @Override
            public void onSuccess() {
                renter = (Renter) results.get("user"); // Assuming the user is retrieved successfully
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                System.err.println("Error fetching renter: " + e.getMessage());
            }
        });
    }

    // Method to fetch Listing by ID
    private void fetchListing(String listingId) {
        DatabaseHelper.getInstance().getListing(listingId, new QueryCallback() {
            @Override
            public void onSuccess() {
                listing = (Listing) results.get("listing"); // Assuming the listing is retrieved successfully
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                System.err.println("Error fetching listing: " + e.getMessage());
            }
        });
    }

    // Getters and Setters
    public String getStart() {return start;}
    public void setStart(String start) {this.start = start;}

    public String getEnd() {return end;}
    public void setEnd(String end) {this.end = end;}

    public Renter getRenter() {return renter;}
    public void setRenter(Renter renter) {this.renter = renter;}

    public Listing getListing() {return listing;}
    public void setListing(Listing listing) {this.listing = listing;}

    public String getId() {return id;}
    public void setId(String id) {this.id=id;}

    // Convert the object to a Map for database storage
    public Map<String, Object> toMap() {
        Map<String, Object> availabilityMap = new HashMap<>();
        availabilityMap.put("start", start);
        availabilityMap.put("end", end);
        availabilityMap.put("renter", renter.getId());
        availabilityMap.put("listing", listing.getId());

        return availabilityMap;
    }
}
