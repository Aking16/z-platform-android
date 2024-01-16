package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    TextView txtUsername, txtName, txtCreatedAt, txtBody;
    ImageView imgprofileImage, profileImageComment;
    Button btnSubmit;
    ImageButton menuBtn, btnBack;
    EditText edtComment;
    ApiHandler apiHandler;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
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
        profileImageComment = findViewById(R.id.profileImageComment);
        btnSubmit = findViewById(R.id.btnSubmitComment);
        btnBack = findViewById(R.id.btnBack);
        menuBtn = findViewById(R.id.menuBtn);
        edtComment = findViewById(R.id.edtComment);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        apiHandler = new ApiHandler(getApplicationContext());
        NavigationViewHandler navigationViewHandler = new NavigationViewHandler(this);
        BtmNavigationViewHandler btmNavigationViewHandler = new BtmNavigationViewHandler(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String userId = sh.getString("userId", "");

        Bundle bundle = getIntent().getExtras();
        String postId = bundle.getString("postId");
        txtName.setText(bundle.getString("name"));
        txtUsername.setText(bundle.getString("username"));
        txtBody.setText(bundle.getString("body"));
        txtCreatedAt.setText(bundle.getString("createdAt"));
        Glide.with(this).load(bundle.getString("profileImage")).placeholder(R.color.dark_secondary).into(imgprofileImage);

        HashMap data = new HashMap();
        data.put("userId", userId);
        data.put("postId", postId);
        data.put("userId", userId);
        commentList = new ArrayList<>();

        menuBtn.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);

        btnSubmit.setOnClickListener(view -> {
            data.put("post", edtComment.getText().toString());

            apiHandler.postComments(data, result -> apiHandler.fetchComments(commentList, recyclerView, postId));
        });

        apiHandler.fetchComments(commentList, recyclerView, postId);
        apiHandler.fetchCurrentUser(data, profileImageComment);
        hideSystemBar();
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}