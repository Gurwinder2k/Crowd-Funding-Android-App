package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SubscriptionTierPage extends AppCompatActivity {

    ImageButton tier1btn, tier2btn, tier3btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_tier_page);

        Post post = (Post) getIntent().getSerializableExtra("post");

        tier1btn = findViewById(R.id.tier1btn);
        tier2btn = findViewById(R.id.tier2btn);
        tier3btn = findViewById(R.id.tier3btn);


        //the buttions are associated to different tiers
        //this one is tier one
        //intent is created to open the payment page
        //post object, amount, type-subscription and tier 1 is added to the intent
        tier1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = 5;
                Intent intent = new Intent(SubscriptionTierPage.this, PaymentPage.class);
                intent.putExtra("post", post);
                intent.putExtra("amount", amount);
                intent.putExtra("type", "subscription");
                intent.putExtra("tier", "1");
                startActivity(intent);
                finish();
            }
        });

        tier2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = 10;
                Intent intent = new Intent(SubscriptionTierPage.this, PaymentPage.class);
                intent.putExtra("post", post);
                intent.putExtra("amount", amount);
                intent.putExtra("type", "subscription");
                intent.putExtra("tier", "2");
                startActivity(intent);
                finish();
            }
        });

        tier3btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = 15;
                Intent intent = new Intent(SubscriptionTierPage.this, PaymentPage.class);
                intent.putExtra("post", post);
                intent.putExtra("amount", amount);
                intent.putExtra("type", "subscription");
                intent.putExtra("tier", "3");
                startActivity(intent);
                finish();
            }
        });


    }
}