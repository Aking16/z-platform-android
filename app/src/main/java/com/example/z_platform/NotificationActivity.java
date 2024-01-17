package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    List<Notification> notificationList;
    ImageButton btnMenu;
    ImageView headerProfileImage;
    TextView headerName, headerUsername, headerFollowLength, headerFollowerLength;
    ApiHandler apiHandler;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        drawerLayout = findViewById(R.id.navMenu);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerview);
        btnMenu = findViewById(R.id.menuBtn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        apiHandler = new ApiHandler(getApplicationContext());
        NavigationViewHandler navigationViewHandler = new NavigationViewHandler(this);
        BtmNavigationViewHandler btmNavigationViewHandler = new BtmNavigationViewHandler(this);

        SharedPreferences sh = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        userId = sh.getString("userId", "");

        navigationView.setNavigationItemSelectedListener(navigationViewHandler);
        viewHeader();
        bottomNavigationView.setOnItemSelectedListener(btmNavigationViewHandler);
        bottomNavigationView.getMenu().findItem(R.id.btmNav_notification).setChecked(true);

        btnMenu.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        apiHandler.fetchNotification(userId, result -> {
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject jsonObject = result.getJSONObject(i);
                    String body = jsonObject.getString("body");

                    Notification notification = new Notification(body);
                    notificationList.add(notification);

                    NotificationAdapter adapter = new NotificationAdapter(this, notificationList);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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