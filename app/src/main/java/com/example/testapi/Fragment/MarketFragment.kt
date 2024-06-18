package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.Adapter.MarketAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentMarketBinding
import com.example.testapi.model.DataItem
import com.example.testapi.model.Market
import com.example.testapi.my_interface.ItemClickListener
import retrofit2.Call
import retrofit2.Response


class MarketFragment : Fragment() {

    private lateinit var _binding: FragmentMarketBinding
    private val binding get() = _binding
    private lateinit var sessionManager: SessionManager
    private lateinit var marketAdapter: MarketAdapter
    private lateinit var lCoinMarkets : ArrayList<DataItem>

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        lCoinMarkets = ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        marketAdapter = MarketAdapter(requireContext(), lCoinMarkets, object : ItemClickListener {
            override fun onClick(dataItem: DataItem) {
                onClickToDetail(dataItem)
            }
        })
        setupRecyclerView(binding.rcvMarket, marketAdapter)
        loadCoinTopList()
        // Sorting buttons setup
        setupSortingButtons()

        binding.editSearchMarket.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                marketAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupSortingButtons() {
        binding.btnArrangeName.setOnClickListener {
            lCoinMarkets.sortBy { it.name}
            lCoinMarkets.reverse()
            marketAdapter.notifyDataSetChanged()
        }
        binding.btnArrangePrice.setOnClickListener {
            lCoinMarkets.sortBy { it.quote.usd.price }
            lCoinMarkets.reverse()
            marketAdapter.notifyDataSetChanged()
        }
        binding.btnArrangePercent24h.setOnClickListener {
            lCoinMarkets.sortBy { it.quote.usd.percent_change_24h }
            lCoinMarkets.reverse()
            marketAdapter.notifyDataSetChanged()
        }
        binding.btnArrangePercent7D.setOnClickListener {
            lCoinMarkets.sortBy { it.quote.usd.percent_change_7d }
            lCoinMarkets.reverse()
            marketAdapter.notifyDataSetChanged()
        }
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
        ApiCoinMarket.apiCoinMarket.convertMarket(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "market_cap", 1, 20, "tokens", "USD"
        ).enqueue(object : retrofit2.Callback<Market?> {
            override fun onResponse(call: Call<Market?>, response: Response<Market?>) {
                val market: Market? = response.body()
                if (market != null) {
                    dataItems.addAll(market.data)
                }
                lCoinMarkets = dataItems
                marketAdapter.updateData(lCoinMarkets)
            }

            override fun onFailure(call: Call<Market?>, t: Throwable) {
                Toast.makeText(requireActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onClickToDetail(dataItem: DataItem) {
        sessionManager.setKeyIDCheckDetail(1)
        val bundle = Bundle()
        bundle.putSerializable("object_coin", dataItem)
        val detailFragment = DetailFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.body_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }




}
