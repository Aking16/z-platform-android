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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private Dialog dialog;
    private EditText edtProfileName, edtProfileUsername, edtProfileBio;
    private ImageView profileImage, coverImage;
    private Context mContext;
    Activity activity;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();
    private String userId;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String filePath;
    int urlDirector;
    String ip;

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
            changeSystemColor();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.edit_dialog, container, false);

        mContext = getContext();
        activity = (Activity) mContext;
        ip = getString(R.string.ip);

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

        profileImage.setOnClickListener(v -> {
            urlDirector = 1;
            imageBrowse();
        });

        coverImage.setOnClickListener(v -> {
            urlDirector = 2;
            imageBrowse();
        });
    }

    public void changeSystemColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getColor(R.color.dark_primary));
            window.setNavigationBarColor(activity.getColor(R.color.dark_primary));
        }
    }

    private void imageBrowse() {
        if ((ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && dataIntent != null && dataIntent.getData() != null) {
            Uri picUri = dataIntent.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), picUri);
                    uploadBitmap(bitmap, new VolleyCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            data.put("userId", userId);
                            fetchUser(data);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, "no image selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap, VolleyCallback callback) {
        String url = null;
        if (urlDirector == 1) {
            url = ip + "api/edit/profileImage";
        } else if (urlDirector == 2) {
            url = ip + "api/edit/coverImage";
        }

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PATCH, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (urlDirector == 1) {
                            Toast.makeText(activity, "Profile Image successfully edited!", Toast.LENGTH_SHORT).show();
                        } else if (urlDirector == 2) {
                            Toast.makeText(activity, "Cover Image successfully edited!", Toast.LENGTH_SHORT).show();
                        }
                        callback.onSuccess("Success");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "We couldn't edit your Profile Image.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (urlDirector == 1) {
                    params.put("profileImage", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                } else if (urlDirector == 2) {
                    params.put("coverImage", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                }
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                return params;
            }
        };

        requestQueue.add(volleyMultipartRequest);
    }

    private void fetchUser(HashMap data) {
        String url = ip + "api/user";

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
                            if (!bio.equals("null")) {
                                edtProfileBio.setText(bio);
                            }
                            Glide.with(mContext).load(profileImageURL).placeholder(R.color.dark_secondary).into(profileImage);
                            Glide.with(mContext).load(coverImageURL).placeholder(R.color.dark_secondary).into(coverImage);
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
