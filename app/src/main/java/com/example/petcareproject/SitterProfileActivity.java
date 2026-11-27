package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.ReviewAdapter;
import com.example.petcareproject.models.Review;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.FakeDataManager;

import java.util.List;

public class SitterProfileActivity extends AppCompatActivity {

    private TextView tvInitials, tvFullName, tvCityRole, tvBadge, tvAvgRating, tvCompletedCount;
    private TextView tvBio, tvAcceptedAnimals;
    private RecyclerView rvReviews;
    private Button btnLogout;
    private TextView navOpenRequests, navMyApplications, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sitter_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvInitials = findViewById(R.id.tvInitials);
        tvFullName = findViewById(R.id.tvFullName);
        tvCityRole = findViewById(R.id.tvCityRole);
        tvBadge = findViewById(R.id.tvBadge);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvBio = findViewById(R.id.tvBio);
        tvAcceptedAnimals = findViewById(R.id.tvAcceptedAnimals);
        rvReviews = findViewById(R.id.rvReviews);
        btnLogout = findViewById(R.id.btnLogout);
        navOpenRequests = findViewById(R.id.navOpenRequests);
        navMyApplications = findViewById(R.id.navMyApplications);
        navProfile = findViewById(R.id.navProfile);

        loadSitterProfile();

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        List<Review> reviews = FakeDataManager.getReviewsForSitter(FakeDataManager.currentSitterId);
        rvReviews.setAdapter(new ReviewAdapter(reviews));

        btnLogout.setOnClickListener(v -> {
            // TODO Firebase: FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        navOpenRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, OpenRequestsActivity.class));
            finish();
        });

        navMyApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            finish();
        });
    }

    private void loadSitterProfile() {
        User sitter = FakeDataManager.getUserById(FakeDataManager.currentSitterId);
        if (sitter == null) return;

        tvFullName.setText(sitter.getFullName());
        tvCityRole.setText(sitter.getCity() + " • Sitter");
        tvBadge.setText(sitter.getBadge() + " Sitter");
        tvAvgRating.setText("★ " + sitter.getAverageRating());
        tvCompletedCount.setText(String.valueOf(sitter.getCompletedCount()));
        tvBio.setText(sitter.getBio());

        if (sitter.getAcceptedAnimals() != null && !sitter.getAcceptedAnimals().isEmpty()) {
            tvAcceptedAnimals.setText(TextUtils.join(" • ", sitter.getAcceptedAnimals()));
        } else {
            tvAcceptedAnimals.setText("");
        }

        String[] parts = sitter.getFullName().split(" ");
        String initials = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials += parts[0].charAt(0);
        }
        if (parts.length > 1 && !parts[1].isEmpty()) {
            initials += parts[1].charAt(0);
        }
        tvInitials.setText(initials.toUpperCase());
    }
}
