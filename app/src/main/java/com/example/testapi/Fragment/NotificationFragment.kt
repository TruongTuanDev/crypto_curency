package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.NotifyAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentNotificationBinding
import com.example.testapi.model.Account
import com.example.testapi.model.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.Transaction.Handler
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentNotificationBinding
    private lateinit var recyclerViewNotification : RecyclerView
    private lateinit var imgCreateNotifi : ImageView
    private lateinit var database : DatabaseReference
    private lateinit var notificationList  : ArrayList<Notification>
    private lateinit var notificationAdapter : NotifyAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var accountList : ArrayList<Account>
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
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        accountList = ArrayList()
        notificationList = ArrayList()
        notificationAdapter = NotifyAdapter(notificationList)
        imgCreateNotifi = binding.imgCreateNotifcation
        recyclerViewNotification = binding.recyclerViewNotification
        imgCreateNotifi.setOnClickListener{
            openProfileDialog(Gravity.CENTER)
        }
        getListFeedBacksRealtimeDB()
        getListPhoneBooksRealtimeDB()
        setupRecyclerView(recyclerViewNotification,notificationAdapter)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }
    private fun openProfileDialog(gravity: Int){
        val dialog  : Dialog? = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_addnotifi)
        val window = dialog?.window ?: return
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val winDowAttributes : WindowManager.LayoutParams = window.attributes
        winDowAttributes.gravity = gravity
        window.attributes = winDowAttributes

        if (Gravity.CENTER == gravity){
            dialog.setCancelable(true)
        }else{
            dialog.setCancelable(false)
        }
        var selectedNameTitle = ""
        var selectedPhone =""
        val edtTitleNoti : EditText = dialog.findViewById(R.id.edtTitleNotifi)
        val edtDetailNoti : EditText = dialog.findViewById(R.id.edtDescNotifi)
        val edtUserNoti : TextView = dialog.findViewById(R.id.edtUserNotifi)
        val btnSendNoti : Button = dialog.findViewById(R.id.btnSendDialogNotifi)
        edtUserNoti.setOnClickListener{
            val phoneArray = arrayOfNulls<String>(accountList.size)
            for (i in accountList.indices){
                phoneArray[i] = accountList[i].phone

            }
            //alert dialog
            val builder = AlertDialog.Builder(dialog.context)
            builder.setTitle("Chọn User cần gửi")
            builder.setItems(phoneArray) { dialog, which ->
                // Xử lý khi người dùng chọn một mục từ danh sách
                selectedNameTitle = accountList[which].name.toString()
                selectedPhone = accountList[which].phone!!
                edtUserNoti.setText("$selectedPhone-$selectedNameTitle")
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        btnSendNoti.setOnClickListener {
            val title = edtTitleNoti.text.toString()
            val description = edtDetailNoti.text.toString()
            if (title.isNotEmpty() || description.isNotEmpty()) {
                val keyIdOld: Int = sessionManager.getKeyIDNotify()
                val adminSelectPhone = edtUserNoti.text.toString()
                val index = adminSelectPhone.indexOf("-")

// Cắt chuỗi thành hai phần
                val phone = adminSelectPhone.substring(0, index)  // Phần trước dấu "-"
                val name = adminSelectPhone.substring(index + 1)
                val keyId = phone.toIntOrNull()
//                val timestamp=
                database = FirebaseDatabase.getInstance().getReference("Notification").child(phone!!)
                val accountRef = FirebaseDatabase.getInstance().getReference("Accounts").child(phone!!).child("countNotify")
                accountRef.runTransaction(object : Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentCount = mutableData.getValue(Long::class.java)
                        if (currentCount == null) {
                            mutableData.value = 1
                        } else {
                            mutableData.value = currentCount + 1
                        }
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        if (databaseError != null) {
                        }
                    }
                })
                val idNofi : String = database.push().key!!
                val notification = Notification(
                    id = idNofi,
                    title = title,
                    description = description)
                // Đưa lên firebase
                val time=System.currentTimeMillis()
                database.child("" + time).setValue(notification)
                sessionManager.setKeyIDNotify(time.toInt())
                dialog.dismiss()
            } else {
                Toast.makeText(activity, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
//    fun incrementCountNotify(phone: String?) {
//        val accountRef = FirebaseDatabase.getInstance().getReference("Accounts").child(phone!!).child("countNotify")
//        accountRef.runTransaction(object : Handler {
//            override fun doTransaction(mutableData: MutableData): Transaction.Result {
//                val currentCount = mutableData.getValue(Long::class.java)
//                if (currentCount == null) {
//                    mutableData.value = 1
//                } else {
//                    mutableData.value = currentCount + 1
//                }
//                return Transaction.success(mutableData)
//            }
//
//            override fun onComplete(
//                databaseError: DatabaseError?,
//                committed: Boolean,
//                dataSnapshot: DataSnapshot?
//            ) {
//                if (databaseError != null) {
//                }
//            }
//        })
//    }
    private fun getListFeedBacksRealtimeDB() {
        database = FirebaseDatabase.getInstance().getReference("Notification")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (dataSnapshot in snapshot.children) {
                    for (itemSnapshot in dataSnapshot.children) {
                        val id: String? = itemSnapshot.child("id").getValue(String::class.java)
                        val description: String? = itemSnapshot.child("description").getValue(String::class.java)
                        val title: String? = itemSnapshot.child("title").getValue(String::class.java)
                        val notification = Notification(id, description, title)
                        Log.e("Thông bao", notification.toString())
                        notification?.let { notificationList.add(it) }!!
                    }
                }
                notificationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getListPhoneBooksRealtimeDB() {
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                accountList.clear()
                for (dataSnapshot in snapshot.children) {
                    val account = dataSnapshot.getValue(Account::class.java)
                    val getRule = snapshot.child(dataSnapshot.key!!).child("rule").getValue(
                        String::class.java
                    )
                    if (getRule == "user") {
                        accountList.add(account!!)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}