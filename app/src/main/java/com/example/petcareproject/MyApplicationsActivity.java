package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.ApplicationAdapter;
import com.example.petcareproject.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private RecyclerView rvMyApplications;
    private TextView navOpenRequests, navMyApplications, navProfile;
    private ApplicationAdapter adapter;
    private List<Application> applicationList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_applications);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMyApplications = findViewById(R.id.rvMyApplications);
        navOpenRequests = findViewById(R.id.navOpenRequests);
        navMyApplications = findViewById(R.id.navMyApplications);
        navProfile = findViewById(R.id.navProfile);

        rvMyApplications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationAdapter(applicationList, application -> {
            Intent intent = new Intent(MyApplicationsActivity.this, RequestDetailSitterActivity.class);
            intent.putExtra("requestId", application.getRequestId());
            startActivity(intent);
        });
        rvMyApplications.setAdapter(adapter);

        loadMyApplications();

        navOpenRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, OpenRequestsActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, SitterProfileActivity.class));
            finish();
        });
    }

    private void loadMyApplications() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("applications")
                .whereEqualTo("sitterId", currentUser.getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    applicationList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Application app = doc.toObject(Application.class);
                            if (app != null) {
                                app.setApplicationId(doc.getId());
                                applicationList.add(app);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
