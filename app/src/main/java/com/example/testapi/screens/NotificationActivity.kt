package com.example.testapi.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.NotifyAdapter
import com.example.testapi.R
import com.example.testapi.databinding.ActivityNotificationBinding
import com.example.testapi.databinding.ItemNotificationBinding
import com.example.testapi.model.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var database : DatabaseReference
    private lateinit var notificationList : ArrayList<Notification>
    private lateinit var notifyAdapter: NotifyAdapter
    private lateinit var notificationRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadData()
        notifyAdapter = NotifyAdapter(notificationList)
        notificationRecyclerView = binding.notificationRecycleView
        setupRecyclerView(notificationRecyclerView,notifyAdapter)

    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }
    fun loadData(){
        database = FirebaseDatabase.getInstance().getReference("Notification")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (dataSnapshot : DataSnapshot in snapshot.children){
                    val notification : Notification? = dataSnapshot.getValue(Notification::class.java)
                    if (notification != null) {
                        notificationList.add(notification)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}