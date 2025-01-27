package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBankCardPage extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    EditText nameCard, cardNumber, expDate, cvvCode;
    Button addBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        nameCard = findViewById(R.id.nameCard);
        cardNumber = findViewById(R.id.cardNumber);
        expDate = findViewById(R.id.expDate);
        cvvCode = findViewById(R.id.cvvCode);
        addBtn = findViewById(R.id.addBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets the data entered by the user
                String nameCardTxt = nameCard.getText().toString();
                String carNumberTxt = cardNumber.getText().toString();
                String expDateTxt = expDate.getText().toString();
                String cvvCodeTxt = cvvCode.getText().toString();


                //These are the checks done to ensure that all data is entered by the user
                if (nameCardTxt.isEmpty()) {
                    Toast.makeText(AddBankCardPage.this, "Please enter name on the card", Toast.LENGTH_SHORT).show();
                    return;
                } else if (carNumberTxt.isEmpty()) {
                    Toast.makeText(AddBankCardPage.this, "Please enter card number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (expDateTxt.isEmpty()) {
                    Toast.makeText(AddBankCardPage.this, "Please enter exp date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (cvvCodeTxt.isEmpty()) {
                    Toast.makeText(AddBankCardPage.this, "Please enter CVV number", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //gets a unique key for storing the data
                    String key = FirebaseDatabase.getInstance().getReference()
                            .child("BankCards")
                            .child(user.getUid())
                            .push().getKey();

                    //creates a BankCard object instance
                    BankCard bankCard = new BankCard(false, carNumberTxt, nameCardTxt, expDateTxt, cvvCodeTxt, key);

                    //This adds the bank card to the real time database
                    FirebaseDatabase.getInstance().getReference()
                            .child("BankCards")
                            .child(user.getUid())
                            .child(key)
                            .setValue(bankCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddBankCardPage.this, "Bank Card Added", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AddBankCardPage.this, "Can't add bank card right now", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            }
        });

    }
}