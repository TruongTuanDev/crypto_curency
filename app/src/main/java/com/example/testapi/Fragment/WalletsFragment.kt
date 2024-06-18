package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.MarketAdapter
import com.example.testapi.Adapter.WalletAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentWalletsBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.Wallet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalletsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalletsFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentWalletsBinding
    private var tViewBalance: TextView? = null
    private lateinit var walletsAdapter: WalletAdapter
    private lateinit var lCoinMarkets : ArrayList<Wallet>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletsBinding.inflate(inflater, container, false)
        val btnDeposit = binding.btnDeposit
        lCoinMarkets = ArrayList()


        loadData()
        getListPhoneBooksRealtimeDB()

        btnDeposit.setOnClickListener {
            val fragment = DepositFragment()
            Log.e("Có tiền ko mà mua", "22222")
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.add(R.id.walletsFragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletsAdapter = WalletAdapter(lCoinMarkets)
        setupRecyclerView(binding.recyclerViewCrypto,walletsAdapter)
    }
    fun formatNumber(number: Double): String {
        val formatter = NumberFormat.getInstance(Locale.US)
        return formatter.format(number)
    }
    private fun getListPhoneBooksRealtimeDB() {
        val phone = sessionManager.getPhoneInstall()
        database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it).child("wallets") }!!
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                lCoinMarkets.clear()
                for (dataSnapshot in snapshot.children) {
                    val wallet = dataSnapshot.getValue(Wallet::class.java)
                    Log.e("Bạn đã mua",wallet.toString())
                        lCoinMarkets.add(wallet!!)
                }
                walletsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun loadData() {
        val phone: String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            Log.e("Số điện thoại hiện tại ", phone)
            database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone).child("balance")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balance = snapshot.getValue(Double::class.java)
                    Log.e("Tiền của mày", balance.toString())
                    binding.textBalanceAmount.text = balance?.let { formatNumber(it) }

                    binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        @SuppressLint("SetTextI18n")
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val currency = "%.3f".format(balance)
                            val selectedCurrency = parent?.getItemAtPosition(position).toString()
                            if (selectedCurrency == "USD" && balance != null) {
                                val convertedBalance = balance / 25000
                                binding.textBalanceAmount.text = "%.3f".format(convertedBalance)
                                Log.e("Sau ", "$ $convertedBalance")
                            } else if (selectedCurrency == "VND" && balance != null) {

                                binding.textBalanceAmount.text = currency.toDouble().let { formatNumber(it) }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            Log.e("Số điện thoại hiện tại ", "Không có đâu")
        }
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WalletsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WalletsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
