package com.example.rentify.models;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.database.DatabaseError;

// Abstract class 'QueryCallback' serves as a base class for handling asynchronous query results
public abstract class QueryCallback {
    public Map<String, Object> results;

    public QueryCallback() {
        this.results = new HashMap<String, Object>();
    }

    // Runs for successful query completions
    public abstract void onSuccess();
    // Runs for errors in the query
    public abstract void onError(Exception err);
}

