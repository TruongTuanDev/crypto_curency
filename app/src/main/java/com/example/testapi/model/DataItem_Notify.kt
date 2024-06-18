package com.example.testapi.model;

class DataItem_Notify {
    var description: String? = null
    var title: String? = null

    constructor()
    constructor(description: String?, title: String?) {
        this.title = title
        this.description = description
    }
}
