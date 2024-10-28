package com.example.rentify;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.adapters.UserAdapter;
import com.example.rentify.util.QueryCallback;
import com.example.rentify.models.User;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends BaseActivity implements UserAdapter.UserClickListener {
    private static final String TAG = "AllUsersActivity"; // Static TAG for logging
    private List<User> users;
    private UserAdapter userAdapter;
    private RecyclerView usersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_users);

        // Set up window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize user list and adapter
        users = new ArrayList<>();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        userAdapter = new UserAdapter(users, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);

        // Load users from the database
        setUpToolbar();
        loadUsers();
    }

    private void loadUsers() {
        log('d', "Loading users from the database..."); // Log when loading starts
        db.getAllUsers(new QueryCallback() {
            @Override
            public void onSuccess() {
                users = (List<User>) results.get("users"); // Assuming this line retrieves the user list correctly
                userAdapter.update(users); // Update the adapter with the new user list
                log('d', "Successfully loaded " + users.size() + " users: "+users); // Log successful load
            }

            @Override
            public void onError(Exception err) {// Log error
                logToast('e', "Error loading users"); // Optional toast for user feedback
            }
        });
    }

    @Override
    public void onUserClick(User user) {
        log('d', "User clicked: " + user.fullName()); // Log user click
        Intent intent = new Intent(this, ProfileActivity.class);
        ProfileActivity.setIntentUser(user); // Assuming this method is static and sets the user to be displayed
        startActivity(intent);
    }
}
