package com.example.z_platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class BtmNavigationViewHandler implements NavigationBarView.OnItemSelectedListener {
    Context mContext;
    public BtmNavigationViewHandler(Context context){
        mContext = context;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btmNav_home){
            Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
            return true;
        }
        if (id == R.id.btmNav_profile) {
            Intent intent = new Intent(mContext.getApplicationContext(), profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
            return true;
        }
        return false;
    }
}
