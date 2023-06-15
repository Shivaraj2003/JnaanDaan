package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etCollege, etEmail, etPassword;
    private Button btnRegister;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /// super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//         Initialize FirebaseApp if it's not already initialized
//        if (FirebaseApp.getApps(this).isEmpty()) {
//            FirebaseApp.initializeApp(this);
//        }

        // Initialize views
        etName = findViewById(R.id.etname);
        etCollege = findViewById(R.id.etcollege);
        etEmail = findViewById(R.id.etemail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnregister);

        // Get a reference to the Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input from form fields
                String name = etName.getText().toString();
                String college = etCollege.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // Perform validation on user input
                if (name.isEmpty() || college.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    // Display error message if any field is empty
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Registration successful, save user details to Firebase


                    saveUserDetails(name, college, email, password);
                }
            }
        });
    }

    private void saveUserDetails(String name, String college, String email, String password) {
        // Generate a unique ID for the user using the push() method


        String userId = databaseRef.push().getKey();

        // Create a User object with the provided details
        User user = new User(name, college, email, password);

        // Save the user object to the Firebase Realtime Database
        databaseRef.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // User registration data stored successfully
                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the sign-up activity and go back to the previous activity
                } else {
                    // Failed to store user registration data
                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this,"Registeration Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
