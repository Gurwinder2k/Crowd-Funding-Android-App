package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class PostsCreatedFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    RecyclerView recyclerView;
    List<Post> postList;
    AdapterUserPosts adapterUserPosts;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts_created, container, false);

        Button createPostBtn = view.findViewById(R.id.createPostBtn);
        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this opens the activity for create a new post
                Intent intent = new Intent(getActivity(), CreateNewPost.class);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.userPostsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts();

        return view;

    }

    private void loadPosts() {
        //this loads all the post from the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        DatabaseReference r = reference.child(user.getUid());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                int t = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //this gets all the post data
                    if(ds.getChildrenCount() == 12){
                        String postId = ds.getKey();
                        String userId = ds.child("userId").getValue().toString();
                        String postTitleText = ds.child("postTitleText").getValue().toString();
                        String postDescriptionText = ds.child("postDescriptionText").getValue().toString();
                        int fundingamountText = Integer.parseInt(ds.child("fundingamountText").getValue().toString());
                        String doornumText = ds.child("doornumText").getValue().toString();
                        String streetnameText = ds.child("streetnameText").getValue().toString();
                        String postcodeText = ds.child("postcodeText").getValue().toString();
                        boolean goalReached = (boolean) ds.child("goalReached").getValue();
                        int amountFunded = Integer.parseInt(ds.child("amountFunded").getValue().toString());
                        List<String> imageNamesList = new ArrayList<>();
                        for (DataSnapshot d : ds.child("imageNamesList").getChildren()) {
                            imageNamesList.add(d.getValue().toString());
                        }

                        //post object is created
                        Post post = new Post(postId, userId, postTitleText, postDescriptionText, fundingamountText,
                                doornumText, streetnameText, postcodeText, imageNamesList, amountFunded, goalReached);

                        //post is added to the list
                        postList.add(post);

                        //the adapter is created and list is passed to it
                        adapterUserPosts = new AdapterUserPosts(getActivity(), postList);
                        recyclerView.setAdapter(adapterUserPosts);
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