package com.example.studentside;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    EditText email;
    EditText password;
    Button signup;
    Button login;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_signUp);
        progressBar = findViewById(R.id.progressBar);
        email = findViewById(R.id.editText_signUpEmail);
        password = findViewById(R.id.editText_signUpPassword);
        signup = findViewById(R.id.button_signUp_confirm);

        toolbar.setTitle("Registration");

        firebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isGmailAddress(email.getText().toString())) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        firebaseAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(MainActivity.this, "Registered successfully. Please check your email for verification",
                                                                    Toast.LENGTH_LONG).show();
                                                            email.setText("");
                                                            password.setText("");
                                                        } else {
                                                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(MainActivity.this, "Please enter your Swinburne email.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static boolean isGmailAddress(String emailAddress) {
        String expression = "^[\\w.+\\-]+@students\\.swinburne\\.edu\\.my$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }
}
