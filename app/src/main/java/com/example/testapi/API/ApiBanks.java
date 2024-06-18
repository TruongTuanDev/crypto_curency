package com.example.testapi.API;

import com.example.testapi.model.ListBanks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiBanks {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiBanks apiBanks = new Retrofit.Builder()
            .baseUrl("https://api.vietqr.io/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiBanks.class);

    @GET("v2/banks")
    Call<ListBanks> getListBanksVN();
}
