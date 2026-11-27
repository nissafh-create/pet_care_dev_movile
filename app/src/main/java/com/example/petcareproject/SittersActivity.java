package com.example.petcareproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.ApplicantAdapter;
import com.example.petcareproject.adapters.TopSitterAdapter;
import com.example.petcareproject.models.Application;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SittersActivity extends AppCompatActivity {

    private TextView tabTopSitters, tabApplicants;
    private LinearLayout layoutApplicantsControls;
    private Spinner spRequest;
    private RecyclerView rvSitters;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private List<CareRequest> ownerOpenRequests = new ArrayList<>();
    private ApplicantAdapter applicantAdapter;
    private TopSitterAdapter topSitterAdapter;
    private List<User> topSittersList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sitters);

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

        topSitterAdapter = new TopSitterAdapter(topSittersList);
        applicantAdapter = new ApplicantAdapter(new ArrayList<>(), new ApplicantAdapter.OnChooseClickListener() {
            @Override
            public void onChooseClick(Application application) {
                if (ownerOpenRequests.isEmpty()) return;
                int pos = spRequest.getSelectedItemPosition();
                String requestId = ownerOpenRequests.get(pos).getRequestId();
                chooseSitter(requestId, application.getApplicationId(), application.getSitterId());
            }
        });

        setupTopSittersTab();
        setupApplicantsTab();

        boolean openApplicants = getIntent().getBooleanExtra("openApplicants", false);
        if (openApplicants) {
            showApplicantsTab();
        } else {
            showTopSittersTab();
        }

        tabTopSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopSittersTab();
            }
        });

        tabApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showApplicantsTab();
            }
        });

        navMyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SittersActivity.this, MyRequestsActivity.class));
                finish();
            }
        });

        navPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SittersActivity.this, PublishRequestActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SittersActivity.this, OwnerProfileActivity.class));
                finish();
            }
        });
    }

    private void setupTopSittersTab() {
        db.collection("users")
                .whereEqualTo("role", Constants.ROLE_SITTER)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        topSittersList.clear();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                User u = doc.toObject(User.class);
                                if (u != null) {
                                    u.setUserId(doc.getId());
                                    topSittersList.add(u);
                                }
                            }
                        }
                        Collections.sort(topSittersList, new Comparator<User>() {
                            @Override
                            public int compare(User a, User b) {
                                return Double.compare(b.getAverageRating(), a.getAverageRating());
                            }
                        });
                        topSitterAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupApplicantsTab() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("careRequests")
                .whereEqualTo("ownerId", currentUser.getUid())
                .whereEqualTo("status", Constants.STATUS_OPEN)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ownerOpenRequests.clear();
                        List<String> labels = new ArrayList<>();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                CareRequest r = doc.toObject(CareRequest.class);
                                if (r != null) {
                                    r.setRequestId(doc.getId());
                                    ownerOpenRequests.add(r);
                                    labels.add(r.getPetName() + ", " + r.getPetSpecies() + " (" + r.getStartDate() + " → " + r.getEndDate() + ")");
                                }
                            }
                        }
                        if (labels.isEmpty()) {
                            labels.add("No open requests");
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SittersActivity.this, android.R.layout.simple_spinner_item, labels);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spRequest.setAdapter(spinnerAdapter);

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
                });
    }

    private void loadApplicantsForSelectedRequest() {
        if (ownerOpenRequests.isEmpty()) {
            applicantAdapter.updateList(new ArrayList<>());
            return;
        }
        int pos = spRequest.getSelectedItemPosition();
        if (pos < 0 || pos >= ownerOpenRequests.size()) return;
        String reqId = ownerOpenRequests.get(pos).getRequestId();

        db.collection("applications")
                .whereEqualTo("requestId", reqId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Application> list = new ArrayList<>();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Application app = doc.toObject(Application.class);
                                if (app != null) {
                                    app.setApplicationId(doc.getId());
                                    list.add(app);
                                }
                            }
                        }
                        applicantAdapter.updateList(list);
                    }
                });
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

    // Hard flow A6: WriteBatch -> chosen application accepted, other applications rejected, request confirmed
    private void chooseSitter(final String requestId, final String chosenApplicationId, final String chosenSitterId) {
        db.collection("applications")
                .whereEqualTo("requestId", requestId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.getId().equals(chosenApplicationId)) {
                                    batch.update(doc.getReference(), "status", Constants.APP_ACCEPTED);
                                } else {
                                    batch.update(doc.getReference(), "status", Constants.APP_REJECTED);
                                }
                            }
                        }
                        batch.update(db.collection("careRequests").document(requestId),
                                "status", Constants.STATUS_CONFIRMED,
                                "chosenSitterId", chosenSitterId);

                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Sitter chosen! Request confirmed.", Toast.LENGTH_LONG).show();
                                        setupApplicantsTab();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }

    // Screen 5 (A7): WriteBatch -> cancel request and reject pending applications
    public static void cancelRequest(final AppCompatActivity activity, final String requestId, final Runnable onCancelled) {
        new AlertDialog.Builder(activity)
                .setTitle("Cancel Request")
                .setMessage("Are you sure you want to cancel this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("applications")
                                .whereEqualTo("requestId", requestId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        WriteBatch batch = db.batch();
                                        batch.update(db.collection("careRequests").document(requestId), "status", Constants.STATUS_CANCELLED);
                                        if (queryDocumentSnapshots != null) {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                                if (Constants.APP_PENDING.equals(doc.getString("status"))) {
                                                    batch.update(doc.getReference(), "status", Constants.APP_REJECTED);
                                                }
                                            }
                                        }
                                        batch.commit()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(activity.getApplicationContext(), "Request cancelled", Toast.LENGTH_LONG).show();
                                                        if (onCancelled != null) {
                                                            onCancelled.run();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(activity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
