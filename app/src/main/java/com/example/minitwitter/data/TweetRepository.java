package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sirve para traer la conexión al webservice
 */
public class TweetRepository {

    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;

    TweetRepository(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
    }

    /**
     * Obtiene la información de todos los tweets en un LiveData
     *
     * @return data
     */
    public MutableLiveData<List<Tweet>> getAllTweets(){
        if(allTweets == null){
            allTweets = new MutableLiveData<>();
        }

        Call<List<Tweet>> call = authTwitterService.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if(response.isSuccessful()){
                    // asignando la respuesta a la lista de tweets
                    allTweets.setValue(response.body());
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha salido mal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });

        return allTweets;
    }

    /**
     * Crea un nuevo tweet
     *
     * @param mensaje
     */
    public void createTweet(String mensaje){
        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(mensaje);
        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                // actualizar la lista de tweets
                if(response.isSuccessful()){
                    List<Tweet> listaClonada = new ArrayList<>();

                    // añadimos en primer lugar el nuevo tweet que nos llega del servidor
                    listaClonada.add(response.body());
                    for(int i=0; i<allTweets.getValue().size(); i++){
                        listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                    }

                    allTweets.setValue(listaClonada);
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha salido mal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Da like o lo quita a un tweet
     *
     * @param idTweet
     */
    public void likeTweet(final int idTweet){
        Call<Tweet> call = authTwitterService.likeTweet(idTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                // actualizar la lista de tweets
                if(response.isSuccessful()){
                    List<Tweet> listaClonada = new ArrayList<>();

                    for(int i=0; i<allTweets.getValue().size(); i++){
                        // si el elemento actual es igual al tweet que hemos marcado como like
                        if(allTweets.getValue().get(i).getId() == idTweet){
                            /* si hemos encontrado en la lista original el elemento sobre el que hemos echo like,
                               introducimos el elemento que nos ha llegado del servidor*/
                            listaClonada.add(response.body());
                        }else {
                            listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }

                    allTweets.setValue(listaClonada);
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha salido mal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
