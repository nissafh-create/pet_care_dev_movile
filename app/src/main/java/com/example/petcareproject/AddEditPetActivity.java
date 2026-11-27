package com.example.petcareproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcareproject.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddEditPetActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle;
    private EditText etPetName, etBreed, etAge, etSpecialNeeds;
    private Spinner spSpecies;
    private Button btnSavePet;

    private String petId;
    private final String[] speciesList = {"Dog", "Cat", "Bird", "Rabbit"};
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_pet);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        etPetName = findViewById(R.id.etPetName);
        spSpecies = findViewById(R.id.spSpecies);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);
        etSpecialNeeds = findViewById(R.id.etSpecialNeeds);
        btnSavePet = findViewById(R.id.btnSavePet);

        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, speciesList);
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpecies.setAdapter(speciesAdapter);

        petId = getIntent().getStringExtra("petId");
        if (petId != null) {
            tvTitle.setText("Edit Pet");
            loadPet(petId);
        }

        tvBack.setOnClickListener(v -> finish());
        btnSavePet.setOnClickListener(v -> savePet());
    }

    private void loadPet(String id) {
        db.collection("pets").document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Pet pet = doc.toObject(Pet.class);
                        if (pet != null) {
                            etPetName.setText(pet.getName());
                            etBreed.setText(pet.getBreed());
                            etAge.setText(String.valueOf(pet.getAge()));
                            etSpecialNeeds.setText(pet.getSpecialNeeds());

                            for (int i = 0; i < speciesList.length; i++) {
                                if (speciesList[i].equalsIgnoreCase(pet.getSpecies())) {
                                    spSpecies.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void savePet() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String name = etPetName.getText().toString().trim();
        String species = spSpecies.getSelectedItem().toString();
        String breed = etBreed.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String specialNeeds = etSpecialNeeds.getText().toString().trim();

        if (name.isEmpty()) {
            etPetName.setError("Pet name is required");
            return;
        }
        if (breed.isEmpty()) {
            etBreed.setError("Breed is required");
            return;
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Age is required");
            return;
        }

        int age = Integer.parseInt(ageStr);
        if (specialNeeds.isEmpty()) {
            specialNeeds = "None";
        }

        btnSavePet.setEnabled(false);
        Pet pet = new Pet(petId, currentUser.getUid(), name, species, breed, age, specialNeeds);

        if (petId == null || petId.isEmpty()) {
            db.collection("pets").add(pet)
                    .addOnSuccessListener(docRef -> {
                        docRef.update("petId", docRef.getId());
                        Toast.makeText(getApplicationContext(), "Pet saved!", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSavePet.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            pet.setPetId(petId);
            db.collection("pets").document(petId).set(pet)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Pet updated!", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSavePet.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}
