package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.Iterator;
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
    MutableLiveData<List<Tweet>> favTweets;
    String userName;


    TweetRepository(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
        userName = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
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

                    // para refrescar la lista de favs
                    getFavsTweets();
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
     * Obtiene la lista de tweets favoritos
     *
     * @return lista de favs
     */
    public MutableLiveData<List<Tweet>> getFavsTweets(){
        if(favTweets == null){
            favTweets = new MutableLiveData<>();
        }

        // Recorremos la lista para saber cuales son los tweets marcados con like y cuales no
        List<Tweet> newFavList = new ArrayList<>();
        Iterator itTweets = allTweets.getValue().iterator();

        while(itTweets.hasNext()){
            Tweet current = (Tweet) itTweets.next(); // tweet actual
            Iterator itLikes = current.getLikes().iterator(); // iterar los likes
            boolean encontrado = false; // uauario no encontrado

            // mientras haya elementos en la lista de likes y además no hayamos encontrado al usuario logeado en esa lista de likes, seguimos recorriendo la lista,
            while(itLikes.hasNext() && !encontrado){
                Like like = (Like) itLikes.next();
                // si el nombre de usuario es igual al nombre del usuario loggeado, le ha dado like
                if(like.getUsername().equals(userName)){
                    encontrado = true;
                    newFavList.add(current);
                    //tweet con like añadido a la lista
                }
            }
        }

        // si algún observer está pendiente de este objeto, va a poder recibir la nueva lista de favoritos
        favTweets.setValue(newFavList);

        return favTweets;
    }

    /**
     * Elimina un tweet
     *
     * @param idTweet
     */
    public void deleteTweet(final int idTweet){
        Call<TweetDeleted> call = authTwitterService.deleteTweet(idTweet);

        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if(response.isSuccessful()){
                    // Creando una lista de tweets donde los eliminados no aparezcan
                    List<Tweet> clonedTweets = new ArrayList<>();
                    for(int i=0; i<allTweets.getValue().size(); i++){
                        // si el id del tweet es distinto del tweet que acabamos de eliminar, debe conservarse en la lista
                        if(allTweets.getValue().get(i).getId() != idTweet){
                            clonedTweets.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }

                    allTweets.setValue(clonedTweets);
                    getFavsTweets();

                    Toast.makeText(MyApp.getContext(), "Tweet eliminado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha salido mal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
