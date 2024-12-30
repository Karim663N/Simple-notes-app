package com.example.test;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateUser extends AppCompatActivity {

    private SQLiteDatabase db; // Database instance
    private EditText userIdEditText;
    private EditText passwordEditText;
    private Button newUserButton; // Button reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this matches your layout file

        // Initialize database using DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase(); // Get writable database

        // Get references to input fields
        userIdEditText = findViewById(R.id.userId);
        passwordEditText = findViewById(R.id.secretPassword);

        // Get reference to the button
        newUserButton = findViewById(R.id.newUser);

        // Set up the "New secret" button listener
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Toast message when button is clicked
                Toast.makeText(CreateUser.this, "New Secret Button Clicked!", Toast.LENGTH_SHORT).show();

                // Call method to save new user data
                saveNewUser();
            }
        });
    }

    private void saveNewUser() {
        // Get input values
        String userName = userIdEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the user into the database
        try {
            db.execSQL("INSERT INTO users(name, password) VALUES(?, ?)", new Object[]{userName, password});
            Log.d("Database", "User added: " + userName);

            // Clear input fields
            userIdEditText.setText("");
            passwordEditText.setText("");

            // Show success messages
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Secret Notes Created!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("Database", "Error inserting user", e);
            Toast.makeText(this, "Error adding user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close(); // Close the database when the activity is destroyed
        }
    }
}
