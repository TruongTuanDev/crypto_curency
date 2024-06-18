package com.example.testapi.Fragment

import SessionManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.testapi.databinding.FragmentChangePasswordBinding
import com.example.testapi.model.Account
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChangePasswordFragment : Fragment() {
    private var _binding : FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private lateinit var database : DatabaseReference
    private lateinit var editOldPassword : EditText
    private lateinit var editNewPassword : EditText
    private lateinit var editRePassword : EditText

    private lateinit var imgBack : ImageView

    private lateinit var btnSave : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        editNewPassword = binding.editNewPasswordProfile
        editOldPassword = binding.editOldPasswordProfile
        editRePassword = binding.editReenterPasswordProfile

        imgBack= binding.btnBackPassProChange
        btnSave = binding.btnSavePassProChange
        loadListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadListener(){
       btnSave.setOnClickListener(object : OnClickListener{
           override fun onClick(v: View?) {
               if(editNewPassword.text.toString().isEmpty()
                   || editOldPassword.text.toString().isEmpty()
                   || editRePassword.text.toString().isEmpty()
                   ){
                   Toast.makeText(requireActivity(),"Cần nhập đầy đủ thông tin !",Toast.LENGTH_SHORT).show()
               }else{
                   val phone = sessionManager.getPhoneInstall()
                   database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it) }!!
                   database.addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onDataChange(snapshot: DataSnapshot) {
                           val account = snapshot.getValue(Account::class.java)
                           if(editOldPassword.text.toString().equals(account?.pass)){
                               if (editNewPassword.text.toString().equals(editRePassword)){
                                   Toast.makeText(requireActivity(),"Mật khẩu thay đổi thành công",Toast.LENGTH_SHORT).show()
                                   database.child("pass").setValue(editNewPassword.text.toString())
                                   activity?.supportFragmentManager?.popBackStack()
                               }else{
                                   Toast.makeText(requireActivity(),"Mật khẩu không trùng khớp !",Toast.LENGTH_SHORT).show()
                               }
                           }else{
                               Toast.makeText(requireActivity(),"Nhập mật khẩu sai!",Toast.LENGTH_SHORT).show()
                           }
                       }

                       override fun onCancelled(error: DatabaseError) {
                       }
                   })
               }
           }

       })
        imgBack.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                activity?.supportFragmentManager?.popBackStack()
            }

        })
    }
}