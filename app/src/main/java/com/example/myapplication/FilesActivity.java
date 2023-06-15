package com.example.myapplication;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;

    private DatabaseReference formReference;
    private List<Form> forms;
    private GridView filesGridView;

    private String selectedBranch;
    private String selectedSemester;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        // Retrieve the selected branch and semester from the intent
        selectedBranch = getIntent().getStringExtra("branch");
        selectedSemester = getIntent().getStringExtra("semester");

        // Initialize Firebase Realtime Database reference
        formReference = FirebaseDatabase.getInstance().getReference().child("Form");

        // Initialize forms list
        forms = new ArrayList<>();

        // Initialize GridView for files
        filesGridView = findViewById(R.id.filesGridView);

        // Retrieve the files for the selected branch and semester from the database
        getFiles();

        filesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFileUrl = forms.get(position).getFileUrl();
                downloadFile(selectedFileUrl);
            }
        });

        // Request the necessary permissions
        requestPermissions();

        // Retrieve the search query from the user
        EditText searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuery = searchEditText.getText().toString().trim();
                filterFiles();
            }
        });

        // Add feedback button click listener
        Button feedbackButton = findViewById(R.id.feedbackButton);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedbackActivity();
            }
        });
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    private void getFiles() {
        Query query = formReference.orderByChild("branch").equalTo(selectedBranch);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forms.clear();

                for (DataSnapshot formSnapshot : dataSnapshot.getChildren()) {
                    String branch = formSnapshot.child("branch").getValue(String.class);
                    String semester = formSnapshot.child("semester").getValue(String.class);
                    String title = formSnapshot.child("title").getValue(String.class);
                    String fileUrl = formSnapshot.child("fileUrl").getValue(String.class);
                    String uploadDate = formSnapshot.child("uploadDate").getValue(String.class);

                    if (branch != null && semester != null && title != null && fileUrl != null) {
                        if (semester.equals(selectedSemester)) {
                            Form form = new Form(branch, semester, title, fileUrl, uploadDate);
                            forms.add(form);
                        }
                    }
                }

                // Create an ArrayAdapter to display file names in the GridView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(FilesActivity.this,
                        android.R.layout.simple_list_item_1, getFileTitles());

                filesGridView.setAdapter(adapter);

                filterFiles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private List<String> getFileTitles() {
        List<String> fileTitles = new ArrayList<>();
        for (Form form : forms) {
            fileTitles.add(form.getTitle());
        }
        return fileTitles;
    }

    private List<Form> filterForms(List<Form> forms, String searchQuery) {
        List<Form> filteredForms = new ArrayList<>();
        for (Form form : forms) {
            if (form.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredForms.add(form);
            }
        }
        return filteredForms;
    }

    private List<String> getFileTitles(List<Form> forms) {
        List<String> fileTitles = new ArrayList<>();
        for (Form form : forms) {
            fileTitles.add(form.getTitle());
        }
        return fileTitles;
    }

    private void filterFiles() {
        if (searchQuery.isEmpty()) {
            // No search query, display all files
            ArrayAdapter<String> adapter = new ArrayAdapter<>(FilesActivity.this,
                    android.R.layout.simple_list_item_1, getFileTitles());

            filesGridView.setAdapter(adapter);
        } else {
            // Filter files based on the search query
            List<Form> filteredForms = filterForms(forms, searchQuery);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(FilesActivity.this,
                    android.R.layout.simple_list_item_1, getFileTitles(filteredForms));

            filesGridView.setAdapter(adapter);
        }
    }

    private void downloadFile(String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.pdf");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    String filePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/file.pdf";
                    openFile(filePath);
                    unregisterReceiver(this);
                }
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void openFile(String filePath) {
        Uri uri = Uri.parse(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void openFeedbackActivity() {
        Intent feedbackIntent = new Intent(FilesActivity.this, FeedbackActivity.class);
        startActivity(feedbackIntent);
    }
}
