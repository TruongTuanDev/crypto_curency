package com.example.testapi.model

import com.google.firebase.database.DataSnapshot

data class Watchlist(
     var image : Int,
     var star : Int,
     var name : String,
    var sign_name : String,
    var price : String,
    var change : String,
    var image_change : Int,
    var chart : Int
)
