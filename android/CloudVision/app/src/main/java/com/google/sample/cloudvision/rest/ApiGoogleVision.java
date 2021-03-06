package com.google.sample.cloudvision.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Body;

import okhttp3.ResponseBody;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;

import com.google.sample.cloudvision.model.GoogleVisionRequest;
import com.google.sample.cloudvision.model.GoogleVisionResponse;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;



public interface ApiGoogleVision
{
  @POST("./images:annotate")
  Call<GoogleVisionResponse> imageAnnotate(@Body GoogleVisionRequest request, @Query("key")String key);
}
