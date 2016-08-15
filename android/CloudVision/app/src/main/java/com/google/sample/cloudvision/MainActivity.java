/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.cloudvision;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;
import android.graphics.Bitmap.CompressFormat;
import android.database.Cursor;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
/* Retrofit */
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.ResponseBody;
import okhttp3.RequestBody;
import okhttp3.MediaType;

import com.google.sample.cloudvision.rest.ApiTmdb;
import com.google.sample.cloudvision.rest.ApiCamFind;
import com.google.sample.cloudvision.model.MovieResponse;
import com.google.sample.cloudvision.model.Movie;

public class MainActivity extends AppCompatActivity 
{
  //private static final String CLOUD_VISION_API_KEY = "AIzaSyB7i_nuVzjT33HyDY0BuwhWQ6RW3SRuagk";
  private static final String CLOUD_VISION_API_KEY = "AIzaSyDqC077kQ5LpzMhim-su0Ov-9rRoKkN-kY";
  private static final String TMDB_API_KEY = "2b60aaea85b795c7ce60b9d6b1722916";
  private static final String CAMFIND_API_KEY = "YI6BzUypWikJanYbMWUt3w";
  public static final String FILE_NAME = "temp.jpg";

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int GALLERY_IMAGE_REQUEST = 1;
  public static final int CAMERA_PERMISSIONS_REQUEST = 2;
  public static final int CAMERA_IMAGE_REQUEST = 3;

