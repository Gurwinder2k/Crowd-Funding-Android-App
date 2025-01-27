package com.example.myapplication;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context context;
    private List<Comment> commentList;

    private FirebaseAuth auth;
    private FirebaseUser user;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Comment comment = commentList.get(position);

        //Sets the text on text views
        holder.comment.setText(comment.getComment());
        holder.username.setText(comment.getName());

        //checks if the user that commented is the same as the current user, and changes the background and moves to the right
        if (comment.getCommentUserId().equals(user.getUid())){
            holder.RelativeLayout_comment.setBackgroundResource(R.color.comment_user);
            holder.linear_layout_comment.setGravity(Gravity.RIGHT);
        }


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, comment;
        RelativeLayout RelativeLayout_comment;

        LinearLayout linear_layout_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            RelativeLayout_comment = itemView.findViewById(R.id.RelativeLayout_comment);
            linear_layout_comment = itemView.findViewById(R.id.linear_layout_comment);

        }
    }



}
