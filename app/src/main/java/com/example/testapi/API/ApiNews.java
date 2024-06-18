package com.example.testapi.API;


import com.example.testapi.model.DiscoverModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiNews {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    //https://min-api.cryptocompare.com/data/v2/news/?lang=EN&api_key={283d7ecd8fc18b8a775b3feb651323c508943b922be9b5978fe299fe21f6f0d2}
    ApiNews apiNew = new Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/data/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiNews.class);

    @GET("v2/news/?lang=EN")
    Call<DiscoverModel> getListNews();
}

