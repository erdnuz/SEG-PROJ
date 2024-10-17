package com.example.rentify.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import java.util.Map;

public class DatabaseHelper {

    private final DatabaseReference mDatabase;

    public DatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void createData(@NonNull String tableName, @NonNull long id, @NonNull Map<String, Object> data, @NonNull final OnDataStatusListener listener) {
        DatabaseReference newEntry = mDatabase.child(tableName).push(); // Generates a unique ID
        newEntry.setValue(data)
                .addOnSuccessListener(aVoid -> listener.onSuccess("Data added successfully with ID: " + newEntry.getKey()))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Method to update data in any table
    public void updateData(@NonNull String tableName, @NonNull long id, @NonNull Map<String, Object> updates, @NonNull final OnDataStatusListener listener) {
        mDatabase.child(tableName).child(id).updateChildren(updates)
                .addOnSuccessListener(aVoid -> listener.onSuccess("Data updated successfully in " + tableName))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Method to remove data from any table
    public void removeData(@NonNull String tableName, @NonNull long id, @NonNull final OnDataStatusListener listener) {
        mDatabase.child(tableName).child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Data removed successfully from " + tableName))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnDataStatusListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }


}
