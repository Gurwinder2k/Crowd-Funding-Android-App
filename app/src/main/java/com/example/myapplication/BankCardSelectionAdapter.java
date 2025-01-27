package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BankCardSelectionAdapter extends RecyclerView.Adapter<BankCardSelectionAdapter.MyHolder> {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context;

    private List<BankCard> cardList;

    private int checkedPosition = 0;

    public BankCardSelectionAdapter(Context context, List<BankCard> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    public void setBankCards(ArrayList<BankCard> bankCards) {
        this.cardList = new ArrayList<>();
        this.cardList = bankCards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bank_card_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(cardList.get(position));

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This gets the reference to the bank card onject in firebase
                String cardKey = cardList.get(position).getKey();
                Query fquery = FirebaseDatabase.getInstance().getReference("BankCards").child(user.getUid()).child(cardKey);
                fquery.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // All data in the bank card is deleted
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Card Deleted Successfully", Toast.LENGTH_SHORT).show();
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
        return cardList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView cardNum, nameOnCard, expDate, delBtn;
        private ImageView selectedIcon;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            cardNum = itemView.findViewById(R.id.cardNum);
            nameOnCard = itemView.findViewById(R.id.nameOnCard);
            expDate = itemView.findViewById(R.id.expDate);
            delBtn = itemView.findViewById(R.id.delBtn);
            selectedIcon = itemView.findViewById(R.id.selectedIcon);
        }

        void bind(final BankCard bankCard) {
            //This selects the correct bank card, and shows the a tick for the selected card
            if (checkedPosition == -1) {
                selectedIcon.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    selectedIcon.setVisibility(View.VISIBLE);
                } else {
                    selectedIcon.setVisibility(View.GONE);
                }
            }
            //sets the text on text views
            cardNum.setText(bankCard.getCardNum());
            nameOnCard.setText(bankCard.getNameOnCard());
            expDate.setText(bankCard.getExpDate());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //this changes the position of the tick, depending on the selected card
                    selectedIcon.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });

        }

    }

    public BankCard getSelected(){
        //this returns the selected bank card
        if (checkedPosition != -1){
            return cardList.get(checkedPosition);

        }
        return null;
    }
}
