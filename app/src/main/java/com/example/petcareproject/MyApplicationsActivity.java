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
import com.example.petcareproject.utils.FakeDataManager;

import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private RecyclerView rvMyApplications;
    private TextView navOpenRequests, navMyApplications, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_applications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMyApplications = findViewById(R.id.rvMyApplications);
        navOpenRequests = findViewById(R.id.navOpenRequests);
        navMyApplications = findViewById(R.id.navMyApplications);
        navProfile = findViewById(R.id.navProfile);

        // TODO Firebase: query applications where sitterId == currentUserId
        List<Application> myApps = FakeDataManager.getApplicationsBySitter(FakeDataManager.currentSitterId);
        rvMyApplications.setLayoutManager(new LinearLayoutManager(this));
        ApplicationAdapter adapter = new ApplicationAdapter(myApps, application -> {
            Intent intent = new Intent(MyApplicationsActivity.this, RequestDetailSitterActivity.class);
            intent.putExtra("requestId", application.getRequestId());
            startActivity(intent);
        });
        rvMyApplications.setAdapter(adapter);

        navOpenRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, OpenRequestsActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, SitterProfileActivity.class));
            finish();
        });
    }
}
