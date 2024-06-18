package com.example.testapi.model;

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("USD")
    val usd: USD,
)


