package com.example.myapplication;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageSelectionRecyclerAdapter extends RecyclerView.Adapter<ImageSelectionRecyclerAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    CountOfImagesWhenRemoved countOfImagesWhenRemoved;


    public ImageSelectionRecyclerAdapter(ArrayList<Uri> uriArrayList, CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
        this.uriArrayList = uriArrayList;
        this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
    }

    @NonNull
    @Override
    public ImageSelectionRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);

        return new ViewHolder(view, countOfImagesWhenRemoved);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectionRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageURI(uriArrayList.get(position));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this removes the images from the list
                uriArrayList.remove(uriArrayList.get(position));
                //this notifies the adapter  which will update the images shown
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                countOfImagesWhenRemoved.clicked(uriArrayList.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, delete;
        CountOfImagesWhenRemoved countOfImagesWhenRemoved;
        public ViewHolder(@NonNull View itemView, CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
            super(itemView);
            this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
            imageView = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    public interface CountOfImagesWhenRemoved{
        void clicked(int getSize);
    }
}
