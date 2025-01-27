package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class SearchPage extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    RecyclerView recyclerView;
    List<Post> postList;
    AdapterAllPosts adapterAllPosts;


    public SearchPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_page, container, false);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

        }

        recyclerView = view.findViewById(R.id.allPostsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPosts();

        return view;

    }

    private void loadPosts() {
        //this loads all the posts from the database, same was described in PostsCreatedFragment.java
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot allUsers : snapshot.getChildren()) {
                    for (DataSnapshot ds : allUsers.getChildren()) {
                        //                    Post post = ds.getValue(Post.class);
                        if (ds.getChildrenCount() == 12) {
                            String postId = ds.getKey();
                            String userId = ds.child("userId").getValue().toString();
                            String postTitleText = ds.child("postTitleText").getValue().toString();
                            String postDescriptionText = ds.child("postDescriptionText").getValue().toString();
                            int fundingamountText = Integer.parseInt(ds.child("fundingamountText").getValue().toString());
                            String doornumText = ds.child("doornumText").getValue().toString();
                            String streetnameText = ds.child("streetnameText").getValue().toString();
                            String postcodeText = ds.child("postcodeText").getValue().toString();
                            boolean goalReached = Boolean.parseBoolean(ds.child("goalReached").getValue().toString());
                            int amountFunded = Integer.parseInt(ds.child("amountFunded").getValue().toString());
                            List<String> imageNamesList = new ArrayList<>();
                            for (DataSnapshot d : ds.child("imageNamesList").getChildren()) {
                                imageNamesList.add(d.getValue().toString());
                            }


                            Post post = new Post(postId, userId, postTitleText, postDescriptionText, fundingamountText,
                                    doornumText, streetnameText, postcodeText, imageNamesList, amountFunded, goalReached);


                            postList.add(post);

                            adapterAllPosts = new AdapterAllPosts(getActivity(), postList);
                            recyclerView.setAdapter(adapterAllPosts);
                        }

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "An Error Occured " + error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


}