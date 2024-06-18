package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.compose.ui.text.toLowerCase
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.R
import com.example.testapi.databinding.ItemHomeAdminBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickShowProfile
import java.util.Locale

class HomeAdminAdapter(private var accountList : List<Account>,private var iClickShowProfile : IClickShowProfile) : RecyclerView.Adapter<HomeAdminAdapter.HomeAdminHolder>(), Filterable {
    private var accountListOld: List<Account> = accountList

    inner class HomeAdminHolder(binding: ItemHomeAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        val fullname  = binding.fullname
        val phoneNumber   = binding.phone
        val checkImg = binding.check
        val imgViewSetRule = binding.imgViewSetRule
        fun bind(dataItem : Account){
            fullname.text = dataItem.name
            phoneNumber.text = dataItem.phone
        }

    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType: Int): HomeAdminHolder {
        val view = ItemHomeAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeAdminHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: HomeAdminAdapter.HomeAdminHolder, postion: Int) {
        val data : Account = accountList[postion]
        val name = data.name
        val phone = data.phone
        val check = data.isOnline
        val rule = data.rule
        if (check  == true){
            holder.checkImg.setImageResource(R.drawable.ic_check)
        }else if(check  == false){
            holder.checkImg.setImageResource(R.drawable.ic_check_no)
        }

        holder.fullname.text = name
        holder.phoneNumber.text = phone
        holder.fullname.setOnClickListener {
            iClickShowProfile.onClickShowProfile(phone)
        }
        if (rule.equals("user")){
            holder.imgViewSetRule.setImageResource(R.drawable.ic_check_no)
        }else if (rule.equals("admin")){
            holder.imgViewSetRule.setImageResource(R.drawable.ic_admin)
        }
        holder.bind(data)

    }

    override fun getItemCount(): Int {
        return accountList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<Account>) {
        accountList = newDataList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val strSearch = constraint.toString()
                if (strSearch.isEmpty()){
                    accountList = accountListOld
                }else{
                    val list  = ArrayList<Account>()
                    for (account in accountListOld) {
                        if (account.name?.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true ||
                            account.phone?.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true
                        ) {
                            list.add(account)
                        }
                    }
                    accountList = list
                }
                val filterResults = FilterResults()
                filterResults.values = accountList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                accountList = results?.values as List<Account>
                notifyDataSetChanged()
            }

        }
    }


}