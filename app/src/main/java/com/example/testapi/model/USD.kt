package com.example.testapi.model;

data class USD (
    val price: Double,
    val volume_24h: Double,
    val volume_change_24h: Double,
    val percent_change_1h: Double,
    val percent_change_24h: Double,
    val percent_change_7d: Double,
    val percent_change_30d: Double,
    val market_cap: Double,
    val market_cap_dominance: Double,
    val fully_diluted_market_cap: Double,
    val last_updated: String
)