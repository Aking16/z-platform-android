package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class PostActivity extends AppCompatActivity {
    ApiHandler apiHandler;
    RecyclerView recyclerView;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        recyclerView = findViewById(R.id.recyclerviewPost);

        apiHandler = new ApiHandler(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        String postId = bundle.getString("postId");

        apiHandler.fetchPosts(postList, recyclerView, postId, null);
    }
}