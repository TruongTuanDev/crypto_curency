package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemListAdminBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickItem
import com.google.firebase.database.DatabaseReference
import java.util.Locale

class AccountAdminAdapter(private var accountLists : ArrayList<Account>, private val iClickItem: IClickItem) : RecyclerView.Adapter<AccountAdminAdapter.AccountAdminHolder>(), Filterable {
    private  var accountListOld : ArrayList<Account> = accountLists
    private lateinit var database : DatabaseReference

    inner class AccountAdminHolder(binding : ItemListAdminBinding) : RecyclerView.ViewHolder(binding.root){
        val phoneNumber = binding.phone
        val fullName = binding.fullname
        val IcDelete = binding.deleteRuleAdmin
        fun bind(data : Account){
            phoneNumber.text = data.phone
            fullName.text = data.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AccountAdminHolder {
        val view = ItemListAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccountAdminHolder(view)
    }

    override fun getItemCount(): Int {
        return accountLists.size
    }

    override fun onBindViewHolder(holder: AccountAdminHolder, position: Int) {
        val accounts = accountLists[position]
        val name = accounts.name
        val phone = accounts.phone
        holder.fullName.text = name
        holder.phoneNumber.text = phone
        holder.IcDelete.setOnClickListener{
            if (phone != null) {
                iClickItem.onClickItemSetRule(phone)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val strSearch: String = constraint.toString()
                if (strSearch.isEmpty()) {
                    accountLists = accountListOld
                } else {
                    val list  = ArrayList<Account>()
                    for (account : Account in accountListOld) {
                        if (account.name?.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true
                            || account.phone?.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true
                            )
                       {
                            list.add(account)
                        }
                    }
                    accountLists = list
                }
                val filterResults = FilterResults()
                filterResults.values = accountLists
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                accountLists = results?.values as ArrayList<Account>
                notifyDataSetChanged()
            }

        }
    }
}