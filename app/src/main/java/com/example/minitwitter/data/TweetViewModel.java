package com.example.minitwitter.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {
    private TweetRepository tweetRepository;
    private LiveData<List<Tweet>> tweets;

    public TweetViewModel(@NonNull Application application) {
        super(application);
        tweetRepository = new TweetRepository();
        tweets = tweetRepository.getAllTweets(); // Carga la lista de tweets del webservice del repositorio
    }

    /**
     * Se invoca este método para devolver la lista de tweets
     *
     * @return tweets
     */
    public LiveData<List<Tweet>> getTweets(){
        return tweets;
    }

    /**
     * Se invoca este método para insertar un nuevo tweet
     *
     * @param mensaje
     */
    public void insertTweet(String mensaje){
        tweetRepository.createTweet(mensaje);
    }
}
