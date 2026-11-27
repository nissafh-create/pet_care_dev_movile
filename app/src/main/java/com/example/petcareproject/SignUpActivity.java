package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullName, email, phone, city, password, confirmPassword, bio;
    private RadioButton rbOwner;
    private Button btnSignUp;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + pad, systemBars.top + pad,
                    systemBars.right + pad, systemBars.bottom + pad);
            return insets;
        });

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        bio = findViewById(R.id.bio);
        rbOwner = findViewById(R.id.rbOwner);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        btnSignUp.setOnClickListener(v -> {
            String name = fullName.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String phoneText = phone.getText().toString().trim();
            String cityText = city.getText().toString().trim();
            String pass = password.getText().toString();
            String confirm = confirmPassword.getText().toString();
            String role = rbOwner.isChecked() ? "owner" : "sitter";

            if (name.isEmpty()) {
                fullName.setError("Full name is required");
                return;
            }
            if (emailText.isEmpty()) {
                email.setError("Email is required");
                return;
            }
            if (phoneText.isEmpty()) {
                phone.setError("Phone is required");
                return;
            }
            if (cityText.isEmpty()) {
                city.setError("City is required");
                return;
            }
            if (pass.length() < 6) {
                password.setError("At least 6 characters");
                return;
            }
            if (!pass.equals(confirm)) {
                confirmPassword.setError("Passwords don't match");
                return;
            }

            btnSignUp.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(emailText, pass)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        User newUser;
                        if ("sitter".equals(role)) {
                            newUser = new User(uid, name, emailText, phoneText, cityText, role, userBio,
                                    Arrays.asList("Cat", "Dog"), 0, 0.0, Constants.BADGE_NEW);
                        } else {
                            newUser = new User(uid, name, emailText, phoneText, cityText, role, userBio);
                        }

                        db.collection("users").document(uid).set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_LONG).show();
                                    if (Constants.ROLE_OWNER.equals(role)) {
                                        startActivity(new Intent(SignUpActivity.this, MyRequestsActivity.class));
                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, OpenRequestsActivity.class));
                                    }
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    btnSignUp.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        btnSignUp.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        tvLogin.setOnClickListener(v ->
                finish()
        );
    }
}