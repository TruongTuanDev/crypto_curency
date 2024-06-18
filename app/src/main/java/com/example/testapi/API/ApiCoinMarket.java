package com.example.testapi.API;

import com.example.testapi.model.DetailItem;
import com.example.testapi.model.Exchange;
import com.example.testapi.model.Market;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCoinMarket {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiCoinMarket apiCoinMarket = new Retrofit.Builder()
            .baseUrl("https://pro-api.coinmarketcap.com/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiCoinMarket.class);
//    CMC_PRO_API_KEY: cf1b52a2-e099-4d0a-b165-80b2c242f30f

//    https://pro-api.coinmarketcap.com/
    @GET("v1/cryptocurrency/listings/latest")
    Call<Market> convertMarket(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                               @Query("sort") String sort,
                               @Query("start") int start,
                               @Query("limit") int limit,
                               @Query("cryptocurrency_type") String cryptocurrency_type,
                               @Query("convert") String convert);

    @GET("v1/cryptocurrency/listings/latest")
    Call<Market> convertGainer(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                                  @Query("sort") String sort,
                                  @Query("start") int start,
                                  @Query("limit") int limit,
                                  @Query("cryptocurrency_type") String cryptocurrency_type,
                                  @Query("convert") String convert);

    @GET("v1/cryptocurrency/listings/latest")
    Call<Market> convertLoser(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                                @Query("sort") String sort,
                                @Query("sort_dir") String sort_dir,
                                @Query("start") int start,
                                @Query("limit") int limit,
                                @Query("cryptocurrency_type") String cryptocurrency_type,
                                @Query("convert") String convert);
    @GET("v1/cryptocurrency/listings/latest")
    Call<Market> getCoinNewListing(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                              @Query("sort") String sort,
                              @Query("sort_dir") String sort_dir,
                              @Query("start") int start,
                              @Query("limit") int limit,
                              @Query("cryptocurrency_type") String cryptocurrency_type,
                              @Query("convert") String convert);

    @GET("v2/cryptocurrency/quotes/latest")
    Call<DetailItem> convertDetailMarket(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                                         @Query("id") int id,
                                         @Query("convert") String convert);

    @GET("/v1/exchange/info")
    Call<Exchange> getInforCoin(@Query("CMC_PRO_API_KEY") String CMC_PRO_API_KEY,
                                @Query("id") String id);
}
