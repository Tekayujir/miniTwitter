package com.example.minitwitter.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.ui.BottomModalTweetFragment;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {
    private TweetRepository tweetRepository;
    private LiveData<List<Tweet>> tweets;
    private LiveData<List<Tweet>> favTweets;

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

    // Este método sirve para la funcionalidad del Swipe
    public LiveData<List<Tweet>> getNewTweets(){
        tweets = tweetRepository.getAllTweets(); // Carga la lista de tweets del webservice del repositorio
        return tweets;
    }

    /**
     * Se invoca este método para devolver la lista de tweets favoritos
     *
     * @return tweets favs
     */
    public LiveData<List<Tweet>> getFavTweets(){
        favTweets = tweetRepository.getFavsTweets();
        return favTweets;
    }

    // Este método sirve para la funcionalidad del Swipe / refrescar datos
    public LiveData<List<Tweet>> getNewFavTweets(){
        getNewTweets(); // obtiene primero toda la lista de tweets
        return getFavTweets(); // obtiene los nuevos fav tweets
    }

    /**
     * Se invoca este método para insertar un nuevo tweet
     *
     * @param mensaje
     */
    public void insertTweet(String mensaje){
        tweetRepository.createTweet(mensaje);
    }

    /**
     * Se invoca este método para dar un like
     *
     * @param idTweet
     */
    public void likeTweet(int idTweet){
        tweetRepository.likeTweet(idTweet);
    }

    /**
     * Se invoca este método para eliminar un tweet
     *
     * @param idTweet
     */
    public void deleteTweet(int idTweet){
        tweetRepository.deleteTweet(idTweet);
    }

    public void openDialogTweetMenu(Context ctx, int idTweet){
        BottomModalTweetFragment dialogTweet = BottomModalTweetFragment.newInstance(idTweet);
        dialogTweet.show(((AppCompatActivity)ctx).getSupportFragmentManager(), "BottomModalTweetFragment");
    }
}
