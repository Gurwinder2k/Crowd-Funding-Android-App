package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.MyHolder>{

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context;
    private List<Donation> donationList;



    public DonationHistoryAdapter(Context context, List<Donation> donationList) {
        this.context = context;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donation_history_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationHistoryAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //gets reference to the post from firebase
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("Posts")
                .child(donationList.get(position).getPostUser()).child(donationList.get(position).getPost());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //gets the title description and amount from the snapshot
                Log.d("snapofpost", donationList.get(position).getPost());
                String title = snapshot.child("postTitleText").getValue().toString();
                String desc = snapshot.child("postDescriptionText").getValue().toString();
                String amount = String.valueOf(donationList.get(position).getAmount());
                //this sets the text on the text views
                holder.donationTitle.setText(title);
                holder.donationDescription.setText(desc);
                holder.donationAmount.setText("£ " + amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView donationTitle, donationDescription, donationAmount;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            donationTitle = itemView.findViewById(R.id.donationTitle);
            donationDescription = itemView.findViewById(R.id.donationDescription);
            donationAmount = itemView.findViewById(R.id.donationAmount);

        }

    }
}
