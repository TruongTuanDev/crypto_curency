package com.example.testapi

import SessionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.testapi.Fragment.DetailFragment
import com.example.testapi.Fragment.HomeFragment
import com.example.testapi.Fragment.MarketFragment
import com.example.testapi.Fragment.NewsFragment
import com.example.testapi.Fragment.ProfileFragment
import com.example.testapi.Fragment.ViewAccountFragment
import com.example.testapi.Fragment.WatchListFragment
import com.example.testapi.databinding.ActivityMainBinding
import com.example.testapi.my_interface.ITransmitData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), ITransmitData {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationView: BottomNavigationView
    private lateinit var sessionManager: SessionManager

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Main","vào rồi đó")
        binding = ActivityMainBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.body_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navigationView = binding.bottomBar
        supportFragmentManager.beginTransaction().replace(binding.bodyContainer.id, HomeFragment()).commit()
        navigationView.selectedItemId = R.id.home11
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        val phone : String? = sessionManager.getPhoneInstall()
        val phoneNumberRef = phone?.let { database.child(it) }
        Log.e("Accounts lấy từ firebase", phoneNumberRef.toString())
        if (phone != null) {
            database.child(phone).child("isOnline").setValue(true)
        }

        var fragment : Fragment? = null
        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home11 -> {
                    fragment = HomeFragment()
                    true
                }
                R.id.market11 -> {
                    fragment = MarketFragment()
                    true
                }

                R.id.watchlist11 -> {
                    fragment = WatchListFragment()
                    true
                }
                R.id.newspaper11 -> {
                    fragment = NewsFragment()
                    true
                }
                R.id.account11 -> {
                    fragment = ViewAccountFragment()
                    true
                }
                else -> false
            }
            fragment?.let {
                supportFragmentManager.beginTransaction().replace(binding.bodyContainer.id, it).commit()
                true
            } ?: false
            }
        }

    override fun onPause() {
        super.onPause()
    }

    override fun senData(id: String) {
        val detailFragment : Fragment = DetailFragment()
        val bundle : Bundle = Bundle()
        bundle.putString("id", id)
        detailFragment.arguments = bundle
        val fragmentTransaction :  FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.body_container,detailFragment).commit()

    }




}
