package com.example.z_platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

public class NavigationViewHandler implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext;

    public NavigationViewHandler(Context context) {
        mContext = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userId = sharedPreferences.getString("userId", "");
        int id = item.getItemId();

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        if (id == R.id.nav_home) {
            activitySwitcher(MainActivity.class, bundle);
            return true;
        } else if (id == R.id.nav_profile) {
            activitySwitcher(ProfileActivity.class, bundle);
            return true;
        } else if (id == R.id.nav_notification) {
            activitySwitcher(NotificationActivity.class, bundle);
            return true;
        } else if (id == R.id.nav_logout) {
            editor.remove("userId").apply();
            activitySwitcher(LoginActivity.class, null);
            return true;
        }
        return false;
    }

    public void activitySwitcher(Class activity, Bundle extra) {
        Intent intent = new Intent(mContext.getApplicationContext(), activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(extra);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }
}
