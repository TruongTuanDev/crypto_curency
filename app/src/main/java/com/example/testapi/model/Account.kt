package com.example.testapi.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import java.io.Serializable

data class Account(
    var userID: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var date: String? = null,
    var pass: String? = null,
    var country: String? = null,
    var watchlist: HashMap<String, Boolean>? = null,
    var sex: String? = null,
    var rule: String? = null,
    var notify: Int? = null,
    var isOnline: Boolean? = null
) : Serializable {
    constructor(snapshot: DataSnapshot) : this(
        userID = snapshot.key ?: "",
        name = snapshot.child("name").getValue(String::class.java),
        phone = snapshot.child("phone").getValue(String::class.java),
        email = snapshot.child("email").getValue(String::class.java),
        date = snapshot.child("date").getValue(String::class.java),
        pass = snapshot.child("pass").getValue(String::class.java),
        country = snapshot.child("country").getValue(String::class.java),
        watchlist = snapshot.child("watchlist").getValue(object : GenericTypeIndicator<HashMap<String, Boolean>>() {}),
        sex = snapshot.child("sex").getValue(String::class.java),
        rule = snapshot.child("rule").getValue(String::class.java),
        notify = snapshot.child("notify").getValue(Int::class.java),
        isOnline = snapshot.child("isOnline").getValue(Boolean::class.java)
    )

    constructor(
        userID: String, name: String, phone: String, email: String, date: String, pass: String,
        country: String, sex: String, rule: String, notify: Int, isOnline: Boolean
    ) : this(
        userID = userID,
        name = name,
        phone = phone,
        email = email,
        date = date,
        pass = pass,
        country = country,
        watchlist = null,
        sex = sex,
        rule = rule,
        notify = notify,
        isOnline = isOnline
    )
}
