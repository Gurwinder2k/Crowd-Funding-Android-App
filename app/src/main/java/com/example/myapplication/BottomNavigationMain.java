package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityBottomNavigationMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BottomNavigationMain extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Button logout_btn, editBtn;

    ImageView close_menu;

    TextView name_display, email_text, phnumber_text, total_donations_text, total_subscriptions_text;

    private DrawerLayout drawer;
    ActivityBottomNavigationMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavigationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        name_display = findViewById(R.id.username_text);
        email_text = findViewById(R.id.email_text);
        phnumber_text = findViewById(R.id.phnumber_text);
        total_donations_text = findViewById(R.id.total_donations_text);
        total_subscriptions_text = findViewById(R.id.total_subscriptions_text);

        logout_btn = findViewById(R.id.logout_btn);
        editBtn = findViewById(R.id.editBtn);
        close_menu = findViewById(R.id.close_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            // The default fragment is the search page
            replaceFragment(new SearchPage());
            //this is a click listener for the bottom navigation, changes the fragment depending on the button clicked
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.search_btn:
                        replaceFragment(new SearchPage());
                        break;
                    case R.id.given_donations_btn:
                        replaceFragment(new DonationHistoryFragment());
                        break;
                    case R.id.received_donation_btn:
                        replaceFragment(new PostsCreatedFragment());
                        break;
                    case R.id.rewards_btn:
                        replaceFragment(new RewardsFragment());
                        break;
                }
                return true;
            });

            //Gets a reference to the user object in firebase
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            DatabaseReference r = reference.child(user.getUid());

            Log.d("myTag", user.getUid());

            r.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //gets the user details from firebase
                    String fname = snapshot.child("fname").getValue().toString();
                    String lname = snapshot.child("lname").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String phnumber = snapshot.child("phnumber").getValue().toString();
                    String total_donations = snapshot.child("tdonations").getValue().toString();
                    String total_subscriptions = snapshot.child("tsubscriptions").getValue().toString();

                    Log.d("myTag", fname+" "+lname);
                    //User details are displayed on the text views
                    name_display.setText(fname+" "+lname);
                    email_text.setText(email);
                    phnumber_text.setText(phnumber);
                    total_donations_text.setText("Total Donations: " + total_donations);
                    total_subscriptions_text.setText("Active Subscriptions: " + total_subscriptions);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("myTag", "Failed to read value.", error.toException());
                }
            });


//######################################################

            drawer = findViewById(R.id.drawer_layout);

            //This is the hamburger menu click listener, for closing and opening the menu
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

            drawer.addDrawerListener(toggle);
            toggle.syncState();

        }
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myTag", "logout");
                //If logout is clicked, the user is signed out, and the login screen is shown
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //When the close button on the hamburger menu is clicked, the drawer is closed
        close_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit button opens the page for updating the details
                Intent intent = new Intent(getApplicationContext(), UpadateDetailsPage.class);
                startActivity(intent);
            }
        });
    }


//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.logout_text_btn:
//                Log.d("myTag", "logout");
//                auth.signOut();
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        //is back is clicked, the hamburger menu is closed
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment) {
        //This changes the fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_section, fragment);
        fragmentTransaction.commit();
    }


}