package com.example.test;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotesActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private int loggedInUserId;
    private String loggedInUserName;
    private LinearLayout notesContainer;
    private TextView dateText;
    private EditText newNoteEditText;
    private Button addNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes); // Ensure this is the correct layout file

        // Initialize UI components
        dateText = findViewById(R.id.dateText);
        notesContainer = findViewById(R.id.notesContainer);
        newNoteEditText = findViewById(R.id.editTextText);
        addNoteButton = findViewById(R.id.addNoteButton);

        // Get the logged-in user ID and name from the intent
        Intent intent = getIntent();
        loggedInUserId = intent.getIntExtra("loggedInUserId", -1);
        loggedInUserName = intent.getStringExtra("loggedInUserName");

        if (loggedInUserId == -1 || loggedInUserName == null) {
            Toast.makeText(this, "Error: User information not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("NotesActivity", "Logged-in User ID: " + loggedInUserId);
        Log.d("NotesActivity", "Logged-in User Name: " + loggedInUserName);

        // Display the user's name in the top bar
        TextView userNameTextView = findViewById(R.id.userName);
        userNameTextView.setText("Welcome, " + loggedInUserName);

        // Open or create the database
        db = openOrCreateDatabase("UserNotes.db", MODE_PRIVATE, null);
        createNotesTableIfNotExists();

        // Load existing notes for the user
        loadNotes();

        // Set up the add note button listener
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
    }

    private void createNotesTableIfNotExists() {
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "note TEXT, " +
                "date TEXT, " +
                "user_id INTEGER)");
    }

    private void loadNotes() {
        // Clear any existing views in the container
        notesContainer.removeAllViews();

        // Query notes and dates for the logged-in user
        Cursor notesCursor = db.rawQuery(
                "SELECT note, date FROM notes WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(loggedInUserId)}
        );

        while (notesCursor.moveToNext()) {
            String noteText = notesCursor.getString(notesCursor.getColumnIndexOrThrow("note"));
            String noteDate = notesCursor.getString(notesCursor.getColumnIndexOrThrow("date"));

            // Update the dateText to show the most recent date
            if (notesCursor.isFirst()) {
                dateText.setText(noteDate);
            }

            // Add note to the UI
            addNoteView(noteText);
        }
        notesCursor.close();
    }

    private void addNote() {
        String newNote = newNoteEditText.getText().toString().trim();

        if (newNote.isEmpty()) {
            Toast.makeText(this, "Note cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Insert the new note into the database
            db.execSQL(
                    "INSERT INTO notes (note, date, user_id) VALUES (?, datetime('now'), ?)",
                    new Object[]{newNote, loggedInUserId}
            );

            // Refresh the notes view
            loadNotes();

            // Clear the input field
            newNoteEditText.setText("");

            Toast.makeText(this, "Note added successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("AddNote", "Error adding note: ", e);
            Toast.makeText(this, "Failed to add note. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNoteView(String noteText) {
        TextView noteView = new TextView(this);
        noteView.setText(noteText);
        noteView.setPadding(16, 16, 16, 16);
        noteView.setTextSize(16);
        noteView.setBackgroundResource(android.R.drawable.editbox_background);
        notesContainer.addView(noteView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
