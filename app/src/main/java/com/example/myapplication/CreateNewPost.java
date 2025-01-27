package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateNewPost extends AppCompatActivity implements ImageSelectionRecyclerAdapter.CountOfImagesWhenRemoved{


    FirebaseAuth auth;
    FirebaseUser user;

    EditText postTitle, postDescription, fundingamount, doornum, streetname, postcode;

    Button createPostBtn;

    RecyclerView recyclerView;
    TextView textView;
    ImageButton pick;

    ArrayList<Uri> uri = new ArrayList<Uri>();

    ImageSelectionRecyclerAdapter adapter;

    StorageReference storageReference;


    private static final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        createPostBtn = findViewById(R.id.createPostBtn);
        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        fundingamount = findViewById(R.id.fundingamount);
        doornum = findViewById(R.id.doornum);
        streetname = findViewById(R.id.streetname);
        postcode = findViewById(R.id.postcode);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        }else{

            textView = findViewById(R.id.totalPhotos);
            recyclerView = findViewById(R.id.recyclerView_Gallery_Images);
            pick = findViewById(R.id.pick);

            adapter = new ImageSelectionRecyclerAdapter(uri, this);
            recyclerView.setLayoutManager(new GridLayoutManager(CreateNewPost.this, 3));
            recyclerView.setAdapter(adapter);

            //if read storage permission is not present then it is requested.
            if(ContextCompat.checkSelfPermission(CreateNewPost.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(CreateNewPost.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);

            }

            //This created an intent for choosing multiple images
            pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/+");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                }
            });

            createPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //these are the quests done to ensure all the fields are filled in
                    if (postTitle.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the title", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (postDescription.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the description", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (fundingamount.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the funding amount", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (doornum.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the door number", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (streetname.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the street name", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (postcode.getText().toString().isEmpty()) {
                        Toast.makeText(CreateNewPost.this, "Please enter the post code", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (uri.size() == 0) {
                        Toast.makeText(CreateNewPost.this, "Select at least 1 image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    disableInput();
                    uploadToFirebase();
                }
            });
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this gets the image data, and then checks if the number of images is less than 9.
        // images are added to the uri
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();

                for (int i = 0; i < x; i++) {
                    // Limiting the number of images
                    if (uri.size() < 9) {
                        uri.add(data.getClipData().getItemAt(i).getUri());
                    } else {
                        Toast.makeText(CreateNewPost.this, "Not Allowed to add more than 9 images", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                // Adapter is notified when new images are added and the number of selected images is shown
                adapter.notifyDataSetChanged();
                textView.setText("Photos (" + uri.size() + ")");

            } else if (data.getData() != null) {
                if (uri.size() < 9) {
                    //is the number of images is less than 9, then the image is added to the list
                    String imageURL = data.getData().getPath();
                    uri.add(Uri.parse(imageURL));
                } else {
                    Toast.makeText(CreateNewPost.this, "Not Allowed to add more than 9 images", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void clicked(int getSize) {
        // This sets the text view table to the number of images selected
        textView.setText("Photos (" + uri.size() + ")");
    }


    private void disableInput(){
        //When post is being created, all the input on the buttons and edit texts is disables
        pick.setClickable(false);

        createPostBtn.setClickable(false);

        postTitle.setClickable(false);
        postTitle.setFocusable(false);
        postTitle.setFocusableInTouchMode(false);

        postDescription.setClickable(false);
        postDescription.setFocusable(false);
        postDescription.setFocusableInTouchMode(false);

        fundingamount.setClickable(false);
        fundingamount.setFocusable(false);
        fundingamount.setFocusableInTouchMode(false);

        doornum.setClickable(false);
        doornum.setFocusable(false);
        doornum.setFocusableInTouchMode(false);

        streetname.setClickable(false);
        streetname.setFocusable(false);
        streetname.setFocusableInTouchMode(false);

        postcode.setClickable(false);
        postcode.setFocusable(false);
        postcode.setFocusableInTouchMode(false);


    }
    private void enableInput(){
        //enables all input
        pick.setClickable(true);

        createPostBtn.setClickable(true);

        postTitle.setClickable(true);
        postTitle.setFocusable(true);
        postTitle.setFocusableInTouchMode(true);

        postDescription.setClickable(true);
        postDescription.setFocusable(true);
        postDescription.setFocusableInTouchMode(true);

        fundingamount.setClickable(true);
        fundingamount.setFocusable(true);
        fundingamount.setFocusableInTouchMode(true);

        doornum.setClickable(true);
        doornum.setFocusable(true);
        doornum.setFocusableInTouchMode(true);

        streetname.setClickable(true);
        streetname.setFocusable(true);
        streetname.setFocusableInTouchMode(true);

        postcode.setClickable(true);
        postcode.setFocusable(true);
        postcode.setFocusableInTouchMode(true);
    }

    //this uploads the new post to firebase
    private void uploadToFirebase() {

        Toast.makeText(CreateNewPost.this, "Creating Post Please Wait", Toast.LENGTH_LONG).show();

        String imgFolder = UUID.randomUUID().toString();
        String postId = UUID.randomUUID().toString();
        List<String> imagesNamesList = new ArrayList<>();
        //each image in the list is added to firebase
        for (int i=0; i<uri.size(); i++){

            String randomName = UUID.randomUUID().toString();
            storageReference = FirebaseStorage.getInstance().getReference().child("images/" + user.getUid() + "/" + postId + "/" + randomName);

            Uri img = uri.get(i);
            //put image in firebase storage
            storageReference.putFile(img)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri u) {
                                    //adds the url to the list
                                    imagesNamesList.add(String.valueOf(u));
                                    if(imagesNamesList.size() == uri.size()){
                                        StoreLinks(imagesNamesList, postId);
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateNewPost.this, "Upload Failed", Toast.LENGTH_LONG).show();
                            enableInput();
                        }
                    });
        }


    }

    //this stores all the text information in real time database
    private void StoreLinks(List<String> imagesNamesList, String postId) {
        //gets the text from edit text views.
        String postTitleText = postTitle.getText().toString();
        String postDescriptionText = postDescription.getText().toString();
        int fundingamountText = Integer.parseInt(fundingamount.getText().toString());
        String doornumText = doornum.getText().toString();
        String streetnameText = streetname.getText().toString();
        String postcodeText = postcode.getText().toString();

        //creates a new instance of post object
        Post post = new Post(postId, user.getUid(), postTitleText, postDescriptionText, fundingamountText, doornumText, streetnameText, postcodeText, imagesNamesList, 0, false);

        //the instance is saved to firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(user.getUid()).child(postId)
                .setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CreateNewPost.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateNewPost.this, "Post upload failed.", Toast.LENGTH_SHORT).show();
                            enableInput();
                        }

                    }
                });

    }


}