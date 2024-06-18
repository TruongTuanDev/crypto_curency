package com.example.testapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.AccountAdapter
import com.example.testapi.databinding.ActivityAdminBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var database : DatabaseReference
    private var accountList = mutableListOf<Account>()
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        event()
        mapping()
    }
    private fun getListPhoneBooksRealtimeDB(){
        accountAdapter = AccountAdapter(accountList)
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        database.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                accountList.clear()
                for (accountSnapshot in snapshot.children){
                    val account = accountSnapshot.getValue(Account::class.java)
                    if (account != null) {
                        accountList.add(account)
                    }
                }
                accountAdapter.notifyDataSetChanged()
                Log.e("Người dùng", accountList.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@Admin,"Lấy dữ liệu danh sách thất bại!", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun event(){
        getListPhoneBooksRealtimeDB()
        binding.editSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                (this@Admin).accountAdapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@Admin, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }
    fun mapping(){
        accountAdapter = AccountAdapter(accountList, object : IClickItem{
            override fun onClickItemSetRule(phone: String) {
            }

            override fun onClickItemDelete(phone: String) {

            }

        })
        setupRecyclerView(binding.recyclerViewAccount,accountAdapter)

    }
}