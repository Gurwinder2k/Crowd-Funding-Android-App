package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterAllPosts extends RecyclerView.Adapter<AdapterAllPosts.MyHolder>{

    Context context;
    List<Post> postList;

    FirebaseAuth auth;
    FirebaseUser user;

    public AdapterAllPosts(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_posts_layout, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //gets the current user

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Retrieves the posts details from the list of post
        Post post = postList.get(position);
        String postTitleText = postList.get(position).getPostTitleText();
        String postDescriptionText = postList.get(position).getPostDescriptionText();
        int fundingamountText = postList.get(position).getFundingamountText();
        String doornumText = postList.get(position).getDoornumText();
        String streetnameText = postList.get(position).getStreetnameText();
        String postcodeText = postList.get(position).getPostcodeText();
        List<String> imageNamesList = postList.get(position).getImageNamesList();

        //setting the title and description

        holder.postTitle.setText(postTitleText);
        holder.postDesc.setText(postDescriptionText);

        //function checks is the post has been liked by the user
        isLiked(post.getPostId(), holder.likeBtn);
        //sets the number of likes
        numLikes(holder.likeNum, post.getPostId());

        //checks if the user reported this post before
        isreported(post.getPostId(), holder.reportBtn);


        //Shows the first image on the post
        Picasso.get().load(imageNamesList.get(0)).into(holder.pImageIv);

        //if the image is clicked then the full details of the post are loaded
        holder.pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostFullDetails.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });

        //if like is clicked the user id is added to the likes table in firebase
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likeBtn.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(user.getUid()).setValue(true);
                }else{
                    //removes the user id from the likes table
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(user.getUid()).removeValue();
                }
            }
        });
        //Opens the comment page and passes the post object
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsPage.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });


        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates the string that will be shared
                String content = "Hope Funding\n"
                        + post.getPostTitleText() + "\n"
                        + post.getPostDescriptionText() + "\n";

                //Shares the image and the text
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap bmp = bitmap;
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "Image", null);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.putExtra(Intent.EXTRA_TEXT, content);
                        Uri screenshotUri = Uri.parse(path);
                        share.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        share.setType("image/*");
                        context.startActivity(Intent.createChooser(share, "Share Post"));
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) { }
                };
                //loads the image in to the target above
                Picasso.get().load(imageNamesList.get(0)).into(target);
            }
        });

        holder.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if post is not already reported then it is reported
                if (holder.reportBtn.getTag().equals("report")){
                    FirebaseDatabase.getInstance().getReference().child("Reports").child(post.getPostId())
                            .child(user.getUid()).setValue(true);
                }else{
                    //removes the report
                    FirebaseDatabase.getInstance().getReference().child("Reports").child(post.getPostId())
                            .child(user.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



    private void isLiked(String postId, ImageButton imageView){
        //gets the reference to the likes for the post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //changes the icon depending on if the post is liked or not
                if (snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.baseline_favorite_24);
                    imageView.setTag("liked");
                }else{
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



    private void numLikes(final TextView likes, String postId){
        //gets the reference to the post in the likes table
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //gets the number of likes for this post
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
                //changes the report icon depending on the tag
                if (snapshot.child(user.getUid()).exists()){
                    reportBtn.setImageResource(R.drawable.baseline_flag_24);
                    reportBtn.setTag("reported");
                }else{
                    reportBtn.setImageResource(R.drawable.baseline_outlined_flag_24);
                    reportBtn.setTag("report");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Not used
    private void numReports(String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Reports")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    class MyHolder extends RecyclerView.ViewHolder {
        ImageView pImageIv;
        TextView postTitle, postDesc, likeNum;
        ImageButton likeBtn, commentBtn, shareBtn, reportBtn;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            pImageIv = itemView.findViewById(R.id.pImageIv);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDesc = itemView.findViewById(R.id.postDesc);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            reportBtn = itemView.findViewById(R.id.reportBtn);
            likeNum = itemView.findViewById(R.id.likeNum);


        }
    }

}
