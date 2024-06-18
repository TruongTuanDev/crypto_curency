package com.example.testapi.model

data class DiscoverModel (
    val Type: Int,
    val Message: String,
    val Promoted: List<Any>,
    val Data: List<NewsModel>
)

