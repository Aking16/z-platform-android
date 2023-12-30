package com.example.z_platform;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private Dialog dialog;
    private EditText edtProfileName, edtProfileUsername, edtProfileBio;
    private ImageView profileImage, coverImage;
    private Context mContext;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();
    private String userId;

    public static EditDialog display(FragmentManager fragmentManager) {
        EditDialog editDialog = new EditDialog();
        editDialog.show(fragmentManager, TAG);
        return editDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Theme_Z_Platform_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Base_Theme_Z_Platform_Slide);
            hideSystemBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.edit_dialog, container, false);

        mContext = getContext();

        requestQueue = VolleySingleton.getmInstance(mContext).getRequestQueue();

        toolbar = view.findViewById(R.id.toolbar);
        edtProfileName = view.findViewById(R.id.edtProfileName);
        edtProfileUsername = view.findViewById(R.id.edtProfileUsername);
        edtProfileBio = view.findViewById(R.id.edtProfileBio);
        profileImage = view.findViewById(R.id.profileImage);
        coverImage = view.findViewById(R.id.coverImage);

        SharedPreferences sh = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        userId = sh.getString("userId", "");
        apiHandler = new ApiHandler(mContext);

        data.put("userId", userId);

        fetchUser(data);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Edit Profile");
        toolbar.inflateMenu(R.menu.dialog_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            String name = edtProfileName.getText().toString();
            String username = edtProfileUsername.getText().toString();
            String bio = edtProfileBio.getText().toString();

            data.put("userId", userId);
            data.put("name", name);
            data.put("username", username);
            data.put("bio", bio);

            apiHandler.editUser(data);
            dismiss();
            return true;
        });
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT < 16) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = dialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void fetchUser(HashMap data) {
        String url = "http://192.168.1.103:3000/api/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String bio = response.getString("bio");
                            String profileImageURL = response.getString("profileImage");
                            String coverImageURL = response.getString("coverImage");

                            edtProfileName.setText(name);
                            edtProfileUsername.setText(username);
                            if (!bio.equals("null")) {edtProfileBio.setText(bio);}
                            Glide.with(mContext).load(profileImageURL).into(profileImage);
                            Glide.with(mContext).load(coverImageURL).into(coverImage);
                        } catch (JSONException e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
