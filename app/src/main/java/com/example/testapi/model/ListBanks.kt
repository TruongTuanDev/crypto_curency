package com.example.testapi.model

data class ListBanks(
    val code: String,
    val desc: String,
    val data: List<Banks>
)