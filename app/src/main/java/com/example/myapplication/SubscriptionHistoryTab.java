package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SubscriptionHistoryTab extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private List<Subscription> subscriptionList;
    private SubscriptionHistoryAdapter subscriptionHistoryAdapter;

    private TextView nosubscriptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        View view = inflater.inflate(R.layout.fragment_subscription_history_tab, container, false);

        nosubscriptions = view.findViewById(R.id.nosubscriptions);
        recyclerView = view.findViewById(R.id.subscriptionhistoryRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        subscriptionList = new ArrayList<>();

        nosubscriptions.setVisibility(View.GONE);
        loadSubscriptions();

        return view;

    }

    //this loads all the subscription for the current user
    //data retrieved is used to create subscription object
    //then object is added to the list
    //adapter is created with the list
    private void loadSubscriptions() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Subscriptions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscriptionList.clear();
                if (snapshot.getChildrenCount() > 0) {
                    nosubscriptions.setVisibility(View.GONE);
                    for (DataSnapshot allposts : snapshot.getChildren()) {
                        for (DataSnapshot alldonations : allposts.getChildren()) {
                            if(alldonations.child("user").getValue().toString().equals(user.getUid())){
                                boolean active = Boolean.parseBoolean(alldonations.child("active").getValue().toString());
                                if (active){
                                    int amount = Integer.parseInt(alldonations.child("amount").getValue().toString());
                                    String bankCard = alldonations.child("bankCard").getValue().toString();
                                    String key = alldonations.child("key").getValue().toString();
                                    String postid = alldonations.child("post").getValue().toString();
                                    String userid = alldonations.child("user").getValue().toString();
                                    String postUser = alldonations.child("postUser").getValue().toString();

                                    Subscription subscription = new Subscription(userid, postid, amount, bankCard, key, postUser);

                                    subscriptionList.add(subscription);

                                }
                            }
                        }
                    }
                    subscriptionHistoryAdapter = new SubscriptionHistoryAdapter(getActivity(), subscriptionList);
                    recyclerView.setAdapter(subscriptionHistoryAdapter);

                }else{
                    nosubscriptions.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}