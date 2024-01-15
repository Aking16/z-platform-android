package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    TextView txtUsername, txtName, txtCreatedAt, txtBody;
    ImageView imgprofileImage;
    Button btnSubmit;
    EditText edtComment;
    ApiHandler apiHandler;
    RecyclerView recyclerView;
    List<Comment> commentList;

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
        btnSubmit = findViewById(R.id.btnSubmitComment);
        edtComment = findViewById(R.id.edtComment);

        apiHandler = new ApiHandler(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String userId = sh.getString("userId", "");

        HashMap data = new HashMap();
        commentList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        String postId = bundle.getString("postId");
        txtName.setText(bundle.getString("name"));
        txtUsername.setText(bundle.getString("username"));
        txtBody.setText(bundle.getString("body"));
        txtCreatedAt.setText(bundle.getString("createdAt"));

        btnSubmit.setOnClickListener(view -> {
            data.put("postId", postId);
            data.put("userId", userId);
            data.put("post", edtComment.getText().toString());

            apiHandler.postComments(data);
        });

        apiHandler.fetchComments(commentList, recyclerView, postId);
    }
}