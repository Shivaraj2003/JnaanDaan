package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackEditText;
    private LinearLayout feedbackContainer;

    private DatabaseReference feedbackReference;
    private List<Feedback> feedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firebase database reference
        feedbackReference = FirebaseDatabase.getInstance().getReference().child("Feedback");

        // Initialize views
        feedbackEditText = findViewById(R.id.feedbackEditText);
        feedbackContainer = findViewById(R.id.feedbackContainer);

        Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);
        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        // Retrieve and display existing feedbacks
        fetchFeedbacks();
    }

    private void submitFeedback() {
        String feedbackText = feedbackEditText.getText().toString().trim();

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique feedback ID
        String feedbackId = feedbackReference.push().getKey();

        // Get the current user's display name
        String giverName = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            giverName = currentUser.getDisplayName();
        }

        // Get the current timestamp
        String timestamp = getCurrentTimestamp();

        // Create a new Feedback object
        Feedback newFeedback = new Feedback(feedbackId, feedbackText, giverName, timestamp);

        // Store the feedback in the Firebase database
        feedbackReference.child(feedbackId).setValue(newFeedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    feedbackEditText.setText(""); // Clear the feedback text
                    fetchFeedbacks(); // Refresh the feedback list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFeedbacks() {
        // Clear the existing feedbacks
        feedbackContainer.removeAllViews();

        // Initialize feedback list
        feedbackList = new ArrayList<>();

        // Query the feedbacks from the Firebase database
        feedbackReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();

                for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                    String feedbackId = feedbackSnapshot.getKey();
                    String feedbackText = feedbackSnapshot.child("feedbackText").getValue(String.class);
                    String giverName = feedbackSnapshot.child("giverName").getValue(String.class);
                    String timestamp = feedbackSnapshot.child("timestamp").getValue(String.class);

                    if (feedbackId != null && feedbackText != null && giverName != null && timestamp != null) {
                        Feedback feedback = new Feedback(feedbackId, feedbackText, giverName, timestamp);
                        feedbackList.add(feedback);
                    }
                }

                // Display the feedbacks in the UI
                displayFeedbacks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedbackActivity.this, "Failed to fetch feedbacks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFeedbacks() {
        for (Feedback feedback : feedbackList) {
            View feedbackView = getLayoutInflater().inflate(R.layout.feedback_item, null);
            TextView userNameTextView = feedbackView.findViewById(R.id.userNameTextView);
            TextView feedbackTextView = feedbackView.findViewById(R.id.feedbackTextView);
            TextView timestampTextView = feedbackView.findViewById(R.id.timestampTextView);

            userNameTextView.setText(feedback.getGiverName());
            feedbackTextView.setText(feedback.getFeedbackText());
            timestampTextView.setText(feedback.getTimestamp());

            feedbackContainer.addView(feedbackView);
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
