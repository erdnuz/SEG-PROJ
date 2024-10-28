package com.example.rentify.util;
import java.util.Map;
import java.util.HashMap;

public abstract class QueryCallback {
    public Map<String, Object> results;

    public QueryCallback() {
        this.results = new HashMap<String, Object>();
    }

    public abstract void onSuccess();
    public abstract void onError(Exception err);
}

