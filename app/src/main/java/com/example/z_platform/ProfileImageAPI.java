package com.example.z_platform;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;

public interface ProfileImageAPI {

    @Multipart
    @PATCH("profileImage/")
    Call<AddProfileImage> addProfileImage(
            @Part("profileImage") MultipartBody.Part profileImage
    );
}
