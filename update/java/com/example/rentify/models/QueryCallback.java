package com.example.rentify.models;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.database.DatabaseError;

public abstract class QueryCallback {
    public Map<String, Object> results;

    public QueryCallback() {
        this.results = new HashMap<String, Object>();
    }

    public abstract void onSuccess();
    public abstract void onError(Exception err);
}

