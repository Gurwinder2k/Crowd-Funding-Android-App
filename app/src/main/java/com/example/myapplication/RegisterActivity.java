package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextFname, editTextLname, editTextPhoneNumber;
    Button buttonReg;
    FirebaseAuth mAuth;

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), BottomNavigationMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFname  = findViewById(R.id.firstname);
        editTextLname = findViewById(R.id.lastname);
        editTextPhoneNumber = findViewById(R.id.phonenumber);
        buttonReg = findViewById(R.id.btn_register);

        buttonReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //this gets the details entered by the user
                String email, password, fname, lname, phnumber;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                fname = String.valueOf(editTextFname.getText());
                lname = String.valueOf(editTextLname.getText());
                phnumber = String.valueOf(editTextPhoneNumber.getText());

                //this checks if the details are empty
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(fname)){
                    Toast.makeText(RegisterActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lname)){
                    Toast.makeText(RegisterActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phnumber)){
                    Toast.makeText(RegisterActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                //this creates the account with username and password
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user object is created
                            User user = new User(fname, lname, email, phnumber, 0, 0, 0, 0);
                            //the user details are then added to the real time database
                            FirebaseDatabase.getInstance("https://crowdfunding-application-f21b6-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                savePreference();
                                                //opens the login page
                                                Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });



                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void savePreference(){
        //This saves the default settings in the shared preferences file
        SharedPreferences sharedPref = getSharedPreferences("hopeFundingPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor predEditor = sharedPref.edit();

        predEditor.putString("textSize", "normal");
        predEditor.putString("contrast", "default");
        predEditor.commit();
    }
}