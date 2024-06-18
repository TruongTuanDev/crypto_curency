package com.example.testapi.screens

import SessionManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.testapi.Fragment.FeedbackFragment
import com.example.testapi.Fragment.GoUserFragment
import com.example.testapi.Fragment.NotificationFragment
import com.example.testapi.Fragment.ViewHomeAdminFragment
import com.example.testapi.R
import com.example.testapi.databinding.ActivityMainAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainAdminActivity : AppCompatActivity() {
    private lateinit var navigationView : BottomNavigationView
    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivityMainAdminBinding
    private lateinit var sessionManager: SessionManager
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        navigationView = binding.bottomAdminBar
        supportFragmentManager.beginTransaction().replace(R.id.body_admin_container, ViewHomeAdminFragment()).commit()
        navigationView.selectedItemId = R.id.homeadmin11

        val phone = sessionManager.getPhoneInstall()
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        if (phone != null) {
            database.child(phone).child("isOnline").setValue(true)
        }

        navigationView.setOnNavigationItemSelectedListener (object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var fragment : Fragment? = null
                when (item.itemId) {
                    R.id.homeadmin11 -> fragment = ViewHomeAdminFragment()
                    R.id.feedback -> fragment = FeedbackFragment()
                    R.id.notification -> fragment = NotificationFragment()
                    R.id.accountadmin11 -> fragment = GoUserFragment()
                }
                supportFragmentManager.beginTransaction().replace(
                    R.id.body_admin_container,
                    fragment!!
                ).commit()
                return true
            }

        })
    }

    override fun onRestart() {
        super.onRestart()
        val phone = sessionManager.getPhoneInstall()
        if (phone != null) {
            if (phone.isNotEmpty()){
                database = FirebaseDatabase.getInstance().getReference("Accounts")
                database.child(phone).child("isOnline").setValue(true)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val phone = sessionManager.getPhoneInstall()
        if (phone != null) {
            if (phone.isNotEmpty()){
                database = FirebaseDatabase.getInstance().getReference("Accounts")
                database.child(phone).child("isOnline").setValue(false)
            }
        }
    }
}