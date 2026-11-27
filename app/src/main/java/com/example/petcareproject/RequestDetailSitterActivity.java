package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class RequestDetailSitterActivity extends AppCompatActivity {

    private TextView tvBack, tvPetTitle, tvStatus, tvBreedAge, tvCityDates, tvPriceTotal;
    private TextView tvDescription, tvSpecialNeeds, tvOwnerInitial, tvOwnerName, tvOwnerCity;
    private TextView navOpenRequests, navMyApplications, navProfile;
    private EditText etMessage;
    private Button btnVolunteer;

    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_detail_sitter);

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
        tvSpecialNeeds = findViewById(R.id.tvSpecialNeeds);
        tvOwnerInitial = findViewById(R.id.tvOwnerInitial);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvOwnerCity = findViewById(R.id.tvOwnerCity);
        etMessage = findViewById(R.id.etMessage);
        btnVolunteer = findViewById(R.id.btnVolunteer);
        navOpenRequests = findViewById(R.id.navOpenRequests);
        navMyApplications = findViewById(R.id.navMyApplications);
        navProfile = findViewById(R.id.navProfile);

        requestId = getIntent().getStringExtra("requestId");
        loadRequestDetails();

        tvBack.setOnClickListener(v -> finish());

        btnVolunteer.setOnClickListener(v -> {
            if (requestId == null) return;

            // Prevent duplicate application
            if (FakeDataManager.hasSitterApplied(requestId, FakeDataManager.currentSitterId)) {
                Toast.makeText(getApplicationContext(), "You already applied to this request", Toast.LENGTH_LONG).show();
                return;
            }

            String message = etMessage.getText().toString().trim();
            // TODO Firebase: add document in applications collection
            FakeDataManager.addApplication(requestId, FakeDataManager.currentSitterId, message);
            Toast.makeText(getApplicationContext(), "Application sent!", Toast.LENGTH_LONG).show();
            btnVolunteer.setEnabled(false);
            btnVolunteer.setText("Applied");
        });

        navOpenRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, OpenRequestsActivity.class));
            finish();
        });

        navMyApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, SitterProfileActivity.class));
            finish();
        });
    }

    private void loadRequestDetails() {
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
            tvSpecialNeeds.setText("Special needs: " + pet.getSpecialNeeds());
        }

        User owner = FakeDataManager.getUserById(req.getOwnerId());
        if (owner != null) {
            tvOwnerName.setText(owner.getFullName());
            tvOwnerCity.setText(owner.getCity());
            if (!owner.getFullName().isEmpty()) {
                tvOwnerInitial.setText(String.valueOf(owner.getFullName().charAt(0)).toUpperCase());
            }
        }

        if (!Constants.STATUS_OPEN.equals(req.getStatus())) {
            btnVolunteer.setEnabled(false);
            btnVolunteer.setText("Closed");
        } else if (FakeDataManager.hasSitterApplied(requestId, FakeDataManager.currentSitterId)) {
            btnVolunteer.setEnabled(false);
            btnVolunteer.setText("Already applied");
        }
    }
}
