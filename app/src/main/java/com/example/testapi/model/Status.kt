package com.example.testapi.model

data class Status(
    val timestamp: String,
    val errorCode: Int,
    val errorMessage: String,
    val elapsed: Int,
    val creditCount: Int,
    val notice: String,
    val totalCount: Int
)
