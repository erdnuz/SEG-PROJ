package com.example.rentify.models;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.database.DatabaseError;

public class Review {
    private String id;
    private String dateTime;
    private Renter fromUser;
    private Listing toListing;
    private int rating;
    private String content;

    public Review(String dateTime, String fromUserId, Listing toListing, int rating, String content) {
        DatabaseHelper db = DatabaseHelper.getInstance();
        this.dateTime = dateTime;
        ;
        this.toListing = toListing;
        this.rating = rating;
        this.content = content;

        db.getUser(fromUserId, new QueryCallback() {
            @Override
            public void onSuccess() {
                User user = (User) results.get("user");
                if (user instanceof Renter) {fromUser = (Renter) user;}
                else {System.err.println("Error: User is not a Renter");}
            }
            @Override
            public void onError(Exception err) {
                System.err.println("Error retrieving user. " + err.toString());
            }
        });
    }

    // Setters and getters
    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setDateTime(String dateTime) {this.dateTime = dateTime;}
    public String getDateTime() {return dateTime;}

    public void setFromUser(Renter fromUser) {this.fromUser = fromUser;}
    public Renter getFromUser() {return fromUser;}

    public void setToListing(Listing toListing) {this.toListing = toListing;}
    public Listing getToListing() {return toListing;}

    public void setRating(int rating) {this.rating = rating;}
    public int getRating() {return rating;}

    public void setContent(String content) {this.content = content;}
    public String getContent() {return content;}

    public Map<String, Object> toMap() {
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("dateTime", dateTime);
        reviewMap.put("fromUser", fromUser.getId());
        reviewMap.put("toListing", toListing.getId());
        reviewMap.put("rating", rating);
        reviewMap.put("content", content);

        return reviewMap;
    }
}
