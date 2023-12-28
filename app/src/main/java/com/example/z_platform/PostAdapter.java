package com.example.z_platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PosterHolder> {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        postList = posts;
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
        Glide.with(context).load(post.getProfileImage()).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PosterHolder extends RecyclerView.ViewHolder {
        TextView name, username, body, createdAt;
        ImageView profileImage;

        public PosterHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            body = itemView.findViewById(R.id.body);
            createdAt = itemView.findViewById(R.id.createdAt);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
