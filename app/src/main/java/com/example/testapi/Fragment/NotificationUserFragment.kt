package com.example.testapi.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.Adapter.NotifyAdapter
import com.example.testapi.Adapter.NotifyUserAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentNotificationUserBinding
import com.example.testapi.model.Notification
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
 * Use the [NotificationUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationUserFragment : Fragment() {
    private lateinit var database : DatabaseReference
    private lateinit var notificationList : ArrayList<Notification>
    private lateinit var notificationAdapter: NotifyUserAdapter
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var backHome : ImageView
    private lateinit var _binding : FragmentNotificationUserBinding
    private val binding get() = _binding
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
       _binding = FragmentNotificationUserBinding.inflate(inflater, container, false)
       backHome = binding.notificationBack
       loadData()
       backHome.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.body_container ,  HomeFragment())?.commit()
        }
        notificationList = ArrayList()
        notificationRecyclerView = binding.notificationRecycleView
        notificationAdapter = NotifyUserAdapter(notificationList)
        setupRecyclerView(notificationRecyclerView,notificationAdapter)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationUserFragment().apply {
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

    private fun loadData() {
        database = FirebaseDatabase.getInstance().getReference("Notification")
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (dataSnapshot in snapshot.children) {
                    val notification: Notification? = dataSnapshot.getValue(Notification::class.java)
                    if (notification != null) {
                        notificationList.add(notification)
                    }
                }
                notificationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}