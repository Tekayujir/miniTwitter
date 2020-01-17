package com.example.minitwitter.retrofit;

import com.example.minitwitter.common.Constantes;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthTwitterClient {

    private static AuthTwitterClient instance = null;
    private AuthTwitterService authTwitterService;
    private Retrofit retrofit;

    public AuthTwitterClient() {

        // Incluir en la cabecera de la petición el token que autoriza al usuario
        // fabricando un cliente que permite asociar información a la petición
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new AuthInterceptor());
        OkHttpClient cliente = okHttpClientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(cliente) //adjunta a todas las peticiones el token
                .build();

        authTwitterService = retrofit.create(AuthTwitterService.class);
    }

    // Singleton
    public static AuthTwitterClient getInstance(){
        if(instance == null){
            instance = new AuthTwitterClient();
        }
        return instance;
    }

    public AuthTwitterService getAuthTwitterService(){
        return authTwitterService;
    }
}
