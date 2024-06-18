package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import com.example.testapi.R
import com.example.testapi.databinding.FragmentChangeProfileBinding
import com.example.testapi.model.Account
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker





class ChangeProfileFragment : Fragment() {

    @SuppressLint("StaticFieldLeak")
    private var _binding: FragmentChangeProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    lateinit var database : DatabaseReference
    private lateinit var editFullName : EditText
    private lateinit var editEmail : EditText
    private lateinit var editBirthday : EditText
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var imgBack : ImageView
    private lateinit var btnSave : Button

    private lateinit var r_male : RadioButton
    private lateinit var r_female : RadioButton
    private lateinit var r_difference : RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeProfileBinding.inflate(inflater, container, false)
        editFullName = binding.editFullNameProfile
        editEmail = binding.editEmailProfile
        editBirthday = binding.editBirthDayProfile

        r_male = binding.radioMaleProChange
        r_female = binding.radioFemaleProChange
        r_difference = binding.radioDifferenceProChange

        countryCodePicker = binding.ccpCountryProfileChange

        imgBack = binding.imgBackProChange
        btnSave = binding.btnSaveProChange

        loadData()
        loadListener()
        return binding.root
    }
    private fun loadListener(){
        imgBack.setOnClickListener { activity?.supportFragmentManager?.popBackStack() }
        btnSave.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                if(editFullName.text.toString().isEmpty()
                    || editEmail.text.toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }else{
                    val phone: String? = sessionManager.getPhoneInstall()
                    database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it)}!!
                    database.child("name").setValue(editFullName.text.toString())
                    database.child("email").setValue(editEmail.text.toString())
                    if (r_male.isChecked){
                        database.child("sex").setValue("Male")
                    }else if (r_female.isChecked) {
                        database.child("sex").setValue("Female")
                    }else{
                        database.child("sex").setValue("Difference")
                    }
                    database.child("country").setValue(countryCodePicker.selectedCountryCode)
                }
                Toast.makeText(requireActivity(),"Thay đổi thành công",Toast.LENGTH_SHORT).show()
//                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.body_container, ProfileFragment())?.commit()
            }

        })
    }
    private fun loadData(){
        val phone: String? = sessionManager.getPhoneInstall()
        database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it) }!!
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                val account : Account? = snapshot.getValue(Account::class.java)
                editFullName.setText(account?.name)
                editEmail.setText(account?.email)
                editBirthday.setText(account?.date)
                if(account?.sex.equals("Male")){
                    r_male.isChecked = true
                }else if(account?.sex.equals("Female")){
                    r_female.isChecked = true
                }else{
                    r_difference.isChecked = true
                }
                account?.country?.let { countryCodePicker.setCountryForNameCode(it) }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }
}