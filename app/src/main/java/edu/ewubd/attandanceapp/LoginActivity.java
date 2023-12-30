package edu.ewubd.attandanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {
    Button signin, signup;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.btnSignIn);
        signup = findViewById(R.id.btnSignUp);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = email.getText().toString();
                String enteredPassword = password.getText().toString();

                if (validator(email) && validator(password)) {
                    if (checkCredentials(enteredEmail, enteredPassword)) {
                        Intent intent = new Intent(LoginActivity.this, ClassListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials. Please check the fields.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid input. Please check the fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean validator(EditText value) {
        String valueToText = value.getText().toString();

        if (valueToText.isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean checkCredentials(String userId, String password) {
        SharedPreferences localPref = getSharedPreferences("myPrefsLogin", MODE_PRIVATE);
        String storedUserId = localPref.getString("Email", "");
        String storedPassword = localPref.getString("Password", "");

        return userId.equals(storedUserId) && password.equals(storedPassword);
    }
}
