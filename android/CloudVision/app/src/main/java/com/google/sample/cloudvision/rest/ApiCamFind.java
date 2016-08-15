package com.google.sample.cloudvision.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Body;

import okhttp3.ResponseBody;
import okhttp3.RequestBody;

public interface ApiCamFind
{
    @Multipart
    @Headers("Authorization: CloudSight YI6BzUypWikJanYbMWUt3w")
    @POST("image_requests")
    Call<ResponseBody> imageRequest(@Part("image_request[image]") RequestBody image, @Part("image_request[locale]") String locale);
}
