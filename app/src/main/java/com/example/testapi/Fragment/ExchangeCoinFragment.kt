package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.testapi.R
import com.example.testapi.databinding.FragmentExchangeCoinBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.Wallet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ncorti.slidetoact.SlideToActView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var dataItem: DataItem
private lateinit var sessionManager : SessionManager
private lateinit var database : DatabaseReference
private lateinit var walletBalanceText : String



class ExchangeCoinFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentExchangeCoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExchangeCoinBinding.inflate(inflater, container, false)
        arguments?.let {
            dataItem = it.getSerializable("object_coin") as DataItem
        }
        loadData()
        binding.tvCryptoName.text = dataItem.name
        loadImageCoin(binding.ivCryptoImage, dataItem.id)
        val priceText = dataItem.quote.usd.price.toString()
        val price = priceText.substringAfter("$").replace(",", "").toDoubleOrNull()
        binding.tvCryptoPrice.text = "%.3f".format(price) + " USD"

        binding.btnCalculate.setOnClickListener{
            val amountToBuy = binding.etAmountToBuy.text.toString().toDoubleOrNull()
            val walletBalanceText = binding.tvWalletBalance.text.toString()
            val walletBalance = walletBalanceText.substringAfter("$").replace(",", "").toDoubleOrNull()
            if (amountToBuy != null && price != null && walletBalance != null) {
                if (amountToBuy <= walletBalance) {
                    val transactionFeeRate = 0.005
                    val cryptoAmounts = amountToBuy / (price * 25000)
                    val cryptoAmount = amountToBuy / (price * 25000) - ((amountToBuy / (price * 25000)) * transactionFeeRate)
                    binding.tvCryptoAmount.text = "Số tiền điện tử quy đổi: %.6f ${dataItem.name}".format(cryptoAmount)
                    Log.e("trước",cryptoAmounts.toString())
                    Log.e("sau",cryptoAmount.toString())
                    binding.tvCryptoAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.tvCryptoAmount.text = "Số dư ví không đủ"
                    binding.tvCryptoAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            } else {
                binding.tvCryptoAmount.text = "Vui lòng nhập số tiền hợp lệ"
                binding.tvCryptoAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            binding.slideToConfirm.resetSlider()
        }
        binding.slideToConfirm.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {

                handleTransaction()
                Toast.makeText(requireActivity(), "Giao dịch đã được xác nhận!", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun handleTransaction() {
        val phone: String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone).child("balance")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balance = snapshot.getValue(Double::class.java)
                    val amountToBuy = binding.etAmountToBuy.text.toString().toDoubleOrNull()
                    val newBalance = balance?.minus(amountToBuy!!)
                    database.setValue(newBalance)
                    database = phone.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1) }
                    database.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (amountToBuy != null) {
                            val account = dataSnapshot.getValue(Account::class.java)
                            val currentDate = LocalDate.now()
                            val walets =  HashMap<String, String>()
                            val price = dataItem.quote.usd.price
                            val transactionFeeRate = 0.005
                            val cryptoAmount = amountToBuy / (price * 25000) - ((amountToBuy / (price * 25000)) * transactionFeeRate)
                                    val wallet = Wallet(
                                        id = dataItem.id.toString(),
                                        name = dataItem.name,
                                        symbol = dataItem.symbol,
                                        price = dataItem.quote.usd.price,
                                        quantity = amountToBuy,
                                        quantity_curency = cryptoAmount,
                                        buy_date = currentDate.toString())
                                     database.child("wallets").child(dataItem.name).setValue(wallet)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                    binding.tvWalletBalance.text = newBalance?.let { formatNumber(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            Log.e("Số điện thoại hiện tại ", "Không có đâu")
        }
    }
    fun loadImageCoin(imageView : ImageView, coinId: Int){
        val loadImage = LoadImage(imageView)
        loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/$coinId.png")
    }
    class LoadImage(private val imageView: ImageView) {
        fun loadImage(url: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = fetchBitmap(url)
                imageView.setImageBitmap(bitmap)
            }
        }
        private suspend fun fetchBitmap(url: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                var bitmap: Bitmap? = null
                try {
                    val inputStream: InputStream = URL(url).openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                bitmap
            }
        }

    }
    fun formatNumber(number: Double): String {
        val formatter = NumberFormat.getInstance(Locale.US)
        return formatter.format(number)
    }
    private fun loadData() {
        val phone: String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            Log.e("Số điện thoại hiện tại ", phone)
            database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone).child("balance")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balance = snapshot.getValue(Double::class.java)
                    Log.e("Tiền của mày", balance.toString())
                    binding.tvWalletBalance.text = balance?.let { formatNumber(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            Log.e("Số điện thoại hiện tại ", "Không có đâu")
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExchangeCoinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExchangeCoinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}