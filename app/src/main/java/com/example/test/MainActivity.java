package com.example.test;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db; // Declare the database object
    EditText userIdEditText, secretPasswordEditText;
    Button loginButton, createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database
        initializeDatabase();

        // Initialize EditText fields
        userIdEditText = findViewById(R.id.userId);
        secretPasswordEditText = findViewById(R.id.secretPassword);

        // Set up buttons
        loginButton = findViewById(R.id.login_button);
        createUserButton = findViewById(R.id.newUser);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Login btn clicked");
                login(); // Call the login method
            }
        });

        // Create user button click listener
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Creating secret user!", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Create User Button clicked");
                createNewUser();
            }
        });
    }

    private void initializeDatabase() {
        try {
            // Open or create the database
            db = openOrCreateDatabase("UserNotes.db", MODE_PRIVATE, null);

            // Create tables if they do not exist
            db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, password VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS notes(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "note TEXT, " +
                    "date TEXT, " +
                    "user_id INTEGER, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id));");


            Log.d("MainActivity", "Database initialized successfully");
        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing database: ", e);
        }
    }

    private void login() {
        String username = userIdEditText.getText().toString().trim();
        String password = secretPasswordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = null;
        try {
            // Ensure the database query is correct
            String query = "SELECT id, name FROM users WHERE name = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{username, password});

            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                Log.d("MainActivity", "User found: ID = " + userId + ", Name = " + name);
                Log.d("MainActivity", "Navigating to NotesActivity");

                // Navigate to NotesActivity
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                intent.putExtra("loggedInUserId", userId);
                intent.putExtra("loggedInUserName", name);
                startActivity(intent);

                // Ensure the current activity is finished
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error querying database: ", e);
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void createNewUser() {
        // Get input values
        String userName = userIdEditText.getText().toString().trim();
        String password = secretPasswordEditText.getText().toString().trim();

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
            secretPasswordEditText.setText("");

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
            db.close();
        }
    }
}
