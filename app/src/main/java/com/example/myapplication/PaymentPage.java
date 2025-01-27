package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ServerValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private List<BankCard> bankCardList;
    private BankCardSelectionAdapter bankCardSelectionAdapter;

    private Button addCardBtn, payBtn;
    private TextView noCardsTxt, paymentAmount;

    public int drinkPointsGiven, badgePointsGiven;
    public long currentDrinkPoints;
    public long currentBadgePoints;

    private int bankCardNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        //gets the post object from intent
        Post post = (Post) getIntent().getSerializableExtra("post");
        //gets the amount from intent
        int amount = getIntent().getIntExtra("amount", 0);
        //gets the type from intent
        String type = getIntent().getStringExtra("type");

        //checks if the type is one time donation
        if (type.equals("onetime")) {
            //reward points are given for just one time donation
            drinkPointsGiven = 3;
            badgePointsGiven = 2;
        } else if (type.equals("subscription")) {
            //if the type is subscription, then the tier is extracted from intent
            String tier = getIntent().getStringExtra("tier");
//            reward points are given based on the tier selected
            if (tier.equals("1")) {
                drinkPointsGiven = 3;
                badgePointsGiven = 2;
            } else if (tier.equals("2")) {
                drinkPointsGiven = 5;
                badgePointsGiven = 3;
            } else if (tier.equals("3")) {
                drinkPointsGiven = 10;
                badgePointsGiven = 4;
            } else {
                Toast.makeText(PaymentPage.this, "A problem has occurred", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PaymentPage.this, "A problem has occurred", Toast.LENGTH_LONG).show();
        }

        addCardBtn = findViewById(R.id.addCardBtn);
        payBtn = findViewById(R.id.payBtn);
        paymentAmount = findViewById(R.id.paymentAmount);

        noCardsTxt = findViewById(R.id.noCardsTxt);

        recyclerView = findViewById(R.id.bankcardRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        noCardsTxt.setVisibility(View.GONE);

        paymentAmount.setText("£" + amount);

        bankCardList = new ArrayList<>();

        loadBankCard();


        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentPage.this, AddBankCardPage.class);
                startActivity(intent);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checks if a bank card is selected
                if (bankCardNum>0) {
                    if(type.equals("onetime")){
                        //if the donation type is one time then the donation is given
                        String key = FirebaseDatabase.getInstance().getReference()
                                .child("Donations")
                                .child(post.getPostId())
                                .push().getKey();

                        //gets the bank card id
                        String cardKey = bankCardSelectionAdapter.getSelected().getKey();
                        //new donation object is created
                        Donation donation = new Donation(user.getUid(), post.getPostId(), amount, cardKey, key, post.getUserId());
                        Log.d("donation", String.valueOf(donation));
                        //donation is added to real time database
                        FirebaseDatabase.getInstance().getReference()
                                .child("Donations")
                                .child(post.getPostId())
                                .child(key)
                                .setValue(donation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //the amount funded for the post is incremented by the donation amount
                                            DatabaseReference r = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(post.getUserId()).child(post.getPostId()).child("amountFunded");
                                            r.setValue(ServerValue.increment(amount));

                                            //this calls method to update points for donation
                                            updatePoints("tdonations");


                                            Toast.makeText(PaymentPage.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                                            finish();


                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(post.getUserId()).child(post.getPostId());
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    int amountFunded = Integer.parseInt(snapshot.child("amountFunded").getValue().toString());
                                                    int fundingamountText = Integer.parseInt(snapshot.child("fundingamountText").getValue().toString());
                                                    //this checks if the post has reached the required funding amount.
                                                    if (amountFunded >= fundingamountText) {
                                                        ref.child("goalReached").setValue(true);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {
                                            Toast.makeText(PaymentPage.this, "Couldn't send the payment1", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        //if the donation is a subscription then a random key is generated
                        String key = FirebaseDatabase.getInstance().getReference()
                                .child("Subscriptions")
                                .child(post.getPostId())
                                .push().getKey();

                        Log.d("subscription", key);

                        //bank card id is retrieved
                        String cardKey = bankCardSelectionAdapter.getSelected().getKey();

                        //this creates a subscription object and adds it to firebase database
                        Subscription subscription = new Subscription(user.getUid(), post.getPostId(), amount, cardKey, key, post.getUserId());
                        FirebaseDatabase.getInstance().getReference()
                                .child("Subscriptions")
                                .child(post.getPostId())
                                .child(key)
                                .setValue(subscription).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //this increments the amount funded in the database
                                            DatabaseReference r = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(post.getUserId()).child(post.getPostId()).child("amountFunded");
                                            r.setValue(ServerValue.increment(amount));

                                            //this updates the points for subscriptions
                                            updatePoints("tsubscriptions");


                                            Toast.makeText(PaymentPage.this, "Subscription Setup", Toast.LENGTH_SHORT).show();
                                            finish();

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(post.getUserId()).child(post.getPostId());
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    int amountFunded = Integer.parseInt(snapshot.child("amountFunded").getValue().toString());
                                                    int fundingamountText = Integer.parseInt(snapshot.child("fundingamountText").getValue().toString());

                                                    //this checks if the required funding has been reached
                                                    if (amountFunded >= fundingamountText) {
                                                        ref.child("goalReached").setValue(true);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {
                                            Toast.makeText(PaymentPage.this, "Couldn't setup subscription 1", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }


                } else {
                    Toast.makeText(PaymentPage.this, "Add Bank Card", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

    }


    public void updatePoints(String t){
        //this updated the points and increments the number of donations or subscriptions set up
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference r = reference.child(user.getUid());
        r.child(t).setValue(ServerValue.increment(1));
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //this gets the current reward points from the database
                currentDrinkPoints = (Long) snapshot.child("drinkPoints").getValue();
                currentBadgePoints = (Long) snapshot.child("badgePoints").getValue();
                Log.d("accesstest", "inside"+String.valueOf(currentDrinkPoints));

                // this checks if the maximum number of rewards will be reached and sets the max number of points available
                if((drinkPointsGiven+currentDrinkPoints) >= 39){
                    drinkPointsGiven = 39;
                }else{
                    drinkPointsGiven = (int) (drinkPointsGiven+currentDrinkPoints);
                }
                if((badgePointsGiven+currentBadgePoints) >= 50){
                    badgePointsGiven = 50;
                }else{
                    badgePointsGiven = (int) (badgePointsGiven+currentBadgePoints);
                }

                //creates a hash map with the new values
                Map<String, Object> map = new HashMap<>();
                map.put("drinkPoints", drinkPointsGiven);
                map.put("badgePoints", badgePointsGiven);
                //this updates the points
                r.updateChildren(map);
                return;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("myTag", "Failed to read value.", error.toException());
            }
        });

        Log.d("accesstest", "outside"+String.valueOf(currentDrinkPoints));

        Log.d("accesstest", String.valueOf(drinkPointsGiven));



    }


    private void loadBankCard() {
        //this gets all the bank cards associated with to the current user from the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BankCards");
        DatabaseReference r = reference.child(user.getUid());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bankCardList.clear();
                if (snapshot.getChildrenCount() > 0) {
                    noCardsTxt.setVisibility(View.GONE);
                    for (DataSnapshot s : snapshot.getChildren()) {
                        if (s.getChildrenCount() == 6) {
                            //this gets the data for each bank card.
                            String cardNum = s.child("cardNum").getValue().toString();
                            boolean isChecked = (boolean) s.child("checked").getValue();
                            String cvvCode = s.child("cvvCode").getValue().toString();
                            String expDate = s.child("expDate").getValue().toString();
                            String nameOnCard = s.child("nameOnCard").getValue().toString();
                            String key = s.child("key").getValue().toString();

                            //bank card object is created
                            BankCard bankCard = new BankCard(isChecked, cardNum, nameOnCard, expDate, cvvCode, key);
                            //the bank card object is then added to the list
                            bankCardList.add(bankCard);
                            bankCardNum+=1;
                        }

                    }
                    //new adapter is created and the list of bank cards is passed
                    bankCardSelectionAdapter = new BankCardSelectionAdapter(PaymentPage.this, bankCardList);
                    recyclerView.setAdapter(bankCardSelectionAdapter);
                } else {
                    noCardsTxt.setVisibility(View.VISIBLE);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentPage.this, "An Error Occured " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}