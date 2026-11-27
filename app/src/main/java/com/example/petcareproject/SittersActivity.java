package com.example.petcareproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import com.example.petcareproject.adapters.ApplicantAdapter;
import com.example.petcareproject.adapters.TopSitterAdapter;
import com.example.petcareproject.models.Application;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.FakeDataManager;

import java.util.ArrayList;
import java.util.List;

public class SittersActivity extends AppCompatActivity {

    private TextView tabTopSitters, tabApplicants;
    private LinearLayout layoutApplicantsControls;
    private Spinner spRequest;
    private RecyclerView rvSitters;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private List<CareRequest> ownerOpenRequests;
    private ApplicantAdapter applicantAdapter;
    private TopSitterAdapter topSitterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sitters);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabTopSitters = findViewById(R.id.tabTopSitters);
        tabApplicants = findViewById(R.id.tabApplicants);
        layoutApplicantsControls = findViewById(R.id.layoutApplicantsControls);
        spRequest = findViewById(R.id.spRequest);
        rvSitters = findViewById(R.id.rvSitters);
        navMyRequests = findViewById(R.id.navMyRequests);
        navPublish = findViewById(R.id.navPublish);
        navSitters = findViewById(R.id.navSitters);
        navProfile = findViewById(R.id.navProfile);

        rvSitters.setLayoutManager(new LinearLayoutManager(this));

        setupTopSittersTab();
        setupApplicantsTab();

        boolean openApplicants = getIntent().getBooleanExtra("openApplicants", false);
        if (openApplicants) {
            showApplicantsTab();
        } else {
            showTopSittersTab();
        }

        tabTopSitters.setOnClickListener(v -> showTopSittersTab());
        tabApplicants.setOnClickListener(v -> showApplicantsTab());

        navMyRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, MyRequestsActivity.class));
            finish();
        });

        navPublish.setOnClickListener(v -> {
            startActivity(new Intent(this, PublishRequestActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, OwnerProfileActivity.class));
            finish();
        });
    }

    private void setupTopSittersTab() {
        List<User> topSitters = FakeDataManager.getTopSitters();
        topSitterAdapter = new TopSitterAdapter(topSitters);
    }

    private void setupApplicantsTab() {
        ownerOpenRequests = FakeDataManager.getOwnerOpenRequests(FakeDataManager.currentOwnerId);
        List<String> labels = new ArrayList<>();
        for (CareRequest r : ownerOpenRequests) {
            labels.add(r.getPetName() + ", " + r.getPetSpecies() + " (" + r.getStartDate() + " → " + r.getEndDate() + ")");
        }
        if (labels.isEmpty()) {
            labels.add("No open requests");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequest.setAdapter(spinnerAdapter);

        applicantAdapter = new ApplicantAdapter(new ArrayList<>(), application -> {
            if (ownerOpenRequests.isEmpty()) return;
            int pos = spRequest.getSelectedItemPosition();
            String requestId = ownerOpenRequests.get(pos).getRequestId();
            chooseSitter(requestId, application.getApplicationId());
        });

        spRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadApplicantsForSelectedRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadApplicantsForSelectedRequest() {
        if (ownerOpenRequests.isEmpty()) {
            applicantAdapter.updateList(new ArrayList<>());
            return;
        }
        int pos = spRequest.getSelectedItemPosition();
        String requestId = ownerOpenRequests.get(pos).getRequestId();
        List<Application> list = FakeDataManager.getApplicationsForRequest(requestId);
        applicantAdapter.updateList(list);
    }

    private void showTopSittersTab() {
        layoutApplicantsControls.setVisibility(View.GONE);
        rvSitters.setAdapter(topSitterAdapter);

        tabTopSitters.setTextColor(ContextCompat.getColor(this, R.color.green_dark));
        tabTopSitters.setTypeface(null, Typeface.BOLD);
        tabApplicants.setTextColor(ContextCompat.getColor(this, R.color.black));
        tabApplicants.setTypeface(null, Typeface.NORMAL);
    }

    private void showApplicantsTab() {
        layoutApplicantsControls.setVisibility(View.VISIBLE);
        rvSitters.setAdapter(applicantAdapter);
        loadApplicantsForSelectedRequest();

        tabApplicants.setTextColor(ContextCompat.getColor(this, R.color.green_dark));
        tabApplicants.setTypeface(null, Typeface.BOLD);
        tabTopSitters.setTextColor(ContextCompat.getColor(this, R.color.black));
        tabTopSitters.setTypeface(null, Typeface.NORMAL);
    }

    // Hard flow A6: accept chosen application, reject others, confirm request
    private void chooseSitter(String requestId, String applicationId) {
        // TODO Firebase: run WriteBatch to update application, other applications, and careRequest
        FakeDataManager.chooseSitter(requestId, applicationId);
        Toast.makeText(getApplicationContext(), "Sitter chosen! Request confirmed.", Toast.LENGTH_LONG).show();
        setupApplicantsTab();
        loadApplicantsForSelectedRequest();
    }

    // Screen 5 (A7): confirmation dialog before cancelling a request
    public static void cancelRequest(AppCompatActivity activity, String requestId, Runnable onCancelled) {
        new AlertDialog.Builder(activity)
                .setTitle("Cancel Request")
                .setMessage("Are you sure you want to cancel this request?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // TODO Firebase: WriteBatch to set status = cancelled and reject pending applications
                    FakeDataManager.cancelRequest(requestId);
                    Toast.makeText(activity.getApplicationContext(), "Request cancelled", Toast.LENGTH_LONG).show();
                    if (onCancelled != null) {
                        onCancelled.run();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
