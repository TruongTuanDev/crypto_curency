package com.example.testapi.model

import com.google.firebase.database.DataSnapshot

class GetWatchList(snapshot: DataSnapshot) {

    var watchlist: HashMap<String, Boolean>? = null

    init {
        val hashMap = snapshot.value as? HashMap<*, *>
        watchlist = hashMap?.get("watchlist") as? HashMap<String, Boolean>
    }

    fun getWatchList(): List<String> {
        return watchlist?.keys?.toList() ?: emptyList()
    }
}