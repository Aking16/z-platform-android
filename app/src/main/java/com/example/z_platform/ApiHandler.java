package com.example.z_platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class ApiHandler {
    RequestQueue requestQueue;
    Context mContext;

    public ApiHandler(Context context) {
        mContext = context;
        requestQueue = VolleySingleton.getmInstance(context).getRequestQueue();
    }

    public void fetchPosts(List<Post> postList, RecyclerView recyclerView) {
        String url = "http://192.168.1.103:3000/api/posts";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject user = jsonObject.getJSONObject("user");
                                String name = user.getString("name");
                                String username = user.getString("username");
                                String profileImage = user.getString("profileImage");
                                String body = jsonObject.getString("body");
                                String createdAt = jsonObject.getString("createdAt");

                                Post post = new Post(body, name, username, profileImage, createdAt);
                                postList.add(post);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            PostAdapter adapter = new PostAdapter(mContext, postList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void fetchCurrentUser(HashMap data, ImageView profileImagePost) {
        String url = "http://192.168.1.103:3000/api/current";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String profileImage = response.getString("profileImage");

                            Glide.with(mContext).load(profileImage).placeholder(R.color.dark_secondary).into(profileImagePost);
                        } catch (JSONException e) {
                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void postTweet(HashMap data) {
        String url = "http://192.168.1.103:3000/api/posts";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext, "Post Created!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void signin(HashMap data, VolleyCallback callback) {
        String url = "http://192.168.1.103:3000/api/signin";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userId = response.getString("id");

                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (!userId.isEmpty()) {
                                editor.putString("userId", userId).apply();
                                callback.onSuccess("Success");
                                Toast.makeText(mContext, "Logged In!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(mContext, "We couldn't log you in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "We couldn't log you in!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void register(HashMap data, VolleyCallback callback) {
        String url = "http://192.168.1.103:3000/api/register";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userId = response.getString("id");

                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (!userId.isEmpty()) {
                                editor.putString("userId", userId).apply();
                                callback.onSuccess("Success");
                                Toast.makeText(mContext, "Logged In!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(mContext, "We couldn't log you in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "We couldn't log you in!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void editUser(HashMap data) {
        String url = "http://192.168.1.103:3000/api/edit";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext, "Your profile has been edited!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
