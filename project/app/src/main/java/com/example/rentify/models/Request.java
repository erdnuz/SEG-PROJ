package com.example.rentify.models;

import java.io.Serializable;

public class Request implements Serializable {

    // Attributes
    private Long date;
    private Renter renter;
    private Listing listing;

    // Default constructor
    public Request() {}

    // Constructor with parameters
    public Request(Long date, Renter renter, Listing listing) {
        this.date = date;
        this.renter = renter;
        this.listing = listing;
    }

    // Getter and Setter for date
    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    // Getter and Setter for renter
    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    // Getter and Setter for listing
    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    @Override
    public String toString() {
        return "Request{" +
                "date=" + date +
                ", renter=" + renter +
                ", listing=" + listing +
                '}';
    }
}
