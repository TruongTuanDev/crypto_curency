package com.example.testapi.model;

class DetailItem(status: Status, data: HashMap<String, DataItem>) {
    lateinit var data: HashMap<String, DataItem>
    lateinit var status: Status

    class Status {
        var timestamp: String? = null
        var error_code = 0
        var error_message: String? = null
        var elapsed = 0
        var credit_count = 0
    }
}
