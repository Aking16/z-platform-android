package com.example.z_platform;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PosterHolder> {
    private Context context;
    private List<Post> postList;
    private String postId;

    public PostAdapter(Context context, List<Post> posts, String postId){
        this.context = context;
        postList = posts;
        this.postId = postId;
    }

    @NonNull
    @Override
    public PosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_card, parent, false);
        return new PosterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterHolder holder, int position) {
        Post post = postList.get(position);
        holder.name.setText(post.getName());
        holder.username.setText(post.getUsername());
        holder.body.setText(post.getBody());
        holder.createdAt.setText(post.getCreatedAt());
        Glide.with(context).load(post.getProfileImage()).placeholder(R.color.dark_secondary).into(holder.profileImage);

        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(context , PostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle bundle = new Bundle();
            bundle.putString("postId" , postId);
            bundle.putString("name" , post.getName());
            bundle.putString("username" , post.getUsername());
            bundle.putString("body" , post.getBody());
            bundle.putString("createdAt" , post.getCreatedAt());
            bundle.putString("profileImage" , post.getProfileImage());

            intent.putExtras(bundle);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PosterHolder extends RecyclerView.ViewHolder {
        TextView name, username, body, createdAt;
        ImageView profileImage;
        ConstraintLayout layout;

        public PosterHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            body = itemView.findViewById(R.id.body);
            createdAt = itemView.findViewById(R.id.createdAt);
            profileImage = itemView.findViewById(R.id.profileImage);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }
}