  private TextView mImageDetails;
  private ImageView mMainImage;
  private ApiCamFind apiCamFind;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder
          .setMessage(R.string.dialog_select_prompt)
          .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              startGalleryChooser();
            }
          })
        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            startCamera();
          }
        });
        builder.create().show();
      }
    });

    mImageDetails = (TextView) findViewById(R.id.image_details);
    mMainImage = (ImageView) findViewById(R.id.main_image);

    /* Camfind API init */
    final String BASE_URL = "http://api.cloudsightapi.com/";
    //final String BASE_URL = "http://api.themoviedb.org/3/";

    OkHttpClient httpClient = new OkHttpClient.Builder()
      .readTimeout(350, TimeUnit.SECONDS)
      .connectTimeout(350, TimeUnit.SECONDS)
      .build();

    //Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(httpClient)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build();
    //ApiTmdb apiTmdb   = retrofit.create(ApiTmdb.class);
    apiCamFind = retrofit.create(ApiCamFind.class);

    /*
       Call<MovieResponse> call = apiTmdb.getTopRatedMovies(TMDB_API_KEY);
       call.enqueue(new Callback<MovieResponse>() {
       @Override
       public void onResponse(Call<MovieResponse>call, Response<MovieResponse> response) {
       List<Movie> movies = response.body().getResults();
       Log.d(TAG, "Number of movies received: " + movies.size());
       mImageDetails.setText("Number of movies received: " + movies.size());
       }

       @Override
       public void onFailure(Call<MovieResponse>call, Throwable t) {
    // Log error here since request failed
    Log.e(TAG, t.toString());
    mImageDetails.setText(t.toString());
       }
       });
     */
  }

  public void startGalleryChooser() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select a photo"),
        GALLERY_IMAGE_REQUEST);
  }

  public void startCamera() {
    if (PermissionUtils.requestPermission(
          this,
          CAMERA_PERMISSIONS_REQUEST,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.CAMERA)) 
    {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
      startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
    }
  }

  public File getCameraFile() {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return new File(dir, FILE_NAME);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) 
    {
      uploadImage(data.getData());
    } 
    else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) 
    {
      File file = getCameraFile();
      uploadImage(Uri.fromFile(file));
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (PermissionUtils.permissionGranted(
          requestCode,
          CAMERA_PERMISSIONS_REQUEST,
          grantResults)) {
      startCamera();
          }
      }

  public void uploadImage(Uri uri) {
    if (uri != null) {
      try {
        // scale the image to save on bandwidth
        Bitmap bitmap = scaleBitmapDown(
            MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
            1200);

        callCloudVision(bitmap, uri);
        mMainImage.setImageBitmap(bitmap);

      } catch (IOException e) {
        Log.d(TAG, "Image picking failed because " + e.getMessage());
        Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
      }
    } else {
      Log.d(TAG, "Image picker gave us a null image.");
      Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
    }
  }

  private void callCloudVision(final Bitmap bitmap, final Uri uri) throws IOException 
  {
    // Switch text to loading
    mImageDetails.setText(R.string.loading_message);

    // Do the real work in an async task, because we need to use the network anyway
    new AsyncTask<Object, Void, String>() {
      @Override
      protected String doInBackground(Object... params) {
        try {
          HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
          JsonFactory jsonFactory     = GsonFactory.getDefaultInstance();

          Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
          builder.setVisionRequestInitializer(new VisionRequestInitializer(CLOUD_VISION_API_KEY));
          builder.setApplicationName(getResources().getString(R.string.app_name));
          Vision vision = builder.build();

          List<Feature> featureList = new ArrayList<>();
          Feature labelDetection = new Feature();
          labelDetection.setType("LABEL_DETECTION");
          labelDetection.setMaxResults(10);
          featureList.add(labelDetection);

          Feature textDetection = new Feature();
          textDetection.setType("TEXT_DETECTION");
          textDetection.setMaxResults(10);
          featureList.add(textDetection);

          Feature landmarkDetection = new Feature();
          landmarkDetection.setType("LOGO_DETECTION");
          landmarkDetection.setMaxResults(10);
          featureList.add(landmarkDetection);

          List<AnnotateImageRequest> imageList      = new ArrayList<>();
          AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
          Image base64EncodedImage                  = getBase64EncodedJpeg(bitmap);

          annotateImageRequest.setImage(base64EncodedImage);
          annotateImageRequest.setFeatures(featureList);
          imageList.add(annotateImageRequest);

          BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
          batchAnnotateImagesRequest.setRequests(imageList); 

          Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
          // Due to a bug: requests to Vision API containing large images fail when GZipped.
          annotateRequest.setDisableGZipContent(true);
          Log.d(TAG, "created Cloud Vision request object, sending request");

          //MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/*");
          MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

          //RequestBody imageBody = RequestBody.create(MEDIA_TYPE_JPEG, getEncodedImageBytesFromBitmap(bitmap));

          /*
             String[] proj = { MediaStore.Images.Media.DATA };
             Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
             int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
             cursor.moveToFirst();
             String filePath = cursor.getString(columnIndex);
             cursor.close();
           */
          String[] filePathColumn = { MediaStore.Images.Media.DATA };
          Cursor cursor = getContentResolver().query(uri,filePathColumn, null, null, null);
          cursor.moveToFirst();
          //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
          int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          String filePath = cursor.getString(columnIndex);
          cursor.close();
           filePathUri = Uri.parse(cursor.getString(column_index));
           fileName = filePathUri.getLastPathSegment().toString();

          Log.d(TAG, String.format("Call uri: %s filepath: %s", uri, filePath));
          /*
          File file = new File(filePath);
          RequestBody imageBody = RequestBody.create(MEDIA_TYPE_JPEG, file);

          try {
            Log.d(TAG, "Call camfind api");
            Call<ResponseBody> call = apiCamFind.imageRequest(imageBody, "en-US");
            Log.d(TAG, "call body: " + call.request().toString());
            Log.d(TAG, "call type: " + call.request().body().contentType().toString());
            Log.d(TAG, "call size: " + call.request().body().contentLength());
            call.enqueue(new Callback<ResponseBody>() {
              @Override
              public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) 
              {
                //List<Movie> movies = response.body().getResults();
                Log.d(TAG, "Cloudvision request ok code: " + response.code());
                if(response.isSuccessful())
                {
                  try {
                    mImageDetails.setText("Couldvision request ok: " + response.body().string());
                  } catch(IOException e) {
                    Log.d(TAG, "Exception reading body");
                  }
                }
                else
                {
                  try {
                    Log.d(TAG, "Cloudvision error: " + response.errorBody().string());
                  } catch(IOException e) {
                    Log.d(TAG, "Exception reading body");
                  }
                }
              }

              @Override
              public void onFailure(Call<ResponseBody> call, Throwable t) 
              {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                mImageDetails.setText("Couldvision request error");
              }
            });
          } catch(Exception e) {
            Log.d(TAG, "Exception in call CamFind");
          }
          */

          /* Call Google cloud vision */
          BatchAnnotateImagesResponse response = annotateRequest.execute();
          return convertResponseToString(response);
        } 
        catch (GoogleJsonResponseException e) 
        {
          Log.d(TAG, "failed to make API request because " + e.getContent());
        } 
        catch (IOException e) 
        {
          Log.d(TAG, "failed to make API request because of other IOException " +
              e.getMessage());
        }
        return "Cloud Vision API request failed. Check logs for details.";
      }

      protected void onPostExecute(String result) {
        mImageDetails.setText(result);
      }
    }.execute();
  }

  public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

    int originalWidth = bitmap.getWidth();
    int originalHeight = bitmap.getHeight();
    int resizedWidth = maxDimension;
    int resizedHeight = maxDimension;

    if (originalHeight > originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
    } else if (originalWidth > originalHeight) {
      resizedWidth = maxDimension;
      resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
    } else if (originalHeight == originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = maxDimension;
    }
    return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
  }

  public Image getBase64EncodedJpeg(Bitmap bitmap) {
    Image image = new Image();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
    byte[] imageBytes = byteArrayOutputStream.toByteArray();
    image.encodeContent(imageBytes);
    return image;
  }

  public byte[] getEncodedImageBytesFromBitmap(Bitmap bitmap) 
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.JPEG, 90, stream);
    byte[] byteFormat = stream.toByteArray();

    return byteFormat;
  }

  public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) 
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.JPEG, 90, stream);
    byte[] byteFormat = stream.toByteArray();
    // get the base 64 string
    String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    return imgString;
  }

  private String convertResponseToString(BatchAnnotateImagesResponse response) 
  {
    StringBuilder message = new StringBuilder("Results:\n\n");

    message.append("Labels:\n");

    List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
    if (labels != null) {
      for (EntityAnnotation label : labels) {
        message.append(String.format(Locale.getDefault(), "%.3f: %s",
              label.getScore(), label.getDescription()));
        message.append("\n");
      }
    } else {
      message.append("nothing\n");
    }

    message.append("Texts:\n");
    List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();
    if (texts != null) {
      for (EntityAnnotation text : texts) {
        message.append(String.format(Locale.getDefault(), "%s: %s",
              text.getLocale(), text.getDescription()));
        message.append("\n");
      }
    } else {
      message.append("nothing\n");
    }

    message.append("Logos:\n");
    List<EntityAnnotation> landmarks = response.getResponses().get(0).getLogoAnnotations();
    if (landmarks != null) {
      for (EntityAnnotation landmark : landmarks) {
        message.append(String.format(Locale.getDefault(), "%.3f: %s",
              landmark.getScore(), landmark.getDescription()));
        message.append("\n");
      }
    } else {
      message.append("nothing\n");
    }

    return message.toString();
  }
}
