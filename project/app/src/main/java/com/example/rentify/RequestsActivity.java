package com.example.rentify;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentify.models.Listing;
import com.example.rentify.models.Renter;


//There are two possible cases, the currentUser is a renter checking their requests
// to see if any have been approved (get requests by renter, read-only)
// OR
// The current user is a lessor managing requests on a given listing.
// (get requests by  listing, buttons approve and decline next to each request)
public class RequestsActivity extends BaseActivity {
    private static Listing intentListing;
    private static Renter intentRenter;

    public static void setIntentListing(Listing listing) {
        intentListing = listing;
    }

    public static void setIntentListing(Renter renter) {
        intentRenter = renter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}