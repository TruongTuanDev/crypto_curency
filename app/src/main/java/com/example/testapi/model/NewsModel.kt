package com.example.testapi.model

import java.util.Objects
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class NewsModel(
    val id: String,
    val guid: String,
    val published_on: Long,
    val imageurl: String,
    val title: String,
    val url: String,
    val body: String,
    val tags: String,
    val lang: String,
    val upvotes: String,
    val downvotes: String,
    val categories: String,
    val source_info: SourceInfo,
    val source: String
)
