package com.example.testapi.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.HomeAdminAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentHomeAdminBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickShowProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeAdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeAdminFragment : Fragment() {
    private var _binding : FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseReference
    private lateinit var recyclerViewAccount : RecyclerView
    private lateinit var editTextSearch : EditText
    private lateinit var textViewTotal : TextView
    private lateinit var homeAdminAdapter : HomeAdminAdapter
    private lateinit var accountList : ArrayList<Account>

    // TODO: Rename and change types of parameters
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
        _binding = FragmentHomeAdminBinding.inflate(inflater,container,false)
        recyclerViewAccount = binding.recyclerViewAccount
        editTextSearch = binding.editSearch
        textViewTotal = binding.textViewTotalOnline
        accountList = ArrayList()
        homeAdminAdapter = HomeAdminAdapter(accountList, object : IClickShowProfile{
            override fun onClickShowProfile(phone: String?) {
                if (phone != null) {
                    openProfileDialog(Gravity.CENTER,phone)
                }
            }

        })

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerViewAccount.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recyclerViewAccount.addItemDecoration(dividerItemDecoration)
        recyclerViewAccount.adapter = homeAdminAdapter
        event()

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeAdminFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeAdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getListPhoneBooksRealtimeDB() {
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                accountList.clear()
                for (dataSnapshot in snapshot.children) {
                    val getPhoneonline = snapshot.child(dataSnapshot.key!!).child("isOnline").getValue(Boolean::class.java)
                    Log.e("Dữ liệu từ firebase ", dataSnapshot.toString())

//                    Log.e("Test thôi mà",getPhoneonline.toString())
                    try {
                        val account = Account(
                            userID = dataSnapshot.child("userID").getValue(String::class.java),
                            name = dataSnapshot.child("name").getValue(String::class.java),
                            phone = dataSnapshot.child("phone").getValue(String::class.java),
                            email = dataSnapshot.child("email").getValue(String::class.java),
                            date = dataSnapshot.child("date").getValue(String::class.java),
                            pass = dataSnapshot.child("pass").getValue(String::class.java),
                            country = dataSnapshot.child("country").getValue(String::class.java),
                            sex = dataSnapshot.child("sex").getValue(String::class.java),
                            rule = dataSnapshot.child("rule").getValue(String::class.java),
                            notify = dataSnapshot.child("notify").getValue(Int::class.java),
                            isOnline = dataSnapshot.child("isOnline").getValue(Boolean::class.java)
                        )

                        Log.e("Dữ liệu sau khi thông qua Account",account.toString())
                        accountList.add(account)
                    } catch (e: DatabaseException) {
                        Log.e("Firebase", "Error converting to Account: ${dataSnapshot.value}", e)
                    }
                }
                homeAdminAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
            }
        })
    }

    private fun getTotalOnline() {
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                var totaloff = 0
                var totalon = 0
                for (dataSnapshot in snapshot.children) {
                    Log.e("Tau ngán lắm", dataSnapshot.toString())
                    val getPhoneonline = snapshot.child(dataSnapshot.key!!).child("isOnline").getValue(Boolean::class.java)
                    when (getPhoneonline) {
                        true -> {
                            totalon++
                        }
                        false -> {
                            totaloff++
                        }
                        else -> {
                            return
                        }
                    }
                }
                val total = totaloff + totalon
                textViewTotal.setText("Online $totalon/$total")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun openProfileDialog(gravity: Int, phone: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_profile)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        windowAttributes.gravity = gravity
        window.setAttributes(windowAttributes)
        if (Gravity.CENTER == gravity) {
            dialog.setCancelable(false)
        } else {
            dialog.setCancelable(true)
        }
        val textViewName = dialog.findViewById<TextView>(R.id.txtNameDialog)
        val textViewEmail = dialog.findViewById<TextView>(R.id.txtEmailDialog)
        val textViewPhone = dialog.findViewById<TextView>(R.id.txtPhoneDialog)
        val textViewBirth = dialog.findViewById<TextView>(R.id.txtBirthDayDialog)
        val textViewGender = dialog.findViewById<TextView>(R.id.txtGenderDialog)
        val countryDialog = dialog.findViewById<CountryCodePicker>(R.id.ccpCountryDialog)
        val btnClose = dialog.findViewById<Button>(R.id.btnCloseDialog)
        database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                textViewName.text = account?.name
                textViewEmail.text = account?.email
                textViewPhone.text = account?.phone
                textViewBirth.text = account?.date
                textViewGender.text = account?.sex
                account?.country?.toInt()?.let { countryDialog.setCountryForPhoneCode(it) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    fun event(){
        getListPhoneBooksRealtimeDB()
        getTotalOnline()
        editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                homeAdminAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}