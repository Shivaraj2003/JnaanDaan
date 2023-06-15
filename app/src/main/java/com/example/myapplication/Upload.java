package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Form;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

public class Upload extends AppCompatActivity {

    private Spinner spinnerBranch, spinnerSemester;
    private EditText editTextTitle;
    private Button buttonUpload, buttonSubmit, buttonCancel;
    private TextView textViewFileName;

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private Uri fileUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ImageView imageViewFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        editTextTitle = findViewById(R.id.editTextTitle);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonCancel = findViewById(R.id.buttonCancel);
        imageViewFile = findViewById(R.id.imageViewFile);
        textViewFileName = findViewById(R.id.textViewFileName);

        // Populate branch spinner
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        // Populate semester spinner
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this,
                R.array.semester_array, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            textViewFileName.setText("File Name: " + fileName);
            textViewFileName.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private void submitForm() {
        String branch = spinnerBranch.getSelectedItem().toString();
        String semester = spinnerSemester.getSelectedItem().toString();
        String title = editTextTitle.getText().toString();
        if (branch.isEmpty() || semester.isEmpty() || title.isEmpty() ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Return early, do not proceed with form submission
        }
        if (fileUri == null) {
            Toast.makeText(this, "Please choose at least one file", Toast.LENGTH_SHORT).show();
            return; // Return early, no file selected
        }
        if (fileUri != null) {
            // Upload file to Firebase Storage
            Toast.makeText(this, "Uploaded ", Toast.LENGTH_SHORT).show();

            StorageReference fileRef = storageReference.child("uploads/" + fileUri.getLastPathSegment());
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded file
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the form data and file URL in Firebase Database
                            String fileUrl = uri.toString();
                            String uploadDate = getCurrentDate();
                            String username=getLoggedInUsername();
                            Form form = new Form(branch, semester, title, fileUrl,uploadDate);
                            form.setUsername(username); // Set the username
                            // Specify the database reference to save under a "Form" node
                            DatabaseReference formRef = databaseReference.child("Form");
                            formRef.push().setValue(form);
                            //formRef.setValue(form);
                            // Display the uploaded file and show the cancel button
                            Glide.with(Upload.this)
                                    .load(fileUrl)
                                    .into(imageViewFile);
                            imageViewFile.setVisibility(View.VISIBLE);
                            buttonCancel.setVisibility(View.VISIBLE);
                            textViewFileName.setVisibility(View.GONE);

                            // Clear the form
                            spinnerBranch.setSelection(0);
                            spinnerSemester.setSelection(0);
                            editTextTitle.setText("");
                            fileUri = null;
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle upload failure
                    });
        }
    }

    private void onCancelClicked() {
        // Hide the uploaded file, cancel button, and file name
        imageViewFile.setVisibility(View.GONE);
       // buttonCancel.setVisibility(View.GONE);
        textViewFileName.setVisibility(View.GONE);
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getLoggedInUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getDisplayName();
        } else {
            return ""; // Return an empty string or handle the case when the user is not logged in
        }
    }


}
