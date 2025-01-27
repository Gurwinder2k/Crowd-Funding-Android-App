package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DonationAmountPage extends AppCompatActivity {

    TextView subscriptionLink;
    Button continueBtn;

    EditText fundingamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_amount_page);

        //this gets the post object from the intent
        Post post = (Post) getIntent().getSerializableExtra("post");

        fundingamount = findViewById(R.id.fundingamount);
        subscriptionLink = findViewById(R.id.subscriptionLink);
        continueBtn = findViewById(R.id.continueBtn);

        subscriptionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //opens the subscription tier page
                Intent intent = new Intent(DonationAmountPage.this, SubscriptionTierPage.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checks if input is empty
                if (fundingamount.getText().toString().isEmpty()){
                    Toast.makeText(DonationAmountPage.this, "Please enter the donation amount", Toast.LENGTH_SHORT).show();
                }else{
                    //this opens the payment page and passes the post object, the amount and the type of donation in the intent
                    int amount = Integer.parseInt(fundingamount.getText().toString());
                    Intent intent = new Intent(DonationAmountPage.this, PaymentPage.class);
                    intent.putExtra("post", post);
                    intent.putExtra("amount", amount);
                    intent.putExtra("type", "onetime");
                    startActivity(intent);
                    finish();
                }


            }
        });

    }
}