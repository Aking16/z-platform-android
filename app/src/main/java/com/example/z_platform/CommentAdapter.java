package com.example.z_platform;

import android.content.Context;
import android.content.Intent;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> comments){
        this.context = context;
        commentList = comments;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_card, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.name.setText(comment.getName());
        holder.username.setText(comment.getUsername());
        holder.body.setText(comment.getBody());
        holder.createdAt.setText(comment.getCreatedAt());
        Glide.with(context).load(comment.getProfileImage()).placeholder(R.color.dark_secondary).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        TextView name, username, body, createdAt;
        ImageView profileImage;
        ConstraintLayout layout;

        public CommentHolder(@NonNull View itemView) {
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
