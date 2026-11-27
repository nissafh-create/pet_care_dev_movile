package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.adapters.PetAdapter;
import com.example.petcareproject.models.Pet;
import com.example.petcareproject.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerProfileActivity extends AppCompatActivity {

    private TextView tvInitials, tvFullName, tvCityRole, tvEmail, tvPhone, tvBio;
    private RecyclerView rvPets;
    private Button btnAddPet, btnLogout;
    private TextView navMyRequests, navPublish, navSitters, navProfile;

    private PetAdapter petAdapter;
    private List<Pet> petsList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_profile);

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

        rvPets.setLayoutManager(new LinearLayoutManager(this));
        petAdapter = new PetAdapter(petsList, new PetAdapter.OnPetEditListener() {
            @Override
            public void onEditPet(Pet pet) {
                Intent intent = new Intent(OwnerProfileActivity.this, AddEditPetActivity.class);
                intent.putExtra("petId", pet.getPetId());
                startActivity(intent);
            }
        });
        rvPets.setAdapter(petAdapter);

        loadOwnerProfile();
        loadPets();

        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerProfileActivity.this, AddEditPetActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OwnerProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        navMyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerProfileActivity.this, MyRequestsActivity.class));
                finish();
            }
        });

        navPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerProfileActivity.this, PublishRequestActivity.class));
                finish();
            }
        });

        navSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerProfileActivity.this, SittersActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPets();
    }

    private void loadOwnerProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (doc.exists()) {
                            User owner = doc.toObject(User.class);
                            if (owner != null) {
                                tvFullName.setText(owner.getFullName());
                                tvCityRole.setText(owner.getCity() + " • Owner");
                                tvEmail.setText(owner.getEmail());
                                tvPhone.setText(owner.getPhone());
                                tvBio.setText(owner.getBio());

                                if (owner.getFullName() != null) {
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
                        }
                    }
                });
    }

    private void loadPets() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("pets")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        petsList.clear();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Pet p = doc.toObject(Pet.class);
                                if (p != null) {
                                    p.setPetId(doc.getId());
                                    petsList.add(p);
                                }
                            }
                        }
                        petAdapter.notifyDataSetChanged();
                    }
                });
    }
}
