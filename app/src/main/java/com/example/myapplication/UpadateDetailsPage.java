package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ServerValues;

import java.util.HashMap;
import java.util.Map;

public class UpadateDetailsPage extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;

    EditText fname_txt, lname_txt, email_txt, ph_num_txt, currentPass, newPass;

    Button updateBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadate_details_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fname_txt = findViewById(R.id.fname_txt);
        lname_txt = findViewById(R.id.lname_txt);
//        email_txt = findViewById(R.id.email_txt);
        ph_num_txt = findViewById(R.id.ph_num_txt);
        currentPass = findViewById(R.id.currentPass);
        newPass = findViewById(R.id.newPass);

        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);


        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }

//        testing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference r = reference.child(user.getUid());

        Log.d("myTag", user.getUid());

        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //this gets the user details from database
                String fname = snapshot.child("fname").getValue().toString();
                String lname = snapshot.child("lname").getValue().toString();
                String phnumber = snapshot.child("phnumber").getValue().toString();
                String total_donations = snapshot.child("tdonations").getValue().toString();
                String total_subscriptions = snapshot.child("tsubscriptions").getValue().toString();


                fname_txt.setText(fname);
                lname_txt.setText(lname);
                ph_num_txt.setText(phnumber);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("myTag", "Failed to read value.", error.toException());
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this checks if the anyone the details are empty
                if (fname_txt.getText().toString().isEmpty()) {
                    Toast.makeText(UpadateDetailsPage.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lname_txt.getText().toString().isEmpty()) {
                    Toast.makeText(UpadateDetailsPage.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }else if (ph_num_txt.getText().toString().isEmpty()) {
                    Toast.makeText(UpadateDetailsPage.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if the password fields are empty then only the name and phone number is updated
                if (currentPass.getText().toString().isEmpty() && newPass.getText().toString().isEmpty()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("fname", fname_txt.getText().toString());
                    map.put("lname", lname_txt.getText().toString());
                    map.put("phnumber", ph_num_txt.getText().toString());

                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Users");
                    DatabaseReference rr = rf.child(user.getUid());
                    rr.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UpadateDetailsPage.this, "Details Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentPass.getText().toString() != "" && newPass.getText().toString().isEmpty()) {
                    //this check is both passwords are entered
                    Toast.makeText(UpadateDetailsPage.this, "Please Enter a new pass word to update", Toast.LENGTH_SHORT).show();
                } else if (currentPass.getText().toString().isEmpty() && newPass.getText().toString() != "") {
                    Toast.makeText(UpadateDetailsPage.this, "Please Enter the current password to update", Toast.LENGTH_SHORT).show();
                } else {
                    //this updates the user's password
                    AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPass.getText().toString());
                    user.reauthenticate(authCredential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    user.updatePassword(newPass.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("fname", fname_txt.getText().toString());
                                                    map.put("lname", lname_txt.getText().toString());
                                                    map.put("phnumber", ph_num_txt.getText().toString());

                                                    //this updates the name and phone number
                                                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Users");
                                                    DatabaseReference rr = reference.child(user.getUid());
                                                    rr.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(UpadateDetailsPage.this, "Details Updated", Toast.LENGTH_SHORT).show();
                                                            currentPass.setText("");
                                                            newPass.setText("");
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpadateDetailsPage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpadateDetailsPage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }


            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }

}