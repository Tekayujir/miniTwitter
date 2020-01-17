package com.example.minitwitter.retrofit;

import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Clase para las peticiones privadas (con token necesario)
 **/
public interface AuthTwitterService {

    @GET("tweets/all")
    Call<List<Tweet>> getAllTweets();
}
