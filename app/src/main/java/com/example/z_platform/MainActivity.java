package com.example.z_platform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    List<Post> postList;
    EditText edtPost;
    ImageButton btnMenu;
    Button btnSubmitPost;
    ImageView profileImagePost, headerProfileImage;
    TextView headerName, headerUsername, headerFollowLength, headerFollowerLength;
    ApiHandler apiHandler;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.navMenu);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerview);
        btnMenu = findViewById(R.id.menuBtn);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        profileImagePost = findViewById(R.id.profileImagePost);
        edtPost = findViewById(R.id.edtPost);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        apiHandler = new ApiHandler(getApplicationContext());
        NavigationViewHandler navigationViewHandler = new NavigationViewHandler(this);
        BtmNavigationViewHandler btmNavigationViewHandler = new BtmNavigationViewHandler(this);

        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        userId = sh.getString("userId", "");

        HashMap data = new HashMap();
        data.put("userId", userId);

        btnMenu.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        viewHeader();
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);
        bottomNavigationView.getMenu().findItem(R.id.btmNav_home).setChecked(true);

        btnSubmitPost.setOnClickListener(view -> {
            if (!edtPost.getText().toString().isEmpty()) {

                data.put("body", edtPost.getText().toString());

                apiHandler.postTweet(data, new VolleyCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        fetchPost("");
                    }
                });
            } else {
                Toast.makeText(this, "Your post is empty or invalid!", Toast.LENGTH_SHORT).show();
            }
        });

        fetchPost("");
        apiHandler.fetchCurrentUser(userId, result -> {
            try {
                String profileImage = result.getString("profileImage");
                Glide.with(this).load(profileImage).placeholder(R.color.dark_secondary).into(profileImagePost);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        changeSystemColor();
    }

    public void fetchPost(String userId) {
        apiHandler.fetchPosts(userId, result -> {
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject jsonObject = result.getJSONObject(i);
                    JSONObject user = jsonObject.getJSONObject("user");
                    JSONArray comments = jsonObject.getJSONArray("comments");
                    String name = user.getString("name");
                    String id = user.getString("id");
                    String username = user.getString("username");
                    String profileImage = user.getString("profileImage");
                    String postId = jsonObject.getString("id");
                    String body = jsonObject.getString("body");
                    String createdAt = jsonObject.getString("createdAt");

                    String commentCount = String.valueOf(comments.length());

                    Post post = new Post(postId, id, body, name, username, profileImage, createdAt, commentCount);
                    postList.add(post);

                    PostAdapter adapter = new PostAdapter(this, postList);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void viewHeader(){
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