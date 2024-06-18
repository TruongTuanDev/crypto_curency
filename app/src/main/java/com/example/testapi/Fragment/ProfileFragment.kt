package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.testapi.Adapter.WatchListAdapter
import com.example.testapi.R
import com.example.testapi.auth.SignIn
import com.example.testapi.databinding.FragmentProfileBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.FeedBack
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import java.time.LocalDate


class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private lateinit var database : DatabaseReference
    private lateinit var txtFullName : TextView
    private lateinit var txtPhone : TextView
    private lateinit var txtEmail : TextView
    private lateinit var txtBirthday : TextView
    private lateinit var txtGender : TextView

    private lateinit var imgCreateFeedBack : ImageView
    private lateinit var imgEditProfile : ImageView
    private lateinit var imgEditPass : ImageView
    private lateinit var imgLogOut : ImageView

    private lateinit var btnContact : Button

    private lateinit var ccpCountryCodePicker: CountryCodePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        txtFullName = binding.txtFullNameProfile
        txtEmail = binding.txtEmailProfile
        txtPhone = binding.txtPhoneProfile
        txtBirthday = binding.txtBirthDayProfile
        txtGender = binding.txtGenderProfile
        ccpCountryCodePicker = binding.ccpCountryProfile

        btnContact = binding.btnContact

        imgCreateFeedBack = binding.imgCreateFeedback
        imgEditProfile = binding.imgchangeProfile
        imgEditPass = binding.imgchangePassword
        imgLogOut = binding.imgLogOut

        imgCreateFeedBack.setOnClickListener {
            openProfileDiglog(Gravity.CENTER)
        }
        loadData()
        loadListener()
        UpdateData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun openProfileDiglog(gravity : Int ) {
        val dialog = activity?.let { Dialog(it) }
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_addfeedback)

            val window: Window = dialog.window ?: return

            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val windowAttributes = window.attributes
            windowAttributes.gravity = gravity
            window.attributes = windowAttributes

            if (Gravity.CENTER == gravity) {
                dialog.setCancelable(true)
            } else {
                dialog.setCancelable(false)
            }
            dialog.show()
            val edtTitleFeedBack: EditText = dialog.findViewById(R.id.edtTitleFeedback)
            val edtDetailFeedBack: EditText = dialog.findViewById(R.id.edtDescFeedback)
            val btnSendFeedBack: Button = dialog.findViewById(R.id.btnSendDialogFeedback)
            btnSendFeedBack.setOnClickListener {
                val title: String = edtTitleFeedBack.text.toString()
                val description: String = edtDetailFeedBack.text.toString()
                val date: String = LocalDate.now().toString()

                database = FirebaseDatabase.getInstance().getReference("FeedBacks")
                if (title.isNotEmpty() || description.isNotEmpty()) {
                    val feedBack = FeedBack(title, description, date)
                    database.push().setValue(feedBack)
                    dialog.dismiss()
                    Toast.makeText(requireActivity(), "Đã phản hồi thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    @SuppressLint("CommitTransaction")
    private fun loadListener(){
        imgEditProfile.setOnClickListener {
            val fragment = ChangeProfileFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.add(R.id.fragment_profile, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        imgEditPass.setOnClickListener {
            val fragment = ChangePasswordFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.add(R.id.fragment_profile, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        imgLogOut.setOnClickListener {
            val phone: String? = sessionManager.getPhoneInstall()
            database =FirebaseDatabase.getInstance().getReference("Accounts")
            if (phone != null) {
                database.child(phone).child("isOnline").setValue(false)
            }

            Log.d("Chương trình kết thúc", "")
            sessionManager.setFirstInstall(false)
            sessionManager.setPhoneInstall("")
            sessionManager.setRuleAdminInstall(false)
            sessionManager.setRuleUserInstall(false)
            val intent = Intent(activity, SignIn::class.java)
            intent.putExtra("checkSignIn", "")
            startActivity(intent)
            activity?.finish()
        }
        btnContact.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.body_container, ContactFragment())?.commit()
        }


    }

    fun loadData(){
        val phone : String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            Log.e("Số điện thoại hiện tại ",phone)
        }else{
            Log.e("Số điện thoại hiện tại ","Không có đâu")
        }
        database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it) }!!
//        database.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val account = snapshot.getValue(Account::class.java)
//                txtFullName.text = account?.name
//                txtEmail.text = account?.email
//                txtPhone.text = account?.phone
//                txtBirthday.text = account?.date
//                txtGender.text = account?.sex
//                account?.country?.toInt()?.let { ccpCountryCodePicker.setCountryForPhoneCode(it) }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun UpdateData(){
       database  = FirebaseDatabase.getInstance().getReference("Accounts")
        val phone : String? = sessionManager.getPhoneInstall()
        val accountRef  = phone?.let { database.child(it) }
        accountRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val account = dataSnapshot.getValue(Account::class.java)
                account?.let { it ->
                    txtFullName.text = it.name
                    txtEmail.text = it.email
                    txtPhone.text ="*******" + it.phone?.substring(7)
                    txtBirthday.text = it.date
                    txtGender.text = it.sex
                    it.country?.toInt()?.let { ccpCountryCodePicker.setCountryForPhoneCode(it) }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase Error", databaseError.message)
            }
        })
    }
}