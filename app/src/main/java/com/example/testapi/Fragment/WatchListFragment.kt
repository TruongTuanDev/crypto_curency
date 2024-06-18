package com.example.testapi.Fragment

import SessionManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.Adapter.WatchListAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentWatchListBinding
import com.example.testapi.model.DataItem
import com.example.testapi.model.GetWatchList
import com.example.testapi.model.Market
import com.example.testapi.my_interface.ItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Response


class WatchListFragment : Fragment() {
    private var _binding : FragmentWatchListBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private lateinit var database : DatabaseReference

    private lateinit var watchListAdapter: WatchListAdapter

    private lateinit var myList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        val phone : String? = sessionManager.getPhoneInstall()
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        if (phone != null) {
            database.child(phone).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val getWatchList = GetWatchList(dataSnapshot)
                    myList = getWatchList.getWatchList()
                    Log.e("Mày cũng tìm", myList.toString())
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View {
        _binding = FragmentWatchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCoinTopList()
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }


    private fun loadCoinTopList() {
        val dataItems = ArrayList<DataItem>()
        val phone = sessionManager.getPhoneInstall()
        ApiCoinMarket.apiCoinMarket.convertMarket(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "market_cap",
            1,
            100,
            "all",
            "USD"
        ).enqueue(object : retrofit2.Callback<Market?> {
            override fun onResponse(call: Call<Market?>, response: Response<Market?>) {
                val market : Market? = response.body()
                if (market == null) {
                    Log.e("Tag","market null")
                }
                if (market != null) {
                    for (i in 0..< market.data.size) {
                            dataItems.add(market.data[i])
                    }
                }
//                val watchlistIds  = phone?.let { getWatchlist(it) }
//                Log.e("Ko mua bày đặt xem",watchlistIds.toString())
                val myShowList = getCoinsFromWatchlist(dataItems,myList)
                val mutableList = myShowList.toMutableList()
                Log.e("Ko mua bày đặt xem ok",dataItems.toString())
                Log.e("Ko mua bày đặt xem ok",myList.toString())
                Log.e("Ko mua bày đặt xem ok",myShowList.toString())
                watchListAdapter = WatchListAdapter(requireContext(),mutableList,object : ItemClickListener{
                    override fun onClick(dataItem: DataItem) {
                        onClickToDetail(dataItem)
                    }

                })
                setupRecyclerView(binding.watchListRecyclerView,watchListAdapter)
            }
            override fun onFailure(p0: Call<Market?>, t: Throwable) {
                Toast.makeText(requireActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show()
            }
        })

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun getCoinsFromWatchlist(coins: List<DataItem>, watchlistIds: List<String>): List<DataItem> {
        return coins.filter { watchlistIds.contains(it.symbol) }
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
}