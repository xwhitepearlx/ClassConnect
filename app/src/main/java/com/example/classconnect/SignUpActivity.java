package com.example.classconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.repositories.UserRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SignUpActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button buttonSelectImage, buttonRegister;
    private TextView buttonLogin;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri = null; // Store selected image URI

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput, interestInput;
    private RadioGroup roleGroup;

    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize views
        imagePreview = findViewById(R.id.imageRegisterPreview);
        buttonSelectImage = findViewById(R.id.addImageRegisterButton);
        buttonRegister = findViewById(R.id.registerRegisterButton);

        nameInput = findViewById(R.id.nameRegisterInput);
        emailInput = findViewById(R.id.emailRegisterInput);
        passwordInput = findViewById(R.id.passwordRegisterInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordRegisterInput);
        interestInput = findViewById(R.id.interestRegisterInput);
        roleGroup = findViewById(R.id.roleRegisterGroup);

        buttonLogin = findViewById(R.id.tvLogInNow);

        // Initialize repository
        userRepo = new UserRepository(this);

        // Initialize image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imagePreview.setImageURI(imageUri);
                    }
                }
        );

        // Button Navigation
        buttonSelectImage.setOnClickListener(v -> openImagePicker());
        buttonRegister.setOnClickListener(v -> registerUser());
        buttonLogin.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, TemporaryDashboard.class)));
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String passwordConfirm = confirmPasswordInput.getText().toString().trim();
        String interest = interestInput.getText().toString().trim();
        String cleanedInterests = Arrays.stream(interest.split(","))
                .map(String::trim) // remove spaces around each word
                .filter(s -> !s.isEmpty()) // skip empty strings
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()) // capitalize first letter
                .collect(Collectors.joining(", "));

        // Validate required fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Name validation
        if (!name.matches("^[\\p{L} ]+$")) {
            Toast.makeText(this, "Name can only contain letters and spaces", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (userRepo.checkEmailExists(email)) {
            Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            return;
        }

        // Password validation
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Please enter match password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Interest validation
        if (!interest.matches("^[a-zA-Z0-9]+(, ?[a-zA-Z0-9]+)*$")) {
            Toast.makeText(this, "Interest must be words separated by single commas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine role
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getText().toString();

        // Convert optional image URI to string (nullable)
        String pictureUrl = (imageUri != null) ? imageUri.toString() : null;

        // Insert user using repository
        long resultId = userRepo.insertUser(name, email, password, role, cleanedInterests, pictureUrl);

        if (resultId != -1) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish(); // close activity or navigate to login/home
        } else {
            Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
        }

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

}