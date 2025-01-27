package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RewardsFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    TextView text_drink_points, badge_points;
    List<ImageView> drinksList;
    List<ImageView> badgeList;


    Button claim_drink, claim_badge;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9,imageView10,imageView11,imageView12,imageView13
            ,imageView16,imageView17,imageView18,imageView19,imageView20,imageView21,imageView22,imageView23,imageView24,imageView25,imageView26,imageView27,imageView28
            ,imageView31,imageView32,imageView33,imageView34,imageView35,imageView36,imageView37,imageView38,imageView39,imageView40,imageView41,imageView42,imageView43
            ,imageView44,imageView45,imageView46,imageView47,imageView48,imageView49,imageView50,imageView51,imageView52,imageView53,imageView54,imageView55,imageView56
            ,imageView57,imageView58,imageView59,imageView60,imageView61,imageView62,imageView63,imageView64,imageView65,imageView66,imageView67,imageView68,imageView69
            ,imageView70,imageView71,imageView72,imageView73,imageView74,imageView75,imageView76,imageView77,imageView78,imageView79,imageView80,imageView81,imageView82
            ,imageView83,imageView84,imageView85,imageView86,imageView87,imageView88,imageView89,imageView90,imageView91,imageView92,imageView93;

    private Integer drinkPointsNum;
    private Integer badgePoints;
    public RewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("testing1234", "here rewards");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        text_drink_points = view.findViewById(R.id.text_drink_points);
        badge_points = view.findViewById(R.id.badge_points);

        claim_drink = view.findViewById(R.id.claim_drink);
        claim_badge = view.findViewById(R.id.claim_badge);

        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView4 = view.findViewById(R.id.imageView4);
        imageView5 = view.findViewById(R.id.imageView5);
        imageView6 = view.findViewById(R.id.imageView6);
        imageView7 = view.findViewById(R.id.imageView7);
        imageView8 = view.findViewById(R.id.imageView8);
        imageView9 = view.findViewById(R.id.imageView9);
        imageView10 = view.findViewById(R.id.imageView10);
        imageView11 = view.findViewById(R.id.imageView11);
        imageView12 = view.findViewById(R.id.imageView12);
        imageView13 = view.findViewById(R.id.imageView13);
        imageView16 = view.findViewById(R.id.imageView16);
        imageView17 = view.findViewById(R.id.imageView17);
        imageView18 = view.findViewById(R.id.imageView18);
        imageView19 = view.findViewById(R.id.imageView19);
        imageView20 = view.findViewById(R.id.imageView20);
        imageView21 = view.findViewById(R.id.imageView21);
        imageView22 = view.findViewById(R.id.imageView22);
        imageView23 = view.findViewById(R.id.imageView23);
        imageView24 = view.findViewById(R.id.imageView24);
        imageView25 = view.findViewById(R.id.imageView25);
        imageView26 = view.findViewById(R.id.imageView26);
        imageView27 = view.findViewById(R.id.imageView27);
        imageView28 = view.findViewById(R.id.imageView28);
        imageView31 = view.findViewById(R.id.imageView31);
        imageView32 = view.findViewById(R.id.imageView32);
        imageView33 = view.findViewById(R.id.imageView33);
        imageView34 = view.findViewById(R.id.imageView34);
        imageView35 = view.findViewById(R.id.imageView35);
        imageView36 = view.findViewById(R.id.imageView36);
        imageView37 = view.findViewById(R.id.imageView37);
        imageView38 = view.findViewById(R.id.imageView38);
        imageView39 = view.findViewById(R.id.imageView39);
        imageView40 = view.findViewById(R.id.imageView40);
        imageView41 = view.findViewById(R.id.imageView41);
        imageView42 = view.findViewById(R.id.imageView42);
        imageView43 = view.findViewById(R.id.imageView43);
        imageView44 = view.findViewById(R.id.imageView44);
        imageView45 = view.findViewById(R.id.imageView45);
        imageView46 = view.findViewById(R.id.imageView46);
        imageView47 = view.findViewById(R.id.imageView47);
        imageView48 = view.findViewById(R.id.imageView48);
        imageView49 = view.findViewById(R.id.imageView49);
        imageView50 = view.findViewById(R.id.imageView50);
        imageView51 = view.findViewById(R.id.imageView51);
        imageView52 = view.findViewById(R.id.imageView52);
        imageView53 = view.findViewById(R.id.imageView53);
        imageView54 = view.findViewById(R.id.imageView54);
        imageView55 = view.findViewById(R.id.imageView55);
        imageView56 = view.findViewById(R.id.imageView56);
        imageView57 = view.findViewById(R.id.imageView57);
        imageView58 = view.findViewById(R.id.imageView58);
        imageView59 = view.findViewById(R.id.imageView59);
        imageView60 = view.findViewById(R.id.imageView60);
        imageView61 = view.findViewById(R.id.imageView61);
        imageView62 = view.findViewById(R.id.imageView62);
        imageView63 = view.findViewById(R.id.imageView63);
        imageView64 = view.findViewById(R.id.imageView64);
        imageView65 = view.findViewById(R.id.imageView65);
        imageView66 = view.findViewById(R.id.imageView66);
        imageView67 = view.findViewById(R.id.imageView67);
        imageView68 = view.findViewById(R.id.imageView68);
        imageView69 = view.findViewById(R.id.imageView69);
        imageView70 = view.findViewById(R.id.imageView70);
        imageView71 = view.findViewById(R.id.imageView71);
        imageView72 = view.findViewById(R.id.imageView72);
        imageView73 = view.findViewById(R.id.imageView73);
        imageView74 = view.findViewById(R.id.imageView74);
        imageView75 = view.findViewById(R.id.imageView75);
        imageView76 = view.findViewById(R.id.imageView76);
        imageView77 = view.findViewById(R.id.imageView77);
        imageView78 = view.findViewById(R.id.imageView78);
        imageView79 = view.findViewById(R.id.imageView79);
        imageView80 = view.findViewById(R.id.imageView80);
        imageView81 = view.findViewById(R.id.imageView81);
        imageView82 = view.findViewById(R.id.imageView82);
        imageView83 = view.findViewById(R.id.imageView83);
        imageView84 = view.findViewById(R.id.imageView84);
        imageView85 = view.findViewById(R.id.imageView85);
        imageView86 = view.findViewById(R.id.imageView86);
        imageView87 = view.findViewById(R.id.imageView87);
        imageView88 = view.findViewById(R.id.imageView88);
        imageView89 = view.findViewById(R.id.imageView89);
        imageView90 = view.findViewById(R.id.imageView90);
        imageView91 = view.findViewById(R.id.imageView91);
        imageView92 = view.findViewById(R.id.imageView92);
        imageView93 = view.findViewById(R.id.imageView93);

        //lists are created for holding the image views
        drinksList =  new ArrayList<>(Arrays.asList(imageView1,imageView2,imageView3,
                imageView4,imageView5,imageView6,imageView7,imageView8,
                imageView9,imageView10,imageView11,imageView12,imageView13,
                imageView16,imageView17,imageView18,imageView19,imageView20,
                imageView21,imageView22,imageView23,imageView24,imageView25,
                imageView26,imageView27,imageView28,imageView31,imageView32,
                imageView33,imageView34,imageView35,imageView36,imageView37,
                imageView38,imageView39,imageView40,imageView41,imageView42,imageView43));

        badgeList =  new ArrayList<>(Arrays.asList(imageView44,imageView45,imageView46,imageView47,imageView48,
                imageView49,imageView50,imageView51,imageView52,imageView53,imageView54,imageView55,
                imageView56,imageView57,imageView58,imageView59,imageView60,imageView61,imageView62,
                imageView63,imageView64,imageView65,imageView66,imageView67,imageView68,imageView69,
                imageView70,imageView71,imageView72,imageView73,imageView74,imageView75,imageView76,
                imageView77,imageView78,imageView79,imageView80,imageView81,imageView82,imageView83,
                imageView84,imageView85,imageView86,imageView87,imageView88,imageView89,imageView90,
                imageView91,imageView92,imageView93));


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //this gets the points from the user's account
                drinkPointsNum = Integer.valueOf(snapshot.child("drinkPoints").getValue().toString());
                badgePoints = Integer.valueOf(snapshot.child("badgePoints").getValue().toString());

                //this sets all the image views to show off
                for (int i=0; i< drinksList.size(); i++){
                    drinksList.get(i).setImageResource(R.drawable.baseline_radio_button_unchecked_24);
                }
                for (int i=0; i< badgeList.size(); i++){
                    badgeList.get(i).setImageResource(R.drawable.baseline_radio_button_unchecked_24);
                }
                //this turns on image views for the number of drink points
                for (int i=0; i< drinkPointsNum; i++){
                    drinksList.get(i).setImageResource(R.drawable.baseline_radio_button_checked_24);
                }
                //this turns on image views for the number of badge points
                for (int i=0; i< badgePoints; i++){
                    badgeList.get(i).setImageResource(R.drawable.baseline_radio_button_checked_24);
                }

                text_drink_points.setText("Points: " + drinkPointsNum + "/39");
                badge_points.setText("Points: " + badgePoints + "/50");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        claim_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checks if enough points are present
                if (drinkPointsNum < 39){
                    Toast.makeText(view.getContext(), "You don't have enough points", Toast.LENGTH_LONG).show();
                    return;
                }
                //opens the claim page
                Intent intent = new Intent(getActivity(), RewardClaimPage.class);
                startActivity(intent);
            }
        });

        claim_badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBadgePoints();
            }
        });

    }

    private void resetBadgePoints() {
        if (badgePoints < 50){
            Toast.makeText(getContext(), "You don't have enough points", Toast.LENGTH_LONG).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference r = reference.child(user.getUid());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Map<String, Object> map = new HashMap<>();
                map.put("badgePoints", 0);

                r.updateChildren(map);
                return;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("myTag", "Failed to read value.", error.toException());
            }
        });
    }

}