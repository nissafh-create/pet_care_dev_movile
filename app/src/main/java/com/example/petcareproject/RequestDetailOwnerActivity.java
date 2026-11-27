package com.example.petcareproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.models.Pet;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.Constants;
import com.example.petcareproject.utils.FakeDataManager;

public class RequestDetailOwnerActivity extends AppCompatActivity {

    private TextView tvBack, tvPetTitle, tvStatus, tvBreedAge, tvCityDates, tvPriceTotal;
    private TextView tvDescription, tvApplicantsCount, tvSitterInitial, tvSitterName, tvSitterInfo;
    private TextView navMyRequests, navPublish, navSitters, navProfile;
    private LinearLayout layoutChosenSitter;
    private Button btnViewApplicants, btnMarkCompleted, btnCancelRequest;

    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_detail_owner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBack = findViewById(R.id.tvBack);
        tvPetTitle = findViewById(R.id.tvPetTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvBreedAge = findViewById(R.id.tvBreedAge);
        tvCityDates = findViewById(R.id.tvCityDates);
        tvPriceTotal = findViewById(R.id.tvPriceTotal);
        tvDescription = findViewById(R.id.tvDescription);
        tvApplicantsCount = findViewById(R.id.tvApplicantsCount);
        btnViewApplicants = findViewById(R.id.btnViewApplicants);
        layoutChosenSitter = findViewById(R.id.layoutChosenSitter);
        tvSitterInitial = findViewById(R.id.tvSitterInitial);
        tvSitterName = findViewById(R.id.tvSitterName);
        tvSitterInfo = findViewById(R.id.tvSitterInfo);
        btnMarkCompleted = findViewById(R.id.btnMarkCompleted);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);
        navMyRequests = findViewById(R.id.navMyRequests);
        navPublish = findViewById(R.id.navPublish);
        navSitters = findViewById(R.id.navSitters);
        navProfile = findViewById(R.id.navProfile);

        requestId = getIntent().getStringExtra("requestId");
        loadDetails();

        tvBack.setOnClickListener(v -> finish());

        btnViewApplicants.setOnClickListener(v -> {
            Intent intent = new Intent(this, SittersActivity.class);
            intent.putExtra("openApplicants", true);
            startActivity(intent);
        });

        btnMarkCompleted.setOnClickListener(v -> showReviewDialog());

        btnCancelRequest.setOnClickListener(v ->
                SittersActivity.cancelRequest(this, requestId, this::loadDetails)
        );

        navMyRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, MyRequestsActivity.class));
            finish();
        });

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
    }

    private void loadDetails() {
        CareRequest req = FakeDataManager.getRequestById(requestId);
        if (req == null) return;

        tvPetTitle.setText(req.getPetName() + " • " + req.getPetSpecies());
        tvStatus.setText(req.getStatus());
        tvCityDates.setText(req.getCity() + " • " + req.getStartDate() + " → " + req.getEndDate());

        long days = FakeDataManager.calculateDays(req.getStartDate(), req.getEndDate());
        int total = (int) (days * req.getPricePerDay());
        tvPriceTotal.setText((int) req.getPricePerDay() + " TND/day • " + total + " TND total");
        tvDescription.setText(req.getDescription());

        Pet pet = FakeDataManager.getPetById(req.getPetId());
        if (pet != null) {
            tvBreedAge.setText(pet.getBreed() + ", " + pet.getAge() + " years");
        }

        int count = FakeDataManager.getApplicationsForRequest(requestId).size();
        tvApplicantsCount.setText(count + " applicants applied");

        if (req.getChosenSitterId() != null) {
            User sitter = FakeDataManager.getUserById(req.getChosenSitterId());
            if (sitter != null) {
                layoutChosenSitter.setVisibility(View.VISIBLE);
                tvSitterName.setText(sitter.getFullName());
                tvSitterInfo.setText(sitter.getCity() + " • ★ " + sitter.getAverageRating());
                if (!sitter.getFullName().isEmpty()) {
                    tvSitterInitial.setText(String.valueOf(sitter.getFullName().charAt(0)).toUpperCase());
                }
            }
        } else {
            layoutChosenSitter.setVisibility(View.GONE);
        }

        // Show buttons only when status allows
        if (Constants.STATUS_CONFIRMED.equals(req.getStatus())) {
            btnMarkCompleted.setVisibility(View.VISIBLE);
        } else {
            btnMarkCompleted.setVisibility(View.GONE);
        }

        if (Constants.STATUS_OPEN.equals(req.getStatus()) || Constants.STATUS_CONFIRMED.equals(req.getStatus())) {
            btnCancelRequest.setVisibility(View.VISIBLE);
        } else {
            btnCancelRequest.setVisibility(View.GONE);
        }
    }

    // Hard flow B4: review dialog + completeRequest
    private void showReviewDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(5.0f);
        LinearLayout.LayoutParams rbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ratingBar.setLayoutParams(rbParams);

        EditText etComment = new EditText(this);
        etComment.setHint("Write a review...");
        etComment.setMinLines(2);

        layout.addView(ratingBar);
        layout.addView(etComment);

        new AlertDialog.Builder(this)
                .setTitle("Leave a Review")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    int rating = (int) ratingBar.getRating();
                    if (rating < 1) rating = 1;
                    String comment = etComment.getText().toString().trim();
                    // TODO Firebase: run Transaction to complete request, add review, update sitter stats
                    FakeDataManager.completeRequest(requestId, FakeDataManager.currentOwnerId, rating, comment);
                    Toast.makeText(getApplicationContext(), "Request completed & review saved!", Toast.LENGTH_LONG).show();
                    loadDetails();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
