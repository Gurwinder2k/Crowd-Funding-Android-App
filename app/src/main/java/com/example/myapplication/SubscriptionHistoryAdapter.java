package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SubscriptionHistoryAdapter extends RecyclerView.Adapter<SubscriptionHistoryAdapter.MyHolder> {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context;
    private List<Subscription> subscriptionList;

    public SubscriptionHistoryAdapter(Context context, List<Subscription> subscriptionList) {
        this.context = context;
        this.subscriptionList = subscriptionList;
    }

    @NonNull
    @Override
    public SubscriptionHistoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscriptions_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionHistoryAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //this gets the post title and description from database
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("Posts")
                .child(subscriptionList.get(position).getPostUser()).child(subscriptionList.get(position).getPost());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("snapofpost", String.valueOf(snapshot));
                String title = snapshot.child("postTitleText").getValue().toString();
                String desc = snapshot.child("postDescriptionText").getValue().toString();
                String amount = String.valueOf(subscriptionList.get(position).getAmount());
                holder.subscriptonTitle.setText(title);
                holder.subscriptionDescription.setText(desc);
                holder.subscriptionAmount.setText("£ " + amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.endSubscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this changes the boolean value in the database to false, to show that the subscription has ended
                FirebaseDatabase.getInstance().getReference("Subscriptions")
                        .child(subscriptionList.get(position).getPost()).child(subscriptionList.get(position).getKey())
                        .child("active")
                        .setValue(false);

            }
        });

    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView subscriptonTitle, subscriptionDescription, subscriptionAmount;
        Button endSubscriptionBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            subscriptonTitle = itemView.findViewById(R.id.subscriptonTitle);
            subscriptionDescription = itemView.findViewById(R.id.subscriptionDescription);
            subscriptionAmount = itemView.findViewById(R.id.subscriptionAmount);
            endSubscriptionBtn = itemView.findViewById(R.id.endSubscriptionBtn);

        }
    }
}
