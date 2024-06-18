package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.media.session.MediaSessionManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.testapi.API.ApiBanks
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.BanksAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentDepositBinding
import com.example.testapi.model.Banks
import com.example.testapi.model.DataItem
import com.example.testapi.model.DiscoverModel
import com.example.testapi.model.ListBanks
import com.example.testapi.model.Market
import com.example.testapi.model.NewsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DepositFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DepositFragment : Fragment() {
    private lateinit var binding : FragmentDepositBinding
    private lateinit var banksAdapter: BanksAdapter
    private lateinit var database : DatabaseReference
    private lateinit var sessionManager: SessionManager
    private var listBanks = listOf<Banks>()
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositBinding.inflate(layoutInflater)
        sessionManager = SessionManager(requireContext())
        val phone = sessionManager.getPhoneInstall()
        val selectedBanks = binding.spinnerPaymentMethod
        val btnDeposit = binding.btnAcceptDesposit
        val editQuantityDeposit = binding.editQuantityDeposit
        btnDeposit.setOnClickListener{
            val depositAmount = editQuantityDeposit.text.toString().toDouble()
            database = FirebaseDatabase.getInstance().getReference("Accounts")
            val accountRef = phone?.let { it1 -> database.child(it1) }
            if (accountRef != null) {
                accountRef.child("balance").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val currencyBalance = dataSnapshot.getValue(Double::class.java)?: 0.0
                        val newBalance = currencyBalance + depositAmount
                        accountRef.child("balance").setValue(newBalance).addOnSuccessListener {
                            Toast.makeText(requireActivity(), "Chúc mừng bạn nạp tiền thành công", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener{
                                    exception ->
                                Log.e("Lỗi nạp tiền", "Error updating balance: $exception")
                            }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }

                })
            }

        }
        getListBanks()
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DepositFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DepositFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getListBanks() {
        ApiBanks.apiBanks.listBanksVN.enqueue(object : Callback<ListBanks> {
            override fun onResponse(call: Call<ListBanks>,
                                    response: Response<ListBanks>) {
                if (response.isSuccessful) {
                    listBanks = response.body()?.data ?: listOf()
                    setupSpinner()
                } else {
                    Log.e("MainActivity", "API call failed with response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ListBanks>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
            }
        })
    }
    private fun setupSpinner() {
        banksAdapter = BanksAdapter(requireContext(), listBanks)
        binding.spinnerPaymentMethod.adapter = banksAdapter
    }

}