package com.example.petcareproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class RequestDetailOwnerActivity extends AppCompatActivity {

    private TextView tvBack, tvPetTitle, tvStatus, tvBreedAge, tvCityDates, tvPriceTotal;
    private TextView tvDescription, tvApplicantsCount, tvSitterInitial, tvSitterName, tvSitterInfo;
    private TextView navMyRequests, navPublish, navSitters, navProfile;
    private LinearLayout layoutChosenSitter;
    private Button btnViewApplicants, btnMarkCompleted, btnCancelRequest;

    private String requestId;
    private CareRequest currentRequest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_detail_owner);

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

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnViewApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestDetailOwnerActivity.this, SittersActivity.class);
                intent.putExtra("openApplicants", true);
                startActivity(intent);
            }
        });

        btnMarkCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewDialog();
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SittersActivity.cancelRequest(RequestDetailOwnerActivity.this, requestId, new Runnable() {
                    @Override
                    public void run() {
                        loadDetails();
                    }
                });
            }
        });

        navMyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailOwnerActivity.this, MyRequestsActivity.class));
                finish();
            }
        });

        navPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailOwnerActivity.this, PublishRequestActivity.class));
                finish();
            }
        });

        navSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailOwnerActivity.this, SittersActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestDetailOwnerActivity.this, OwnerProfileActivity.class));
                finish();
            }
        });
    }

    private void loadDetails() {
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

                        // Load pet breed/age
                        if (currentRequest.getPetId() != null) {
                            db.collection("pets").document(currentRequest.getPetId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot petDoc) {
                                            if (petDoc.exists()) {
                                                Pet pet = petDoc.toObject(Pet.class);
                                                if (pet != null) {
                                                    tvBreedAge.setText(pet.getBreed() + ", " + pet.getAge() + " years");
                                                }
                                            }
                                        }
                                    });
                        }

                        // Count applicants
                        db.collection("applications").whereEqualTo("requestId", requestId).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot snaps) {
                                        int count = snaps != null ? snaps.size() : 0;
                                        tvApplicantsCount.setText(count + " applicants applied");
                                    }
                                });

                        // Chosen sitter
                        if (currentRequest.getChosenSitterId() != null) {
                            db.collection("users").document(currentRequest.getChosenSitterId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot userDoc) {
                                            if (userDoc.exists()) {
                                                User sitter = userDoc.toObject(User.class);
                                                if (sitter != null) {
                                                    layoutChosenSitter.setVisibility(View.VISIBLE);
                                                    tvSitterName.setText(sitter.getFullName());
                                                    tvSitterInfo.setText(sitter.getCity() + " • ★ " + sitter.getAverageRating());
                                                    if (sitter.getFullName() != null && !sitter.getFullName().isEmpty()) {
                                                        tvSitterInitial.setText(String.valueOf(sitter.getFullName().charAt(0)).toUpperCase());
                                                    }
                                                }
                                            }
                                        }
                                    });
                        } else {
                            layoutChosenSitter.setVisibility(View.GONE);
                        }

                        // Buttons visibility
                        if (Constants.STATUS_CONFIRMED.equals(currentRequest.getStatus())) {
                            btnMarkCompleted.setVisibility(View.VISIBLE);
                        } else {
                            btnMarkCompleted.setVisibility(View.GONE);
                        }

                        if (Constants.STATUS_OPEN.equals(currentRequest.getStatus()) || Constants.STATUS_CONFIRMED.equals(currentRequest.getStatus())) {
                            btnCancelRequest.setVisibility(View.VISIBLE);
                        } else {
                            btnCancelRequest.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // Hard flow B4: completeRequest in Firestore Transaction
    private void showReviewDialog() {
        if (currentRequest == null || currentRequest.getChosenSitterId() == null) return;
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(5.0f);
        LinearLayout.LayoutParams rbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ratingBar.setLayoutParams(rbParams);

        final EditText etComment = new EditText(this);
        etComment.setHint("Write a review...");
        etComment.setMinLines(2);

        layout.addView(ratingBar);
        layout.addView(etComment);

        new AlertDialog.Builder(this)
                .setTitle("Leave a Review")
                .setView(layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rating = (int) ratingBar.getRating();
                        if (rating < 1) rating = 1;
                        final int finalRating = rating;
                        final String comment = etComment.getText().toString().trim();
                        final String sitterId = currentRequest.getChosenSitterId();

                        // Firestore Transaction
                        final DocumentReference reqRef = db.collection("careRequests").document(requestId);
                        final DocumentReference sitterRef = db.collection("users").document(sitterId);
                        final DocumentReference reviewRef = db.collection("reviews").document();

                        final Map<String, Object> reviewData = new HashMap<>();
                        reviewData.put("requestId", requestId);
                        reviewData.put("ownerId", currentUser.getUid());
                        reviewData.put("sitterId", sitterId);
                        reviewData.put("rating", finalRating);
                        reviewData.put("comment", comment);
                        reviewData.put("createdAt", System.currentTimeMillis());

                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot sitterSnap = transaction.get(sitterRef);
                                Long completedCountObj = sitterSnap.getLong("completedCount");
                                Double avgRatingObj = sitterSnap.getDouble("averageRating");

                                int oldCount = completedCountObj != null ? completedCountObj.intValue() : 0;
                                double oldAvg = avgRatingObj != null ? avgRatingObj : 0.0;

                                int newCount = oldCount + 1;
                                double newAvg = (oldAvg * oldCount + finalRating) / newCount;
                                newAvg = Math.round(newAvg * 10.0) / 10.0;

                                String badge = Constants.BADGE_NEW;
                                if (newCount >= 8 && newAvg >= 4.5) {
                                    badge = Constants.BADGE_TRUSTED;
                                } else if (newCount >= 3) {
                                    badge = Constants.BADGE_CONFIRMED;
                                }

                                // 1. Mark request completed
                                transaction.update(reqRef, "status", Constants.STATUS_COMPLETED);
                                // 2. Add review
                                transaction.set(reviewRef, reviewData);
                                // 3. Update sitter stats & badge
                                transaction.update(sitterRef,
                                        "completedCount", newCount,
                                        "averageRating", newAvg,
                                        "badge", badge);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(getApplicationContext(), "Request completed & review saved!", Toast.LENGTH_LONG).show();
                                loadDetails();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Transaction failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
