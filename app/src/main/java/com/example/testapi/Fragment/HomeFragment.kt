package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.Adapter.NewCoinApdater
import com.example.testapi.Adapter.TopCoinAdapter
import com.example.testapi.Adapter.TopGainerAdapter
import com.example.testapi.Adapter.TopLoserAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentHomeBinding
import com.example.testapi.model.DataItem
import com.example.testapi.model.Market
import com.example.testapi.model.Wallet
import com.example.testapi.my_interface.ItemClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.http.HTTP_GONE
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var database : DatabaseReference
    private lateinit var sessionManager: SessionManager


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var topCoinAdapter: TopCoinAdapter
    private lateinit var topGainerAdapter: TopGainerAdapter
    private lateinit var topLoserAdapter: TopLoserAdapter
    private lateinit var newCoinApdater: NewCoinApdater

    private var topCoinList = listOf<DataItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("Accounts")
        val phone = sessionManager.getPhoneInstall()
        val countNotify = phone?.let { database.child(it).child("countNotify") }
        countNotify?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val countNotify = dataSnapshot.getValue(Long::class.java)
                if (countNotify != null) {
                    if (countNotify < 1){
                        binding.textCountNotify.visibility = View.GONE
                    }
                        binding.textCountNotify.text = countNotify.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

        binding.btnNotification.setOnClickListener {
            val fragment = NotificationUserFragment()
            val bundle = Bundle()
            bundle.putString("phone", phone)
            fragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.body_container, fragment)?.commit()
            countNotify?.setValue(0)
        }
        topCoinAdapter = TopCoinAdapter(topCoinList, object : ItemClickListener{
            override fun onClick(dataItem: DataItem) {
                onClickToDetail(dataItem)
            }

            override fun onClickToSell(wallet: Wallet) {

            }
        })
        topGainerAdapter = TopGainerAdapter(topCoinList, object : ItemClickListener{
            override fun onClick(dataItem: DataItem) {
                onClickToDetail(dataItem)
            }

            override fun onClickToSell(wallet: Wallet) {

            }

        })
        topLoserAdapter = TopLoserAdapter(topCoinList, object : ItemClickListener{
            override fun onClick(dataItem: DataItem) {
                onClickToDetail(dataItem)
            }

            override fun onClickToSell(wallet: Wallet) {

            }

        })
        newCoinApdater = NewCoinApdater(topCoinList, object : ItemClickListener{
            override fun onClick(dataItem: DataItem) {
                onClickToDetail(dataItem)
            }

            override fun onClickToSell(wallet: Wallet) {

            }
        })

        setupRecyclerView(binding.topCoinRecyclerView, topCoinAdapter)
        setupRecyclerView(binding.topGainerRecyclerView, topGainerAdapter)
        setupRecyclerView(binding.topLoserRecyclerView, topLoserAdapter)
        setupRecyclerView(binding.newCoinRecyclerView, newCoinApdater)

        loadCoinTopList()
        loadCoinGainerList()
        loadCoinLoserList()
        loadNewCoinList()
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }
    private fun loadCoinData(
        apiCall: Call<Market?>,
        onResponse: (ArrayList<DataItem>) -> Unit,
        onFailure: () -> Unit
    ) {
        apiCall.enqueue(object : retrofit2.Callback<Market?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Market?>, response: Response<Market?>) {
                val market: Market? = response.body()
                if (market == null) {
                    Log.e("Tag", "market null")
                    onFailure()
                } else {
                    val dataItems = ArrayList<DataItem>()
                    dataItems.addAll(market.data)
                    Log.e("Dữ liệu chung", dataItems.toString())
                    onResponse(dataItems)
                }
            }

            override fun onFailure(call: Call<Market?>, t: Throwable) {
                onFailure()
            }
        })
    }

    private fun loadCoinTopList() {
        val apiCall = ApiCoinMarket.apiCoinMarket.convertMarket(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "market_cap",
            1,
            10,
            "all",
            "USD"
        )
        loadCoinData(apiCall,
            onResponse = { dataItems ->
                topCoinList = dataItems
                topCoinAdapter.updateData(topCoinList)
            },
            onFailure = {
                Log.e("API_COIN_TOP","Lấy API thất bại")
            }
        )
    }
    private fun loadCoinGainerList() {
        val apiCall = ApiCoinMarket.apiCoinMarket.convertGainer(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "percent_change_24h",
            1,
            10,
            "all",
            "USD"
        )
        loadCoinData(apiCall,
            onResponse = { dataItems ->
                topCoinList = dataItems
                topGainerAdapter.updateData(topCoinList)
            },
            onFailure = {
                Log.e("API_COIN_TOP","Lấy API thất bại")
            }
        )
    }
    private fun loadCoinLoserList() {
        val apiCall = ApiCoinMarket.apiCoinMarket.convertLoser(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "percent_change_24h",
            "asc",
            1,
            10,
            "all",
            "USD"
        )
        loadCoinData(apiCall,
            onResponse = { dataItems ->
                topCoinList = dataItems
                topLoserAdapter.updateData(topCoinList)
            },
            onFailure = {
                Log.e("API_COIN_TOP","Lấy API thất bại")
            }
        )
    }
    private fun loadNewCoinList() {
        val apiCall = ApiCoinMarket.apiCoinMarket.getCoinNewListing(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "date_added",
            "desc",
            1,
            10,
            "all",
            "USD"
        )
        loadCoinData(apiCall,
            onResponse = { dataItems ->
                topCoinList = dataItems
                newCoinApdater.updateData(topCoinList)
            },
            onFailure = {
                Log.e("API_COIN_TOP","Lấy API thất bại")
            }
        )
    }
    private fun onClickToDetail(dataItem : DataItem){
        val bundle = Bundle()
        bundle.putSerializable("object_coin", dataItem)
        val detailFragment = DetailFragment()
        detailFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.body_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
    }

