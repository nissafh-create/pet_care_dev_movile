package com.example.petcareproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.petcareproject.utils.Constants;
import com.example.petcareproject.utils.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PublishRequestActivity extends AppCompatActivity {

    private Spinner spPet;
    private TextView tvAddPet, tvSummary;
    private EditText etStartDate, etEndDate, etCity, etPricePerDay, etDescription;
    private Button btnPublish;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private List<Pet> ownerPets = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_publish_request);

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

        spPet = findViewById(R.id.spPet);
        tvAddPet = findViewById(R.id.tvAddPet);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etCity = findViewById(R.id.etCity);
        etPricePerDay = findViewById(R.id.etPricePerDay);
        etDescription = findViewById(R.id.etDescription);
        tvSummary = findViewById(R.id.tvSummary);
        btnPublish = findViewById(R.id.btnPublish);
        navMyRequests = findViewById(R.id.navMyRequests);
        navPublish = findViewById(R.id.navPublish);
        navSitters = findViewById(R.id.navSitters);
        navProfile = findViewById(R.id.navProfile);

        loadPetsSpinner();

        tvAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PublishRequestActivity.this, AddEditPetActivity.class));
            }
        });

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etStartDate);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etEndDate);
            }
        });

        TextWatcher summaryWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSummary();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        etStartDate.addTextChangedListener(summaryWatcher);
        etEndDate.addTextChangedListener(summaryWatcher);
        etPricePerDay.addTextChangedListener(summaryWatcher);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishRequest();
            }
        });

        navMyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PublishRequestActivity.this, MyRequestsActivity.class));
                finish();
            }
        });

        navSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PublishRequestActivity.this, SittersActivity.class));
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PublishRequestActivity.this, OwnerProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPetsSpinner();
    }

    private void loadPetsSpinner() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("pets")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ownerPets.clear();
                        List<String> petLabels = new ArrayList<>();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Pet p = doc.toObject(Pet.class);
                                if (p != null) {
                                    p.setPetId(doc.getId());
                                    ownerPets.add(p);
                                    petLabels.add(p.getName() + ", " + p.getSpecies());
                                }
                            }
                        }
                        if (petLabels.isEmpty()) {
                            petLabels.add("No pets found");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PublishRequestActivity.this, android.R.layout.simple_spinner_item, petLabels);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spPet.setAdapter(adapter);
                    }
                });
    }

    private void showDatePicker(final EditText target) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                target.setText(date);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateSummary() {
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();
        String priceStr = etPricePerDay.getText().toString().trim();

        if (start.isEmpty() || end.isEmpty() || priceStr.isEmpty()) {
            tvSummary.setText("Duration: 0 days • Estimated total: 0 TND");
            return;
        }

        long days = DateUtils.calculateDays(start, end);
        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ignored) {
        }
        int total = (int) (days * price);
        tvSummary.setText("Duration: " + days + " days • Estimated total: " + total + " TND");
    }

    private void publishRequest() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        if (ownerPets.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please add a pet first", Toast.LENGTH_LONG).show();
            return;
        }

        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String priceStr = etPricePerDay.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (start.isEmpty()) {
            etStartDate.setError("Start date is required");
            return;
        }
        if (end.isEmpty()) {
            etEndDate.setError("End date is required");
            return;
        }
        if (city.isEmpty()) {
            etCity.setError("City is required");
            return;
        }
        if (priceStr.isEmpty()) {
            etPricePerDay.setError("Price is required");
            return;
        }

        Pet selectedPet = ownerPets.get(spPet.getSelectedItemPosition());
        double price = Double.parseDouble(priceStr);

        btnPublish.setEnabled(false);
        CareRequest request = new CareRequest(
                null,
                currentUser.getUid(),
                selectedPet.getPetId(),
                selectedPet.getName(),
                selectedPet.getSpecies(),
                city,
                start,
                end,
                price,
                description,
                Constants.STATUS_OPEN,
                null
        );

        db.collection("careRequests").add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference docRef) {
                        docRef.update("requestId", docRef.getId());
                        Toast.makeText(getApplicationContext(), "Request published!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PublishRequestActivity.this, MyRequestsActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnPublish.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Failed to publish: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
