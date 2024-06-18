package com.example.testapi.my_interface

import android.view.View
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.Wallet

interface ItemClickListener {
    fun onClick(dataItem: DataItem)
    fun onClickToSell(wallet: Wallet)
}