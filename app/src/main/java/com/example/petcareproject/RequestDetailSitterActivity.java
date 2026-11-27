package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.models.Pet;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.Constants;
import com.example.petcareproject.utils.FakeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RequestDetailSitterActivity extends AppCompatActivity {

    private TextView tvBack, tvPetTitle, tvStatus, tvBreedAge, tvCityDates, tvPriceTotal;
    private TextView tvDescription, tvSpecialNeeds, tvOwnerInitial, tvOwnerName, tvOwnerCity;
    private TextView navOpenRequests, navMyApplications, navProfile;
    private EditText etMessage;
    private Button btnVolunteer;

    private String requestId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CareRequest currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_detail_sitter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
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

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestId == null || currentRequest == null) return;
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) return;

                btnVolunteer.setEnabled(false);
                String message = etMessage.getText().toString().trim();

                // Fetch current sitter's name from user profile
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot userDoc) {
                                String sitterName = userDoc.exists() && userDoc.getString("fullName") != null
                                        ? userDoc.getString("fullName")
                                        : "Sitter";

                                Map<String, Object> application = new HashMap<>();
                                application.put("requestId", requestId);
                                application.put("sitterId", currentUser.getUid());
                                application.put("sitterName", sitterName);
                                application.put("message", message);
                                application.put("status", Constants.APP_PENDING);
                                application.put("createdAt", System.currentTimeMillis());

                                db.collection("applications").add(application)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference docRef) {
                                                Toast.makeText(getApplicationContext(), "Application sent!", Toast.LENGTH_LONG).show();
                                                btnVolunteer.setText("Applied");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                btnVolunteer.setEnabled(true);
                                                Toast.makeText(getApplicationContext(), "Failed to send: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnVolunteer.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        navOpenRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailSitterActivity.this, OpenRequestsActivity.class));
                finish();
            }
        });

        navMyApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailSitterActivity.this, MyApplicationsActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailSitterActivity.this, SitterProfileActivity.class));
                finish();
            }
        });
    }

    private void loadRequestDetails() {
        if (requestId == null) return;

        db.collection("careRequests").document(requestId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (!doc.exists()) return;
                        currentRequest = doc.toObject(CareRequest.class);
                        if (currentRequest == null) return;
                        currentRequest.setRequestId(doc.getId());

                        tvPetTitle.setText(currentRequest.getPetName() + " • " + currentRequest.getPetSpecies());
                        tvStatus.setText(currentRequest.getStatus());
                        tvCityDates.setText(currentRequest.getCity() + " • " + currentRequest.getStartDate() + " → " + currentRequest.getEndDate());

                        long days = FakeDataManager.calculateDays(currentRequest.getStartDate(), currentRequest.getEndDate());
                        int total = (int) (days * currentRequest.getPricePerDay());
                        tvPriceTotal.setText((int) currentRequest.getPricePerDay() + " TND/day • " + total + " TND total");
                        tvDescription.setText(currentRequest.getDescription());

                        // Load pet details
                        if (currentRequest.getPetId() != null) {
                            db.collection("pets").document(currentRequest.getPetId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot petDoc) {
                                            if (petDoc.exists()) {
                                                Pet pet = petDoc.toObject(Pet.class);
                                                if (pet != null) {
                                                    tvBreedAge.setText(pet.getBreed() + ", " + pet.getAge() + " years");
                                                    tvSpecialNeeds.setText("Special needs: " + pet.getSpecialNeeds());
                                                }
                                            }
                                        }
                                    });
                        }

                        // Load owner details
                        if (currentRequest.getOwnerId() != null) {
                            db.collection("users").document(currentRequest.getOwnerId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot ownerDoc) {
                                            if (ownerDoc.exists()) {
                                                User owner = ownerDoc.toObject(User.class);
                                                if (owner != null) {
                                                    tvOwnerName.setText(owner.getFullName());
                                                    tvOwnerCity.setText(owner.getCity());
                                                    if (owner.getFullName() != null && !owner.getFullName().isEmpty()) {
                                                        tvOwnerInitial.setText(String.valueOf(owner.getFullName().charAt(0)).toUpperCase());
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        // Check if request is closed
                        if (!Constants.STATUS_OPEN.equals(currentRequest.getStatus())) {
                            btnVolunteer.setEnabled(false);
                            btnVolunteer.setText("Closed");
                        } else {
                            checkIfAlreadyApplied();
                        }
                    }
                });
    }

    private void checkIfAlreadyApplied() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("applications")
                .whereEqualTo("requestId", requestId)
                .whereEqualTo("sitterId", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        if (snaps != null && !snaps.isEmpty()) {
                            btnVolunteer.setEnabled(false);
                            btnVolunteer.setText("Already applied");
                        }
                    }
                });
    }
}
