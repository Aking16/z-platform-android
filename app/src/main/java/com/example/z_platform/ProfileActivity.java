package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    List<Post> postList;
    ImageView profileImage, coverImage, headerProfileImage;
    TextView txtName, txtUsername, txtBio, txtJoinedAt, txtFollowLength, txtFollowerLength, txtTitle,
            headerName, headerUsername, headerFollowLength, headerFollowerLength;
    ImageButton btnMenu, btnBack;
    Button btnEditProfile;
    ApiHandler apiHandler;
    String shUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout = findViewById(R.id.navMenu);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerview);
        profileImage = findViewById(R.id.profileImage);
        coverImage = findViewById(R.id.coverImage);
        txtName = findViewById(R.id.txtName);
        txtUsername = findViewById(R.id.txtUsername);
        txtBio = findViewById(R.id.txtBio);
        txtJoinedAt = findViewById(R.id.txtJoineDate);
        txtFollowLength = findViewById(R.id.txtFollowLength);
        txtFollowerLength = findViewById(R.id.txtFollowerLength);
        txtTitle = findViewById(R.id.txtTitle);
        btnMenu = findViewById(R.id.menuBtn);
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        apiHandler = new ApiHandler(getApplicationContext());
        NavigationViewHandler navigationViewHandler = new NavigationViewHandler(this);
        BtmNavigationViewHandler btmNavigationViewHandler = new BtmNavigationViewHandler(this);

        SharedPreferences sh = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        shUserId = sh.getString("userId", "");

        Bundle bundle = getIntent().getExtras();
        String userId = bundle.getString("userId");

        HashMap data = new HashMap();
        data.put("followerId", shUserId);
        data.put("followId", userId);

        btnMenu.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        viewHeader();
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);
        bottomNavigationView.getMenu().findItem(R.id.btmNav_profile).setChecked(true);

        btnBack.setOnClickListener(view -> {
            if (!userId.equals(shUserId)) {
                finish();
            } else {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        if (!userId.equals(shUserId)) {
            apiHandler.fetchCurrentUser(shUserId, result -> {
                try {
                    JSONArray followingIds = result.getJSONArray("followingIds");
                    if (followingIds.toString().contains(userId)) {
                        btnEditProfile.setText("Following");
                        btnEditProfile.setBackground(getDrawable(R.drawable.btn_white));
                        btnEditProfile.setTextColor(getColor(R.color.dark_primary));
                        btnEditProfile.setOnClickListener(view -> {
                            apiHandler.unFollow(data, r -> {
                                btnEditProfile.setText("Follow");
                                btnEditProfile.setBackground(getDrawable(R.drawable.btn_primary));
                                btnEditProfile.setTextColor(getColor(R.color.dark_foreground));
                            });
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            btnEditProfile.setText("Follow");
            btnEditProfile.setOnClickListener(view -> {
                apiHandler.follow(data, result -> {
                    btnEditProfile.setText("Following");
                    btnEditProfile.setBackground(getDrawable(R.drawable.btn_white));
                    btnEditProfile.setTextColor(getColor(R.color.dark_primary));
                });
            });
        } else {
            btnEditProfile.setOnClickListener(view -> {
                EditDialog.display(getSupportFragmentManager());
            });
        }


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
        apiHandler.fetchUser(userId, result -> {
            try {
                String name = result.getString("name");
                String username = result.getString("username");
                String bio = result.getString("bio");
                JSONArray following = result.getJSONArray("followingIds");
                String follower = result.getString("followersCount");
                String createdAt = result.getString("createdAt");
                String profileImageURL = result.getString("profileImage");
                String coverImageURL = result.getString("coverImage");

                txtName.setText(name);
                txtUsername.setText('@' + username);
                txtTitle.setText(name);
                if (!bio.equals("null")) {
                    txtBio.setText(bio);
                } else {
                    txtBio.setVisibility(View.GONE);
                }
                txtFollowLength.setText(Integer.toString(following.length()));
                txtFollowerLength.setText(follower);
                txtJoinedAt.setText("Joined at " + dateFormatter(createdAt));
                Glide.with(ProfileActivity.this).load(profileImageURL).placeholder(R.color.dark_secondary).into(profileImage);
                Glide.with(ProfileActivity.this).load(coverImageURL).into(coverImage);
            } catch (JSONException e) {
                Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        changeSystemColor();
    }

    public void viewHeader(){
        View nav_header = navigationView.getHeaderView(0);

        headerName = nav_header.findViewById(R.id.headerName);
        headerUsername = nav_header.findViewById(R.id.headerUsername);
        headerFollowLength = nav_header.findViewById(R.id.headerFollowLength);
        headerFollowerLength = nav_header.findViewById(R.id.headerFollowerLength);
        headerProfileImage = nav_header.findViewById(R.id.headerProfileImage);

        apiHandler.fetchCurrentUser(shUserId, result -> {
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

    public static String dateFormatter(String date) {
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault());

        return (DateTimeFormatter.ofPattern("MMMM dd, yyyy").format(inst));
    }
}