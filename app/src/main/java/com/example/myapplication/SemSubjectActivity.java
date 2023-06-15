package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SemSubjectActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1;

    private DatabaseReference formReference;
    private List<Form> forms;
    private GridView semesterGridView;

    private String selectedBranch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_subject);

        // Retrieve the selected branch from the intent
        selectedBranch = getIntent().getStringExtra("branch");

        // Initialize Firebase Realtime Database reference
        formReference = FirebaseDatabase.getInstance().getReference().child("Form");

        // Initialize forms list
        forms = new ArrayList<>();

        // Initialize GridView for semesters
        semesterGridView = findViewById(R.id.semesterGridView);
        semesterGridView.setNumColumns(2);

        // Retrieve the forms for the selected branch from the database
        getForms();

        semesterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSemester = forms.get(position).getSemester();
                openFilesActivity(selectedBranch, selectedSemester);
            }
        });

        // Request the necessary permissions
        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    private void getForms() {
        formReference.orderByChild("branch").equalTo(selectedBranch)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        forms.clear();
                        List<String> uniqueSemesters = new ArrayList<>();

                        for (DataSnapshot formSnapshot : dataSnapshot.getChildren()) {
                            String branch = formSnapshot.child("branch").getValue(String.class);
                            String semester = formSnapshot.child("semester").getValue(String.class);
                            String title = formSnapshot.child("title").getValue(String.class);
                            String fileUrl = formSnapshot.child("fileUrl").getValue(String.class);
                            String uploadDate = formSnapshot.child("uploadDate").getValue(String.class);
                            if (branch != null && semester != null && title != null && fileUrl != null) {
                                Form form = new Form(branch, semester, title, fileUrl, uploadDate);
                                if (!uniqueSemesters.contains(semester)) {
                                    forms.add(form);
                                    uniqueSemesters.add(semester);
                                }
                            }
                        }

                        // Sort the forms list by semester in ascending order
                        Collections.sort(forms, (form1, form2) -> {
                            String sem1 = form1.getSemester();
                            String sem2 = form2.getSemester();
                            return sem1.compareTo(sem2);
                        });

                        // Create an ArrayAdapter to display semesters in the GridView
                        ArrayAdapter<Form> adapter = new ArrayAdapter<Form>(SemSubjectActivity.this,
                                android.R.layout.simple_list_item_1, forms) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                if (convertView == null) {
                                    convertView = LayoutInflater.from(getContext())
                                            .inflate(android.R.layout.simple_list_item_1, parent, false);
                                }

                                TextView titleTextView = convertView.findViewById(android.R.id.text1);
                                Form form = getItem(position);
                                titleTextView.setText(form.getSemester());

                                return convertView;
                            }
                        };

                        semesterGridView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }


    private void openFilesActivity(String branch, String semester) {
        Intent intent = new Intent(SemSubjectActivity.this, FilesActivity.class);
        intent.putExtra("branch", branch);
        intent.putExtra("semester", semester);
        startActivity(intent);
    }
}
