package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterUserPosts extends RecyclerView.Adapter<AdapterUserPosts.MyHolder> {

    Context context;
    List<Post> postList;
    FirebaseAuth auth;
    FirebaseUser user;

    public AdapterUserPosts(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_posts_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        //gets the data for the post from the list of posts
        String title = postList.get(position).getPostTitleText();
        String description = postList.get(position).getPostDescriptionText();
        int amountRequired = postList.get(position).getFundingamountText();
        int amountFunded = postList.get(position).getAmountFunded();

        //sets the text for text views
        holder.user_post_title.setText(title);
        holder.user_post_description.setText(description);
        holder.user_post_fundAmount.setText(amountFunded + " / " + amountRequired);

        //this opens the full details of the post.
        holder.viewUserPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passes the post object in the intent
                Intent intent = new Intent(context, PostFullDetails.class);
                intent.putExtra("post", postList.get(position));
                context.startActivity(intent);
            }
        });

        //used to delete post that is created by the user.
        holder.deleteUserPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets the id of the post
                String postId = postList.get(position).getPostId();

                Log.d("testing", "list size : "+postList.get(position).getImageNamesList().size());
                //this removes all images from the firebase storage one by one.
                int imgNum = postList.get(position).getImageNamesList().size();
                for (int i = 0; i <= imgNum-1; i++) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(postList.get(position).getImageNamesList().get(i));
                    storageReference.delete();
                }

                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").child(user.getUid()).child(postId);
                fquery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("mytag", "ref " + snapshot);
                        //this removes the data in real time database.
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }

                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Unable to Delete", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });




    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView user_post_title, user_post_description, user_post_fundAmount;
        Button viewUserPostBtn, deleteUserPostBtn;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            user_post_title = itemView.findViewById(R.id.user_post_title);
            user_post_description = itemView.findViewById(R.id.user_post_description);
            user_post_fundAmount = itemView.findViewById(R.id.user_post_fundAmount);
            viewUserPostBtn = itemView.findViewById(R.id.viewUserPostBtn);
            deleteUserPostBtn = itemView.findViewById(R.id.deleteUserPostBtn);

        }
    }


}
