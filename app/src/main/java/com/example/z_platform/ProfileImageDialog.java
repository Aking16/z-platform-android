package com.example.z_platform;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileImageDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private Dialog dialog;
    private ImageView profileImage;
    private Button btnBrowse;
    private Context mContext;
    Activity activity;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();
    private String userId;
    String path;
    Uri imageUri;

    public static ProfileImageDialog display(FragmentManager fragmentManager) {
        ProfileImageDialog editDialog = new ProfileImageDialog();
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
        View view = inflater.inflate(R.layout.profile_image_dialog, container, false);

        mContext = getContext();
        activity = (Activity) mContext;

        requestQueue = VolleySingleton.getmInstance(mContext).getRequestQueue();

        toolbar = view.findViewById(R.id.toolbar);
        profileImage = view.findViewById(R.id.profileImage);
        btnBrowse = view.findViewById(R.id.btnBrowse);

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
            addProfileImage();
            dismiss();
            return true;
        });

        btnBrowse.setOnClickListener(v -> {
            showFileChooser();
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

                            Glide.with(mContext).load(profileImageURL).placeholder(R.color.dark_secondary).into(profileImage);
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

    private void showFileChooser() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            path = RealPathUtil.getRealPath(mContext, uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            profileImage.setImageBitmap(bitmap);
        }
    }

    public void addProfileImage() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.103:3000/api/edit/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        ProfileImageAPI profileImageAPI = retrofit.create(ProfileImageAPI.class);
        Call<AddProfileImage> call = profileImageAPI.addProfileImage(body);
        call.enqueue(new Callback<AddProfileImage>() {
            @Override
            public void onResponse(Call<AddProfileImage> call, retrofit2.Response<AddProfileImage> response) {
                if (response.isSuccessful()) {

                    if (!response.body().getProfileImage().isEmpty()) {
                        Toast.makeText(activity, "Image Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "not Added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String error = response.errorBody().string().toString();
                        Log.d("Error:", error);
                        Log.d("Path:", path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddProfileImage> call, Throwable t) {
                Toast.makeText(activity, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
