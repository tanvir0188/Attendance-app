package edu.ewubd.attandanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    Button signin, signup;
    EditText email, password, rePassword, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        name = findViewById(R.id.etName);
        signin = findViewById(R.id.btnSignIn);
        signup = findViewById(R.id.btnSignUp);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, ClassListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nameValid = validator(name);
                boolean emailValid = emailValidator(email);
                boolean passwordValid = validator(password);
                boolean rePasswordValid = password.getText().toString().equals(rePassword.getText().toString());


                if(nameValid && emailValid && passwordValid && rePasswordValid)
                {
                    SharedPreferences localPref = SignUpActivity.this.getSharedPreferences("myPrefsLogin", MODE_PRIVATE);
                    SharedPreferences.Editor edit = localPref.edit();
                    edit.putString("Name", name.getText().toString());
                    edit.putString("Email", email.getText().toString());
                    edit.putString("Password", password.getText().toString());

                    edit.apply();

                    Intent intent = new Intent(SignUpActivity.this, ClassListActivity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    if (!nameValid) {
                        Toast.makeText(SignUpActivity.this, "Invalid name. Please enter a valid name.", Toast.LENGTH_SHORT).show();
                    }
                    if (!emailValid) {
                        Toast.makeText(SignUpActivity.this, "Invalid email. Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    }
                    if (!passwordValid) {
                        Toast.makeText(SignUpActivity.this, "Invalid password. Please enter a valid password.", Toast.LENGTH_SHORT).show();
                    }
                    if (!rePasswordValid) {
                        Toast.makeText(SignUpActivity.this, "Passwords do not match. Please re-enter your password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public boolean emailValidator(EditText email) {
        String emailText = email.getText().toString().trim();

        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return false;
        }

        return true;
    }

    public boolean validator(EditText value) {
        String valueToText = value.getText().toString();

        if (valueToText.isEmpty() || (valueToText.length() < 4 || valueToText.length() > 20)) {
            return false;
        }

        return true;
    }
}
