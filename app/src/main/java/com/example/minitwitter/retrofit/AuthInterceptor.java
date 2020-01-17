package com.example.minitwitter.retrofit;

import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Adjunta a cada petición la información del token en la cabecera de la petición
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_TOKEN);
        Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+token).build();
        return chain.proceed(request);
    }
}
