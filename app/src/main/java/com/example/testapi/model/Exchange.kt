package com.example.testapi.model

data class Exchange(
    val id: Int,
    val name: String,
    val slug: String,
    val logo: String,
    val description: String,
    val dateLaunched: String,
    val notice: String?,
    val countries: List<String>,
    val fiats: List<String>,
    val tags: List<String>?,
    val type: String,
    val makerFee: Double,
    val takerFee: Double,
    val weeklyVisits: Int,
    val spotVolumeUsd: Double,
    val spotVolumeLastUpdated: String,
    val urls: Url
)