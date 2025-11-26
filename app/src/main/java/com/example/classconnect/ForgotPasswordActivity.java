package com.example.classconnect;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button recoverButton;
    private TextView passwordResult;

    private DatabaseHelper dbHelper;
    private String generatedCode;
    private String userEmail;
    private int step = 1; // 1 = email, 2 = code, 3 = password

    // TODO: IMPORTANT! Replace with YOUR verified SendGrid email
    // Go to SendGrid → Settings → Sender Authentication → Verify a Single Sender
    private static final String SENDGRID_API_KEY = "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";
    private static final String FROM_EMAIL = "jasleenchhabra.ca@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        // Find views
        emailInput = findViewById(R.id.emailForgotInput);
        recoverButton = findViewById(R.id.recoverButton);
        passwordResult = findViewById(R.id.passwordResult);

        // Set initial state
        emailInput.setHint("Enter your email");
        recoverButton.setText("Send Verification Code");
        passwordResult.setText("");
        passwordResult.setTextColor(getResources().getColor(android.R.color.white));
        passwordResult.setTextSize(16);

        recoverButton.setOnClickListener(v -> handleButtonClick());
    }

    private void handleButtonClick() {
        if (step == 1) {
            sendVerificationCode();
        } else if (step == 2) {
            verifyCode();
        } else if (step == 3) {
            resetPassword();
        }
    }

    private void sendVerificationCode() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email exists in database
        Cursor cursor = dbHelper.getUserByEmail(email);

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "Email not found in system", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }
        cursor.close();

        // Generate 6-digit verification code
        Random random = new Random();
        generatedCode = String.format("%06d", random.nextInt(999999));
        userEmail = email;

        // Disable button while sending
        recoverButton.setEnabled(false);
        recoverButton.setText("Sending...");
        passwordResult.setText("Sending verification code to " + email + "...");

        // Send email in background thread
        sendEmailViaSendGrid(email, generatedCode);
    }

    private void sendEmailViaSendGrid(String toEmail, String code) {
        new Thread(() -> {
            try {
                // Check if FROM_EMAIL is configured
                if (FROM_EMAIL.equals("your-verified-email@example.com")) {
                    // Fallback: Show code on screen if no verified email
                    runOnUiThread(() -> {
                        passwordResult.setText("⚠️ Sender email not verified in SendGrid\n\nYour Verification Code:\n\n" + code +
                                "\n\nTo send real emails:\n1. Go to SendGrid → Settings → Sender Authentication\n2. Verify a Single Sender\n3. Update FROM_EMAIL in code");
                        Toast.makeText(this, "Configure sender verification to send emails",
                                Toast.LENGTH_LONG).show();
                        proceedToCodeEntry();
                    });
                    return;
                }

                // Create SendGrid API request
                URL url = new URL("https://api.sendgrid.com/v3/mail/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + SENDGRID_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create email JSON
                JSONObject email = new JSONObject();

                // Personalizations
                JSONArray personalizations = new JSONArray();
                JSONObject personalization = new JSONObject();
                JSONArray toArray = new JSONArray();
                JSONObject toObject = new JSONObject();
                toObject.put("email", toEmail);
                toArray.put(toObject);
                personalization.put("to", toArray);
                personalizations.put(personalization);
                email.put("personalizations", personalizations);

                // From
                JSONObject from = new JSONObject();
                from.put("email", FROM_EMAIL);
                from.put("name", "ClassConnect");
                email.put("from", from);

                // Subject
                email.put("subject", "ClassConnect - Password Reset Code");

                // Content
                JSONArray content = new JSONArray();
                JSONObject contentObject = new JSONObject();
                contentObject.put("type", "text/html");
                contentObject.put("value",
                        "<html><body style='font-family: Arial, sans-serif;'>" +
                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                                "<h2 style='color: #333;'>Password Reset Request</h2>" +
                                "<p>You requested to reset your password for ClassConnect.</p>" +
                                "<p>Your verification code is:</p>" +
                                "<div style='background-color: #f4f4f4; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 20px 0;'>" +
                                code +
                                "</div>" +
                                "<p>This code will expire in 10 minutes.</p>" +
                                "<p>If you didn't request this, please ignore this email.</p>" +
                                "<p>Best regards,<br>ClassConnect Team</p>" +
                                "</div></body></html>"
                );
                content.put(contentObject);
                email.put("content", content);

                // Send request
                OutputStream os = conn.getOutputStream();
                os.write(email.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                // Handle response
                runOnUiThread(() -> {
                    if (responseCode >= 200 && responseCode < 300) {
                        passwordResult.setText("✓ Verification code sent!\n\nCheck your email: " + toEmail);
                        Toast.makeText(this, "Email sent successfully! Check your inbox.",
                                Toast.LENGTH_LONG).show();
                        proceedToCodeEntry();
                    } else {
                        passwordResult.setText("❌ Failed to send email\n\nYour code: " + code +
                                "\n\n(Check SendGrid sender verification)");
                        Toast.makeText(this, "Email failed. Using code shown above.",
                                Toast.LENGTH_LONG).show();
                        proceedToCodeEntry();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    passwordResult.setText("⚠️ Network error\n\nYour Verification Code:\n\n" + code +
                            "\n\n(Check internet connection)");
                    Toast.makeText(this, "Network error. Code shown above.",
                            Toast.LENGTH_LONG).show();
                    proceedToCodeEntry();
                });
            }
        }).start();
    }

    private void proceedToCodeEntry() {
        // Move to step 2
        step = 2;
        emailInput.setText("");
        emailInput.setHint("Enter 6-digit verification code");
        emailInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        recoverButton.setText("Verify Code");
        recoverButton.setEnabled(true);
    }

    private void verifyCode() {
        String enteredCode = emailInput.getText().toString().trim();

        if (enteredCode.isEmpty()) {
            Toast.makeText(this, "Please enter the verification code",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!enteredCode.equals(generatedCode)) {
            Toast.makeText(this, "Invalid verification code. Please try again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Code verified, move to password reset
        Toast.makeText(this, "Code verified! Now enter your new password",
                Toast.LENGTH_SHORT).show();

        step = 3;
        emailInput.setText("");
        emailInput.setHint("Enter new password (min 6 characters)");
        emailInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        recoverButton.setText("Reset Password");
        passwordResult.setText("Code verified ✓\nEnter your new password below");
    }

    private void resetPassword() {
        String newPassword = emailInput.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter a new password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset password in database
        boolean success = dbHelper.resetPassword(userEmail, newPassword);

        if (success) {
            Toast.makeText(this,
                    "Password reset successful!\n\nYou can now login with your new password",
                    Toast.LENGTH_LONG).show();

            // Wait a moment then go to login
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            }, 2000);
        } else {
            Toast.makeText(this, "Failed to reset password. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}