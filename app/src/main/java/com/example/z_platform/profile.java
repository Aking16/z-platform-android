package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class profile extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    List<Post> postList;
    ImageView profileImage, coverImage;
    TextView txtName, txtUsername, txtBio, txtJoinedAt, txtFollowLength, txtFollowerLength, txtTitle;
    ImageButton btnMenu, btnBack;
    Button btnEditProfile;
    String ip;
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
        ip = getString(R.string.ip);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        postList = new ArrayList<>();
        ApiHandler apiHandler = new ApiHandler(getApplicationContext());
        NavigationViewHandler navigationViewHandler = new NavigationViewHandler(this);
        BtmNavigationViewHandler btmNavigationViewHandler = new BtmNavigationViewHandler(this);

        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String userId = sh.getString("userId", "");

        HashMap data = new HashMap();
        data.put("userId", userId);

        btnMenu.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);
        bottomNavigationView.getMenu().findItem(R.id.btmNav_profile).setChecked(true);

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(profile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnEditProfile.setOnClickListener(view -> {
            EditDialog.display(getSupportFragmentManager());
        });

        apiHandler.fetchPosts(postList, recyclerView, "");
        fetchUser(data);
        hideSystemBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT < 16){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void fetchUser(HashMap data) {
        String url = ip + "api/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String bio = response.getString("bio");
                            JSONArray following = response.getJSONArray("followingIds");
                            String follower = response.getString("followersCount");
                            String createdAt = response.getString("createdAt");
                            String profileImageURL = response.getString("profileImage");
                            String coverImageURL = response.getString("coverImage");

                            txtName.setText(name);
                            txtUsername.setText('@' + username);
                            txtTitle.setText(name);
                            if(!bio.equals("null")){
                                txtBio.setText(bio);
                            } else {
                                txtBio.setVisibility(View.GONE);
                            }
                            txtFollowLength.setText(Integer.toString(following.length()));
                            txtFollowerLength.setText(follower);
                            txtJoinedAt.setText("Joined at " + dateFormatter(createdAt));
                            Glide.with(profile.this).load(profileImageURL).placeholder(R.color.dark_secondary).into(profileImage);
                            Glide.with(profile.this).load(coverImageURL).into(coverImage);
                        } catch (JSONException e) {
                            Toast.makeText(profile.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(profile.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public static String dateFormatter(String date) {
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault());

        return (DateTimeFormatter.ofPattern("MMMM dd, yyyy").format(inst));
    }
}