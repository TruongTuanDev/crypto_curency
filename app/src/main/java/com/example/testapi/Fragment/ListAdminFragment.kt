package com.example.testapi.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.AccountAdminAdapter
import com.example.testapi.databinding.FragmentListAdminBinding
import com.example.testapi.model.Account
import com.example.testapi.my_interface.IClickItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListAdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListAdminFragment : Fragment() {
    private lateinit var binding : FragmentListAdminBinding
    private lateinit var recyclerViewAccount : RecyclerView;
    private lateinit var editTextSearch : EditText
    private lateinit var database : DatabaseReference
    private lateinit var accountAdminAdapter : AccountAdminAdapter

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
         binding = FragmentListAdminBinding.inflate(inflater,container,false)
        recyclerViewAccount = binding.recyclerViewAccount
        editTextSearch = binding.editSearch
        accountList = ArrayList()

        accountAdminAdapter = AccountAdminAdapter(accountList,object : IClickItem{
            override fun onClickItemSetRule(phone: String) {
                val dialogDelete = AlertDialog.Builder(
                    activity
                )
                dialogDelete.setMessage("Bạn chắc chắn muốn xóa quyền Admin của tài khoản có số điện thoại  $phone ?")
                dialogDelete.setPositiveButton(
                    "Có"
                ) { dialog, which ->
                    database = FirebaseDatabase.getInstance().getReference("Accounts")
                    database.child(phone).child("rule").setValue("user")
                }
                dialogDelete.setNegativeButton(
                    "Không"
                ) { dialog, which -> }
                dialogDelete.show()
            }

            override fun onClickItemDelete(phone: String) {
                TODO("Not yet implemented")
            }
        })
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerViewAccount.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recyclerViewAccount.addItemDecoration(dividerItemDecoration)
        recyclerViewAccount.adapter = accountAdminAdapter

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
         * @return A new instance of fragment ListAdminFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListAdminFragment().apply {
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
                    val account = dataSnapshot.getValue(Account::class.java)
                    val getRule = snapshot.child(dataSnapshot.key!!).child("rule").getValue(String::class.java)
                    if (getRule == "admin") {
                        accountList.add(account!!)
                    }
                }
                accountAdminAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun event() {
        getListPhoneBooksRealtimeDB()
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                accountAdminAdapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}