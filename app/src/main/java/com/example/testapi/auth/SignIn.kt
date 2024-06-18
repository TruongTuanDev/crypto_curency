package com.example.testapi.auth

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testapi.Admin
import com.example.testapi.MainActivity
import com.example.testapi.R
import com.example.testapi.databinding.ActivitySignInBinding
import com.example.testapi.screens.MainAdminActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager


    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (sessionManager.getFirstInstall() == false){
            Log.e("rồi nha", "ok mày")
            event()
        }else{
            Log.e("Chưa nha", "ok mày")
            if (sessionManager.getRuleUserInstall()){
                Log.e("Mày vào rồi ra làm gì", "ok mày")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else if(sessionManager.getRuleAdminInstall()){

                val intent = Intent(this, MainAdminActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Log.e("Thì ra l ko có rule", "ok mày")
            }
        }
        binding.imgvGoogle.setOnClickListener {
            signInWithGG()
        }
        binding.btnSignIn.setOnClickListener(View.OnClickListener { signInAccount()})
        binding.textSignUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

    }
    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Đăng nhập bằng google thất bại : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signInWithGG() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent












        = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun event() {
        binding.textSignUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        binding.textForgotPass.setOnClickListener(View.OnClickListener {  })
        binding.btnSignIn.setOnClickListener(View.OnClickListener { signInAccount()})
        binding.imgvGoogle.setOnClickListener(View.OnClickListener{signInWithGG()} )
        binding.imgvFacebook.setOnClickListener(View.OnClickListener {
            //signInWithFB();
        })
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Đăng nhập với tư cách  ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Xác thực thất bại", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun signInAccount(){
        database = FirebaseDatabase.getInstance().getReference("Accounts")
        val phoneNumber : String = binding.edtPhoneSignIn.text.toString().trim()
        val pass :String = binding.edtPasswordSignIn.text.toString().trim()
        if(phoneNumber.isEmpty()){
            binding.edtPhoneSignIn.error = "Số điện thoại hoặc email không được để trống!"
            return
        }else if(phoneNumber.isEmpty()){
            binding.edtPasswordSignIn.error = "Mật khẩu không được để trống!"
            return
        }else{
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (accountSnapshot in dataSnapshot.children){
                        val phoneNumberSnapshot = accountSnapshot.child("phone").getValue(String::class.java)?.trim()
                        if (phoneNumberSnapshot == phoneNumber){
                            val getPass: String = accountSnapshot.child("pass").value.toString().trim()
                            val getRule: String = accountSnapshot.child("rule").value.toString().trim()
                            if (getRule == "user"){
                                if (getPass == pass){
                                    sessionManager.setFirstInstall(true)
                                    sessionManager.setPhoneInstall(phoneNumber)
                                    sessionManager.setRuleUserInstall(true)
                                    database.child(phoneNumberSnapshot).child("isOnline").setValue(true)

                                    Toast.makeText(this@SignIn, "Đăng nhập thành công",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignIn, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    return
                                }else{
                                    Toast.makeText(this@SignIn, "Mật khẩu sai !", Toast.LENGTH_SHORT).show()
                                }
                            }else if(getRule == "admin"){
                                if (getPass == pass ){
                                    sessionManager.setFirstInstall(true)
                                    sessionManager.setPhoneInstall(phoneNumber)
                                    sessionManager.setRuleAdminInstall(true)

                                    database.child(phoneNumberSnapshot).child("isOnline").setValue(true)
                                    Toast.makeText(this@SignIn, "Đăng nhập thành công",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignIn, MainAdminActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    return
                                }else{
                                    Toast.makeText(this@SignIn, "Mật khẩu sai !", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@SignIn, "Firebase Error ${databaseError.message}!",Toast.LENGTH_SHORT).show()
                }

            })
        }
    }



}