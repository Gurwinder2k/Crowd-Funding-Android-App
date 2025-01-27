package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DonationHistoryTab extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private List<Donation> donationList;
    private DonationHistoryAdapter donationHistoryAdapter;

    TextView nodonations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        View view = inflater.inflate(R.layout.fragment_donation_history_tab, container, false);

        nodonations = view.findViewById(R.id.nodonations);

        recyclerView = view.findViewById(R.id.donationhistoryRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        donationList = new ArrayList<>();

        nodonations.setVisibility(View.GONE);
        loadDonations();

        return view;

    }

    private void loadDonations() {
        //gets reference to the donations
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clears the list containing donation objects
                donationList.clear();
                //checks if there are donations
                if (snapshot.getChildrenCount() > 0) {
                    //hides the text that says "no donations to show"
                    nodonations.setVisibility(View.GONE);
                    for (DataSnapshot allposts : snapshot.getChildren()) {
                        for (DataSnapshot alldonations : allposts.getChildren()) {
                            //checks if the donation is made by the current user
                            if(alldonations.child("user").getValue().toString().equals(user.getUid())){
                                //gets all the details
                                int amount = Integer.parseInt(alldonations.child("amount").getValue().toString());
                                String bankCard = alldonations.child("bankCard").getValue().toString();
                                String key = alldonations.child("key").getValue().toString();
                                String postid = alldonations.child("post").getValue().toString();
                                String userid = alldonations.child("user").getValue().toString();
                                String postUser = alldonations.child("postUser").getValue().toString();

                                //creates donation instance
                                Donation donation = new Donation(userid, postid, amount, bankCard, key, postUser);

                                //adds the donation to the list
                                donationList.add(donation);


                            }
                        }
                    }
                    //Adapter is created and the list of donations is passed
                    donationHistoryAdapter = new DonationHistoryAdapter(getActivity(), donationList);
                    recyclerView.setAdapter(donationHistoryAdapter);
                }else{
                    nodonations.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "An Error Occured " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}