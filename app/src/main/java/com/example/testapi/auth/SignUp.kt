package com.example.testapi.auth


import SessionManager
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testapi.R
import com.example.testapi.databinding.ActivitySignUpBinding
import com.example.testapi.model.Account
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar


class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dbRef: DatabaseReference = database.getReference("Accounts")
    private lateinit var sessionManager : SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewSignIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener{
           registerAccount()
        }
        binding.edtBirthSignup.setOnClickListener{
            datePickerDiglog()
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun sex(): String {
        return when {
            binding.radioMale.isChecked -> binding.radioMale.text.toString()
            binding.radioFemale.isChecked -> binding.radioFemale.text.toString()
            else -> ""
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun datePickerDiglog(){
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        var year  = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(this,
            { datePicker, i, i1, i2 ->
                calendar[i, i1] = i2
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                binding.edtBirthSignup.setText(simpleDateFormat.format(calendar.time))
            }, year, month, day
        )
        datePickerDialog.show()
    }
    private fun registerAccount(){
        val name: String = binding.edtUsernameSignup.text.toString()
        val phone: String = binding.edtPhoneSignup.text.toString()
        val email: String = binding.edtEmailSignup.text.toString()
//        val date = datePickerDiglog()
        val date: String = binding.edtBirthSignup.text.toString()
        val pass: String = binding.edtPassSignup.text.toString()
        val confpass: String = binding.edtConfPassSignup.text.toString()
        val rule : String = "user"
        val notify : Int = 0
        val country: String =  binding.ccpCountrySignup.selectedCountryCode
        val sex: String = sex()

        if (name.isEmpty()){
            binding.edtUsernameSignup.error = "Vui lòng nhập đầy đủ họ tên"
            return
        }else if(phone.isEmpty()){
            binding.edtPhoneSignup.error = "Vui lòng nhập vào số điện thoại"
            return
        }else if(email.isEmpty()){
            binding.edtEmailSignup.error = "Vui lòng nhập vào email"
            return
        }
        else if(date.isEmpty()){
            binding.edtBirthSignup.error = "Vui lòng nhập vào ngày sinh"
            return
        }
        else if(pass.isEmpty()){
            binding.edtPassSignup.error = "Vui lòng nhập vào mật khẩu"
            return
        }else if(pass != confpass){
            Toast.makeText(this@SignUp, "Mật khẩu không khớp,vui lòng nhập lại !",Toast.LENGTH_SHORT).show()
        }else{
            val userID : String = dbRef.push().key!!
            val account = Account(
                userID = userID,
                name = name,
                phone = phone,
                email = email,
                date = date,
                pass = pass,
                country = country,
                sex = sex,
                rule = rule,
                notify = notify
            )
            dbRef.child(phone).setValue(account)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignUp, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        sessionManager.setFirstInstall(true)
                        val intent = Intent(this, SignIn::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignUp, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this@SignUp, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}