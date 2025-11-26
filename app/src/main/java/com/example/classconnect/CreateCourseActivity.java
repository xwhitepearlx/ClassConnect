package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

public class CreateCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText  Csis = findViewById(R.id.inputCsis);
        EditText  Code = findViewById(R.id.inputCourseCode);
        EditText  Name = findViewById(R.id.inputCourseName);
        Button btnSave = findViewById(R.id.btnSaveCourse);
        Button btnBack = findViewById(R.id.btnBack);

        btnSave.setOnClickListener(v -> {
                    DatabaseHelper db = new DatabaseHelper(this);
                    String csis = Csis.getText().toString();
                    int code = Integer.parseInt(Code.getText().toString());
                    String name = Name.getText().toString();
                    boolean saved = db.insertCourse(csis, code, name);

                    if (saved) {
                        Toast.makeText(this, "Course added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save course.", Toast.LENGTH_SHORT).show();
                    }
                });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(CreateCourseActivity.this, CoursesActivity.class));
        });
    }
}