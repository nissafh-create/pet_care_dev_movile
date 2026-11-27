package com.example.petcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.Constants;
import com.example.petcareproject.utils.FakeDataManager;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + pad, systemBars.top + pad,
                    systemBars.right + pad, systemBars.bottom + pad);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();

            if (emailText.isEmpty()) {
                email.setError("Email is required");
                return;
            }
            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                return;
            }
            if (passwordText.length() < 6) {
                password.setError("At least 6 characters");
                return;
            }

            // TODO Firebase: signInWithEmailAndPassword, then open Owner or Sitter home based on role
            User user = FakeDataManager.getUserByEmail(emailText.trim());
            Intent intent;
            if (user != null && Constants.ROLE_OWNER.equals(user.getRole())) {
                FakeDataManager.currentOwnerId = user.getUserId();
                intent = new Intent(LoginActivity.this, MyRequestsActivity.class);
            } else {
                if (user != null) {
                    FakeDataManager.currentSitterId = user.getUserId();
                }
                intent = new Intent(LoginActivity.this, OpenRequestsActivity.class);
            }
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }
}