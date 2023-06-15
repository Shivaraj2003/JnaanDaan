package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DateTimeActivity extends AppCompatActivity {
    private EditText titleEditText;
    private Spinner branchSpinner;
    private Spinner semesterSpinner;
    private EditText subjectEditText;
    private Button chooseFileButton;
    private TextView selectedFileTextView;
    private Button uploadButton;

    private DatabaseReference notesRef;

    private static final int FILE_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        // Initialize Firebase Realtime Database reference
        notesRef = FirebaseDatabase.getInstance().getReference().child("notes");

        // Find views by their IDs
        titleEditText = findViewById(R.id.titleEditText);
        branchSpinner = findViewById(R.id.branchSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        subjectEditText = findViewById(R.id.subjectEditText);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        selectedFileTextView = findViewById(R.id.selectedFileTextView);
        uploadButton = findViewById(R.id.uploadButton);

        // Set up branch spinner with options
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch_options, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        // Set up semester spinner with options
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this,
                R.array.semester_options, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        // Set click listener for choose file button
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch file chooser intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_REQUEST_CODE);
            }
        });

        // Set click listener for upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the note title from the titleEditText
                String noteTitle = titleEditText.getText().toString();

                // Get the selected branch and semester from the spinners
                String branch = branchSpinner.getSelectedItem().toString();
                String semester = semesterSpinner.getSelectedItem().toString();

                // Get the subject from the subjectEditText
                String subject = subjectEditText.getText().toString();

                // Get the selected file URI
                String selectedFilePath = ""; // Placeholder for file path, update it based on the selected file
                if (noteTitle.isEmpty() || branch.isEmpty() || semester.isEmpty() || subject.isEmpty()) {
                    // Show an error message or perform appropriate actions
                    Toast.makeText(DateTimeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a unique note ID
                String noteId = notesRef.push().getKey();

                // Create a Note object with the information

                notes note = new notes(branch, semester, noteTitle, subject);

                // Store the note in the database
                notesRef.child(noteId).setValue(note);

                // Display a success message or perform any other actions

                // Finish the activity to return to the previous screen
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected file URI
            Uri fileUri = data.getData();
            String selectedFileName = fileUri.getLastPathSegment();

            // Update the selected file text view
            selectedFileTextView.setText("Selected File: " + selectedFileName);
            selectedFileTextView.setVisibility(View.VISIBLE);

            // Save the file path to be used later during upload
            // Replace the placeholder variable with your file path handling logic
            String selectedFilePath = fileUri.getPath();
        }
    }
}
