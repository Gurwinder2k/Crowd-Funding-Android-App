package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsPage extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    EditText add_comment;
    ImageButton postbtn;

    String postId;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Post post = (Post) getIntent().getSerializableExtra("post");

        postId = post.getPostId();

        recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        add_comment = findViewById(R.id.add_comment);
        postbtn = findViewById(R.id.postbtn);

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_comment.getText().toString().equals("")){
                    Toast.makeText(CommentsPage.this, "There is no comment", Toast.LENGTH_SHORT).show();
                }else{
                    addcomment();
                }
            }
        });

        readComments();

    }

    private void addcomment(){
        //Gets reference to the current user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //gets the user's name
                String fname = snapshot.child("fname").getValue().toString();
                String lname = snapshot.child("lname").getValue().toString();

                //gets reference to the post in comments table
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                        .child(postId);
                //Hashmap that holds the comment details
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("comment", add_comment.getText().toString());
                hashMap.put("userId", user.getUid());
                hashMap.put("name", fname + " " + lname);

                //the comment is added to the real time database
                reference.push().setValue(hashMap);
                //text input is cleared
                add_comment.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private  void readComments(){
        //gets reference to the post in comments table
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    //Every comment in the post is retrieved
                    String commentTxt = s.child("comment").getValue().toString();
                    String commentUserId = s.child("userId").getValue().toString();
                    String commentUserName = s.child("name").getValue().toString();
                    //new instance of the commend object is created
                    Comment c = new Comment(commentTxt, commentUserId, commentUserName);
                    //comment instance is then added to the list
                    commentList.add(c);
                }
                //the adapter is notified of the new changes
                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}