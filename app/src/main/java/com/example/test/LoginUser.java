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

public class LoginUser extends AppCompatActivity {

    SQLiteDatabase db;
    EditText userIdEditText, secretPasswordEditText;
    Button newUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize EditText fields
        userIdEditText = findViewById(R.id.userId);
        secretPasswordEditText = findViewById(R.id.secretPassword);

        // Initialize buttons
        newUserButton = findViewById(R.id.newUser);

        // Open or create the database
        db = openOrCreateDatabase("UserNotes.db", MODE_PRIVATE, null);

        // Create tables
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS notes(id INTEGER PRIMARY KEY AUTOINCREMENT, note TEXT, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id));");

        // Set up the login button listener
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginUser", "Go secret button clicked");
                login(); // Call the login method
            }
        });
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

                Log.d("LoginUser", "User found: ID = " + userId + ", Name = " + name);
                Log.d("LoginUser", "Navigating to NotesActivity");
                // Navigate to NotesActivity
                Intent intent = new Intent(LoginUser.this, NotesActivity.class);
                intent.putExtra("loggedInUserId", userId);
                intent.putExtra("loggedInUserName", name);
                startActivity(intent);

                // Ensure the current activity is finished
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("LoginUser", "Error querying database: ", e);
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
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
