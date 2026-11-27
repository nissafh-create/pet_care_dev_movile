package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.CareRequestAdapter;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.utils.FakeDataManager;

import java.util.ArrayList;
import java.util.List;

public class OpenRequestsActivity extends AppCompatActivity {

    private Spinner spCity, spAnimal;
    private EditText etMaxPrice;
    private RecyclerView rvOpenRequests;
    private TextView navOpenRequests, navMyApplications, navProfile;

    private CareRequestAdapter adapter;
    private List<CareRequest> allOpenRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_requests);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spCity = findViewById(R.id.spCity);
        spAnimal = findViewById(R.id.spAnimal);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        rvOpenRequests = findViewById(R.id.rvOpenRequests);
        navOpenRequests = findViewById(R.id.navOpenRequests);
        navMyApplications = findViewById(R.id.navMyApplications);
        navProfile = findViewById(R.id.navProfile);

        // Setup filter spinners
        String[] cities = {"All cities", "Tunis", "Ariana", "Sousse", "Sfax"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cityAdapter);

        String[] animals = {"All pets", "Dog", "Cat", "Bird", "Rabbit"};
        ArrayAdapter<String> animalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, animals);
        animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAnimal.setAdapter(animalAdapter);

        // Setup RecyclerView
        allOpenRequests = FakeDataManager.getOpenRequests();
        rvOpenRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CareRequestAdapter(allOpenRequests, request -> {
            Intent intent = new Intent(OpenRequestsActivity.this, RequestDetailSitterActivity.class);
            intent.putExtra("requestId", request.getRequestId());
            startActivity(intent);
        });
        rvOpenRequests.setAdapter(adapter);

        // Filter listeners
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spCity.setOnItemSelectedListener(filterListener);
        spAnimal.setOnItemSelectedListener(filterListener);

        etMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Bottom navigation clicks
        navMyApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, SitterProfileActivity.class));
            finish();
        });
    }

    private void applyFilters() {
        String selectedCity = spCity.getSelectedItem().toString();
        String selectedAnimal = spAnimal.getSelectedItem().toString();
        String maxPriceStr = etMaxPrice.getText().toString().trim();

        double maxPrice = -1;
        if (!maxPriceStr.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException ignored) {
            }
        }

        // TODO Firebase: replace local filtering with Firestore whereEqualTo / whereLessThanOrEqualTo
        List<CareRequest> filtered = new ArrayList<>();
        for (CareRequest r : allOpenRequests) {
            boolean matchesCity = selectedCity.equals("All cities") || r.getCity().equalsIgnoreCase(selectedCity);
            boolean matchesAnimal = selectedAnimal.equals("All pets") || r.getPetSpecies().equalsIgnoreCase(selectedAnimal);
            boolean matchesPrice = maxPrice < 0 || r.getPricePerDay() <= maxPrice;

            if (matchesCity && matchesAnimal && matchesPrice) {
                filtered.add(r);
            }
        }
        adapter.updateList(filtered);
    }
}
