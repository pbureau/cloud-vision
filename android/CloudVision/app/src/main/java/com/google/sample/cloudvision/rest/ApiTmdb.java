package com.google.sample.cloudvision.rest;

import com.google.sample.cloudvision.model.MovieResponse;
 
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiTmdb
{
  @GET("movie/top_rated")
  Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

  @GET("movie/{id}")
  Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
