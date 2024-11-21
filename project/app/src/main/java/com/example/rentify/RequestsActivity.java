package com.example.rentify;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.adapters.ListingAdapter;
import com.example.rentify.adapters.RequestAdapter;
import com.example.rentify.models.Lessor;
import com.example.rentify.models.Listing;
import com.example.rentify.models.Renter;
import com.example.rentify.models.Request;
import com.example.rentify.util.QueryCallback;

import java.util.ArrayList;
import java.util.List;


//There are two possible cases, the currentUser is a renter checking their requests
// to see if any have been approved (get requests by renter, read-only)
// OR
// The current user is a lessor managing requests on a given listing.
// (get requests by  listing, buttons approve and decline next to each request)
public class RequestsActivity extends BaseActivity implements RequestAdapter.OnRequestClickListener {
    private static Listing intentListing;
    private static Renter intentRenter;
    private List<Request> requests;
    private TextView titleView;
    private RequestAdapter requestAdapter;

    public static void setIntentListing(Listing listing) {
        intentListing = listing;
        intentRenter = null;
    }

    public static void setIntentRenter(Renter renter) {
        intentListing = null;
        intentRenter = renter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        setUpToolbar();

        titleView = findViewById(R.id.titleView);

        requests = new ArrayList<>();
        RecyclerView requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize requests and adapter with an empty list
        requests = new ArrayList<>();
        requestAdapter = new RequestAdapter(requests, this, intentListing!=null);

        // Set up the RecyclerView with the adapter
                requestsRecyclerView.setAdapter(requestAdapter);

        if (intentListing!=null) {
            titleView.setText("Requests for " + intentListing.getTitle());
            fetchRequests(intentListing);
        } else if (intentRenter!=null)  {
            titleView.setText("Your requests");
            fetchRequests(intentRenter);
        }





    }

    public void fetchRequests(Listing listing) {
        db.getRequestsByListing(listing, new QueryCallback() {
            @Override
            public void onSuccess() {
                requests.clear();
                requests.addAll((List<Request>) results.get("requests"));
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception err) {
                requests.clear();
                requestAdapter.notifyDataSetChanged();
            }

        });
    }

    public void fetchRequests(Renter renter) {
        db.getRequestsByRenter(renter, new QueryCallback() {
            @Override
            public void onSuccess() {
                requests.clear();
                requests.addAll((List<Request>) results.get("requests"));
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception err) {
                requests.clear();
                requestAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onRequestClick(Request request) {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request Options");

        // Determine user type and set options
        if (currentUser instanceof Lessor) {
            Lessor lessor = (Lessor) currentUser;
            builder.setMessage("What would you like to do with this request?")
                    .setPositiveButton("Approve", (dialog, which) ->
                    {
                        lessor.approveRequest(request);
                        requestAdapter.notifyDataSetChanged();
                        logToast('d',"Request approved.");
                    })
                    .setNegativeButton("Reject", (dialog, which) -> {
                        lessor.rejectRequest(request);
                        requestAdapter.notifyDataSetChanged();
                        logToast('d',"Request rejected.");
                    });
        } else if (currentUser instanceof Renter) {
            Renter renter = (Renter) currentUser;
            builder.setMessage("Do you want to cancel this request?")
                    .setPositiveButton("Cancel Request", (dialog, which) ->
                            {
                                renter.cancelRequest(request);
                                requests.remove(request);
                                requestAdapter.notifyDataSetChanged();
                                logToast('d',"Request canceled.");
                            }
                            );
        }

        // Add a cancel button for all users
        builder.setNeutralButton("Close", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}