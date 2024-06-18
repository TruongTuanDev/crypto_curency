package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemListUserBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickItem


class AccountAdapter(private var lDataItems: List<Account>) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() ,Filterable {
    private lateinit var itemClick :  IClickItem
    private  var lDataItemsOld: List<Account> = lDataItems

    constructor(accountList: List<Account>, clickListener: IClickItem) : this(accountList) {
        this.itemClick = clickListener
    }

    inner class AccountViewHolder(binding: ItemListUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val phone : TextView = binding.phone
        val fullname : TextView = binding.fullname
        val imgDelete : ImageView = binding.imgViewDelete
        val imgRule : ImageView = binding.imgViewSetRule
        fun bind(dataItem: Account) {
            phone.text = dataItem.phone
            fullname.text = dataItem.name
            imgDelete.setOnClickListener { itemClick.onClickItemDelete(phone.text.toString()) }
            imgRule.setOnClickListener { itemClick.onClickItemSetRule(phone.text.toString()) }

        }

    }
    override fun getItemCount(): Int {
        return lDataItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = ItemListUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccountViewHolder(view)
    }


    override fun onBindViewHolder(holder : AccountViewHolder, position: Int) {
        val dataItem  = lDataItems[position]
        holder.bind(dataItem)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<Account>) {
        lDataItems = newDataList
        notifyDataSetChanged()
    }
    override fun getFilter() : Filter{
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
               val search : String = charSequence.toString()
                if (search.isEmpty()){
                    lDataItems = lDataItemsOld
                }else{
                    val listAccounts: MutableList<Account>  = ArrayList<Account>()
                    for (account : Account in lDataItemsOld ){
                        if(account.name?.toLowerCase()?.contains(search.toLowerCase()) == true ||
                            account.phone?.contains(search) == true
                        ){
                            listAccounts.add(account)
                        }
                    }
                    lDataItems = listAccounts
                }
                 var filterResult : FilterResults = FilterResults()
                 filterResult.values = lDataItems
                return filterResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    lDataItems = results.values as List<Account>
                    notifyDataSetChanged()
                }
            }

        }
    }

}