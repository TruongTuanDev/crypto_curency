package com.example.testapi.my_interface

import android.view.View
import com.example.testapi.model.DataItem

interface ItemClickListener {
    fun onClick(dataItem: DataItem)
}