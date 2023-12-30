package com.example.z_platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class NavigationViewHandler implements NavigationView.OnNavigationItemSelectedListener{
    Context mContext;
    public NavigationViewHandler(Context context){
        mContext = context;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            editor.remove("userId").apply();
            Intent intent = new Intent(mContext.getApplicationContext(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
            return true;
        }
        return false;
    }
}
