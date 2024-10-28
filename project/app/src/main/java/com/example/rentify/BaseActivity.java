package com.example.rentify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.models.Listing;
import com.example.rentify.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public abstract class BaseActivity extends AppCompatActivity {
    protected User currentUser;
    protected DatabaseHelper db;
    protected ImageView avatarView, requestsView, homeView;
    protected String TAG = "BaseActivity"; // Set your desired TAG for logging

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = DatabaseHelper.getInstance();
        currentUser = db.getCurrentUser();

        super.onCreate(savedInstanceState);// Set your layout here
    }

    protected void setUpToolbar() {
        homeView = findViewById(R.id.toolbarLogo);
        requestsView = findViewById(R.id.toolbarRequests);
        avatarView = findViewById(R.id.toolbarAvatar);

        // Check if views are properly initialized
        if (homeView == null || requestsView == null || avatarView == null) {
            log('e', "View initialization failed. One or more views are null.");
            return; // Handle this scenario appropriately
        }

        // Hide requests view for ADMIN role
        if ((currentUser != null && "RENTER".equals(currentUser.getRole()))) {
            requestsView.setVisibility(View.VISIBLE);
        } else requestsView.setVisibility(View.GONE);

        homeView.setOnClickListener(v -> {
            if (currentUser != null && "LESSOR".equals(currentUser.getRole())) {
                startActivity(new Intent(this, MainActivityLessor.class));
            } else {
                startActivity(new Intent(this, ListingViewHome.class));
            }
        });

        requestsView.setOnClickListener(v -> {
            startActivity(new Intent(this, RequestsActivity.class));
        });

        avatarView.setOnClickListener(v -> {
            ProfileActivity.setIntentUser(currentUser);
            startActivity(new Intent(this, ProfileActivity.class)); // Change to your desired profile activity
        });

        setUpAvatar(avatarView, currentUser);
    }

    protected void log(char type, String message) {
        switch (type) {
            case 'e':
                Log.e(TAG, message);
                break;
            case 'w':
                Log.w(TAG, message);
                break;
            default:
                Log.d(TAG, message);
                break;
        }
    }

    protected void logToast(char type, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        log(type, message);
    }

    protected void setUpAvatar(ImageView toPopulate, User user) {
        if (user != null) {
            FirebaseStorage.getInstance().getReference()
                    .child("avatars")
                    .child(user.getId() + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.drawable.def_avatar)
                                    .error(R.drawable.error_avatar)
                                    .into(toPopulate);
                        }
                    })
                    .addOnFailureListener(e -> {
                        log('e', "Error fetching avatar URL: " + e.getMessage());
                        toPopulate.setImageResource(R.drawable.error_avatar);
                    });
        } else {
            toPopulate.setImageResource(R.drawable.error_avatar);
        }
    }

    protected void setUpListing(ImageView toPopulate, Listing listing, boolean hideOnFail) {
        if (listing != null) {
            FirebaseStorage.getInstance().getReference()
                    .child("listings")
                    .child(listing.getId() + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            toPopulate.setVisibility(View.VISIBLE);
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.drawable.def_thumbnail)
                                    .error(R.drawable.def_thumbnail)
                                    .into(toPopulate);
                        }
                    })
                    .addOnFailureListener(e -> {
                        log('e', "Error fetching listing URL: " + e.getMessage());
                        if (hideOnFail) toPopulate.setVisibility(View.GONE);
                    });
        } else {
            if (hideOnFail) toPopulate.setVisibility(View.GONE);
        }
    }

}
