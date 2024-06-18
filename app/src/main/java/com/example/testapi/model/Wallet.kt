package com.example.testapi.model

data class Wallet(
    val id : String = "",
    val name: String = "",
    val symbol: String = "",
    val price: Double = 0.0,
    val quantity: Double? = null,
    val quantity_curency:  Double? = null,
    val buy_date: String = "",

    )