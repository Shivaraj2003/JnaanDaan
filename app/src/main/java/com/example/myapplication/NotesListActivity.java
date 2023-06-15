package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity {
    private DatabaseReference formRef;
    private List<String> branches;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        // Initialize Firebase Realtime Database reference
        formRef = FirebaseDatabase.getInstance().getReference().child("Form");

        // Initialize branches list
        branches = new ArrayList<>();

        // Initialize GridView
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2); // Set the number of columns in the grid

        // Retrieve the branches from the database
        getBranches();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBranch = branches.get(position);
                // Start the SemSubjectActivity and pass the selected branch
                Intent intent = new Intent(NotesListActivity.this, SemSubjectActivity.class);
                intent.putExtra("branch", selectedBranch);
                startActivity(intent);
            }
        });

        View addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Upload activity
                Intent intent = new Intent(NotesListActivity.this, Upload.class);
                startActivity(intent);
            }
        });
    }

    private void getBranches() {
        formRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                branches.clear();

                for (DataSnapshot formSnapshot : dataSnapshot.getChildren()) {
                    String branch = formSnapshot.child("branch").getValue(String.class);

                    if (branch != null && !branches.contains(branch)) {
                        branches.add(branch);
                    }
                }

                // Create an ArrayAdapter to display branches in the GridView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NotesListActivity.this,
                        android.R.layout.simple_list_item_1, branches);
                gridView.setAdapter(adapter);

                // Set item click listener for the gridView
                gridView.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedBranch = branches.get(position);
                    openSemesterActivity(selectedBranch);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void openSemesterActivity(String branch) {
        Intent intent = new Intent(NotesListActivity.this, SemSubjectActivity.class);
        intent.putExtra("branch", branch);
        startActivity(intent);
    }
}