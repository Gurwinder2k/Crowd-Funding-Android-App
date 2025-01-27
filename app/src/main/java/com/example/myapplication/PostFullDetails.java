package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostFullDetails extends AppCompatActivity {

    //the code in this file is similar to the AdapterAllPosts.java,
    FirebaseAuth auth;
    FirebaseUser user;

    TextView post_title, name, funds_required, post_desc, likeNum;
    ImageButton likeBtn, commentBtn, shareBtn, reportBtn;

    Button donate_btn, subscribe_btn;

    List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_full_details);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Post post = (Post) getIntent().getSerializableExtra("post");

        ImageSlider imageSlider = findViewById(R.id.slider);

        List<SlideModel> slideModels = new ArrayList<>();
        for (String img : post.getImageNamesList()) {
            Log.d("img", img);
            slideModels.add(new SlideModel(img, null, null));
            imageList.add(img);
        }

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);

        Log.d("img", imageList.get(0));

        post_title = findViewById(R.id.post_title);
        funds_required = findViewById(R.id.funds_required);
        post_desc = findViewById(R.id.post_desc);
        likeNum = findViewById(R.id.likeNum);

        likeBtn = findViewById(R.id.likeBtn);
        commentBtn = findViewById(R.id.commentBtn);
        shareBtn = findViewById(R.id.shareBtn);
        reportBtn = findViewById(R.id.reportBtn);

        donate_btn = findViewById(R.id.donate_btn);
        subscribe_btn = findViewById(R.id.subscribe_btn);

        post_title.setText(post.getPostTitleText());
        post_desc.setText(post.getPostDescriptionText());


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts")
                .child(post.getUserId()).child(post.getPostId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //This gets the funding amount required and the amount already funded, and displays it on the screen
                Log.d("testing100", post.getPostId());
                long amountFunded = (long) snapshot.child("amountFunded").getValue();
                long fundingamountText = (long) snapshot.child("fundingamountText").getValue();

                funds_required.setText("Funded: £" + amountFunded + " / " + fundingamountText);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        isLiked(post.getPostId(), likeBtn);
        numLikes(likeNum, post.getPostId());

        isreported(post.getPostId(), reportBtn);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeBtn.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(user.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(user.getUid()).removeValue();
                }
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostFullDetails.this, CommentsPage.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "Hope Funding\n"
                                + post.getPostTitleText() + "\n"
                                + post.getPostDescriptionText() + "\n";

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap bmp = bitmap;
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "Image", null);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.putExtra(Intent.EXTRA_TEXT, content);
                        Uri screenshotUri = Uri.parse(path);
                        share.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        share.setType("image/*");
                        startActivity(Intent.createChooser(share, "Share Post"));
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) { }
                };
                Picasso.get().load(imageList.get(0)).into(target);

            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportBtn.getTag().equals("report")) {
                    FirebaseDatabase.getInstance().getReference().child("Reports").child(post.getPostId())
                            .child(user.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Reports").child(post.getPostId())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostFullDetails.this, DonationAmountPage.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }
        });

        subscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostFullDetails.this, SubscriptionTierPage.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }
        });


    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void isLiked(String postId, ImageButton imageView) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.baseline_favorite_24);
                    imageView.setTag("liked");
                } else {
                    Log.d("like", "here like");
                    imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void numLikes(final TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " like(s)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isreported(String postId, ImageButton reportBtn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Reports")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists()) {
                    reportBtn.setImageResource(R.drawable.baseline_flag_24);
                    reportBtn.setTag("reported");
                } else {
                    reportBtn.setImageResource(R.drawable.baseline_outlined_flag_24);
                    reportBtn.setTag("report");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}