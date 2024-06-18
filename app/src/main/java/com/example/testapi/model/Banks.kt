package com.example.testapi.model

data class Banks(
    val id: Int,
    val name: String,
    val code: String,
    val bin: String,
    val shortName: String,
    val logo: String,
    val transferSupported: Int,
    val lookupSupported: Int,
    val short_name: String,
    val support: Int,
    val isTransfer: Int,
    val swift_code: String
)