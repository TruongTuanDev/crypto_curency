package com.example.testapi.model

import java.io.Serializable

data class DataItem (
    val id: Int,
    val name: String,
    val symbol: String,
    val slug: String,
    val num_market_pairs: Int,
    val date_added: String,
    val max_supply: Double,
    val circulating_supply: Double,
    val total_supply: Double,
    val cmc_rank: Int,
    val self_reported_circulating_supply: Double,
    val self_reported_market_cap: Double,
    val last_updated: String,
    val quote: Quote,
//    @SerializedName("quote")
//    val quote: Map<String, CurrencyInfo>
): Serializable
