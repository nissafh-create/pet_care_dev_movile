package com.example.petcareproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.CareRequestAdapter;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private TextView tabAll, tabOpen, tabConfirmed, tabCompleted;
    private RecyclerView rvMyRequests;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private CareRequestAdapter adapter;
    private String currentFilter = "all";
    private List<CareRequest> allMyRequests = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_requests);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabAll = findViewById(R.id.tabAll);
        tabOpen = findViewById(R.id.tabOpen);
        tabConfirmed = findViewById(R.id.tabConfirmed);
        tabCompleted = findViewById(R.id.tabCompleted);
        rvMyRequests = findViewById(R.id.rvMyRequests);
        navMyRequests = findViewById(R.id.navMyRequests);
        navPublish = findViewById(R.id.navPublish);
        navSitters = findViewById(R.id.navSitters);
        navProfile = findViewById(R.id.navProfile);

        rvMyRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CareRequestAdapter(new ArrayList<>(), request -> {
            Intent intent = new Intent(MyRequestsActivity.this, RequestDetailOwnerActivity.class);
            intent.putExtra("requestId", request.getRequestId());
            startActivity(intent);
        });
        rvMyRequests.setAdapter(adapter);

        tabAll.setOnClickListener(v -> selectTab("all", tabAll));
        tabOpen.setOnClickListener(v -> selectTab(Constants.STATUS_OPEN, tabOpen));
        tabConfirmed.setOnClickListener(v -> selectTab(Constants.STATUS_CONFIRMED, tabConfirmed));
        tabCompleted.setOnClickListener(v -> selectTab(Constants.STATUS_COMPLETED, tabCompleted));

        navPublish.setOnClickListener(v -> {
            startActivity(new Intent(this, PublishRequestActivity.class));
            finish();
        });

        navSitters.setOnClickListener(v -> {
            startActivity(new Intent(this, SittersActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, OwnerProfileActivity.class));
            finish();
        });

        loadRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
    }

    private void selectTab(String filter, TextView selectedTab) {
        currentFilter = filter;
        TextView[] tabs = {tabAll, tabOpen, tabConfirmed, tabCompleted};
        for (TextView t : tabs) {
            t.setTextColor(ContextCompat.getColor(this, R.color.black));
            t.setTypeface(null, Typeface.NORMAL);
        }
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.green_dark));
        selectedTab.setTypeface(null, Typeface.BOLD);
        applyFilter();
    }

    private void loadRequests() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("careRequests")
                .whereEqualTo("ownerId", currentUser.getUid())
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    allMyRequests.clear();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            CareRequest req = doc.toObject(CareRequest.class);
                            if (req != null) {
                                req.setRequestId(doc.getId());
                                allMyRequests.add(req);
                            }
                        }
                    }
                    applyFilter();
                });
    }

    private void applyFilter() {
        List<CareRequest> filtered = new ArrayList<>();
        for (CareRequest r : allMyRequests) {
            if ("all".equals(currentFilter) || currentFilter.equals(r.getStatus())) {
                filtered.add(r);
            }
        }
        adapter.updateList(filtered);
    }
}
