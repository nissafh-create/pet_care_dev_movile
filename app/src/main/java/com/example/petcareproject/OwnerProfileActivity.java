package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.petcareproject.adapters.PetAdapter;
import com.example.petcareproject.models.Pet;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.FakeDataManager;

import java.util.List;

public class OwnerProfileActivity extends AppCompatActivity {

    private TextView tvInitials, tvFullName, tvCityRole, tvEmail, tvPhone, tvBio;
    private RecyclerView rvPets;
    private Button btnAddPet, btnLogout;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private PetAdapter petAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvInitials = findViewById(R.id.tvInitials);
        tvFullName = findViewById(R.id.tvFullName);
        tvCityRole = findViewById(R.id.tvCityRole);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBio = findViewById(R.id.tvBio);
        rvPets = findViewById(R.id.rvPets);
        btnAddPet = findViewById(R.id.btnAddPet);
        btnLogout = findViewById(R.id.btnLogout);
        navMyRequests = findViewById(R.id.navMyRequests);
        navPublish = findViewById(R.id.navPublish);
        navSitters = findViewById(R.id.navSitters);
        navProfile = findViewById(R.id.navProfile);

        loadOwnerProfile();

        rvPets.setLayoutManager(new LinearLayoutManager(this));
        List<Pet> pets = FakeDataManager.getPetsByOwner(FakeDataManager.currentOwnerId);
        petAdapter = new PetAdapter(pets, pet -> {
            Intent intent = new Intent(this, AddEditPetActivity.class);
            intent.putExtra("petId", pet.getPetId());
            startActivity(intent);
        });
        rvPets.setAdapter(petAdapter);

        btnAddPet.setOnClickListener(v -> startActivity(new Intent(this, AddEditPetActivity.class)));

        btnLogout.setOnClickListener(v -> {
            // TODO Firebase: FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (petAdapter != null) {
            petAdapter.updateList(FakeDataManager.getPetsByOwner(FakeDataManager.currentOwnerId));
        }
    }

    private void loadOwnerProfile() {
        User owner = FakeDataManager.getUserById(FakeDataManager.currentOwnerId);
        if (owner == null) return;

        tvFullName.setText(owner.getFullName());
        tvCityRole.setText(owner.getCity() + " • Owner");
        tvEmail.setText(owner.getEmail());
        tvPhone.setText(owner.getPhone());
        tvBio.setText(owner.getBio());

        String[] parts = owner.getFullName().split(" ");
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
