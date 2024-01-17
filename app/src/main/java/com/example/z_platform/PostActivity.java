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
import android.view.Window;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    TextView txtUsername, txtName, txtCreatedAt, txtBody, headerName, headerUsername,
            headerFollowLength, headerFollowerLength;
    ImageView imgprofileImage, profileImageComment, headerProfileImage;
    Button btnSubmit;
    ImageButton menuBtn, btnBack;
    EditText edtComment;
    ApiHandler apiHandler;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    List<Comment> commentList;
    String userId;

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
        userId = sh.getString("userId", "");

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
        commentList = new ArrayList<>();

        menuBtn.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        viewHeader();
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);

        btnSubmit.setOnClickListener(view -> {
            if (!edtComment.getText().toString().isEmpty()) {
                data.put("post", edtComment.getText().toString());
                apiHandler.postComments(data, result -> {
                });
            } else {
                Toast.makeText(this, "Your comment is empty or invalid!", Toast.LENGTH_SHORT).show();
            }
        });

        apiHandler.fetchComments(postId, r -> {
            for (int i = 0; i < r.length(); i++) {
                try {
                    JSONObject jsonObject = r.getJSONObject(i);
                    JSONObject user = jsonObject.getJSONObject("user");

                    String body = jsonObject.getString("body");
                    String createdAt = jsonObject.getString("createdAt");
                    String name = user.getString("name");
                    String username = user.getString("username");
                    String profileImage = user.getString("profileImage");

                    Comment comment = new Comment(body, name, username, profileImage, createdAt);
                    commentList.add(comment);

                    CommentAdapter adapter = new CommentAdapter(this, commentList);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        apiHandler.fetchCurrentUser(userId, result -> {
        });
        changeSystemColor();
    }

    public void viewHeader() {
        View nav_header = navigationView.getHeaderView(0);

        headerName = nav_header.findViewById(R.id.headerName);
        headerUsername = nav_header.findViewById(R.id.headerUsername);
        headerFollowLength = nav_header.findViewById(R.id.headerFollowLength);
        headerFollowerLength = nav_header.findViewById(R.id.headerFollowerLength);
        headerProfileImage = nav_header.findViewById(R.id.headerProfileImage);

        apiHandler.fetchCurrentUser(userId, result -> {
            try {
                String name = result.getString("name");
                String username = result.getString("username");
                String profileImage = result.getString("profileImage");
                JSONArray following = result.getJSONArray("followingIds");
                String follower = result.getString("followersCount");

                headerName.setText(name);
                headerUsername.setText('@' + username);
                headerFollowLength.setText(Integer.toString(following.length()));
                headerFollowerLength.setText(follower);
                Glide.with(this).load(profileImage).placeholder(R.color.dark_secondary).into(headerProfileImage);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void changeSystemColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.dark_primary));
            window.setNavigationBarColor(getColor(R.color.dark_primary));
        }
    }
}