package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PostActivity extends AppCompatActivity {
    TextView txtUsername, txtName, txtCreatedAt, txtBody;
    ImageView imgprofileImage;
    ApiHandler apiHandler;
    RecyclerView recyclerView;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        recyclerView = findViewById(R.id.recyclerviewComment);
        txtUsername = findViewById(R.id.username);
        txtName = findViewById(R.id.name);
        txtCreatedAt = findViewById(R.id.createdAt);
        txtBody = findViewById(R.id.body);
        imgprofileImage = findViewById(R.id.profileImage);

        apiHandler = new ApiHandler(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        String postId = bundle.getString("postId");
        txtName.setText(bundle.getString("name"));
        txtUsername.setText(bundle.getString("username"));
        txtBody.setText(bundle.getString("body"));
        txtCreatedAt.setText(bundle.getString("createdAt"));

    }
}