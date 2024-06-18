package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.R
import com.example.testapi.databinding.FragmentSellCoinBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.Market
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
import retrofit2.Call
import retrofit2.Response
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
private lateinit var wallet: Wallet
private lateinit var sessionManager : SessionManager
private lateinit var database : DatabaseReference
private lateinit var walletBalanceText : String



class SellCoinFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentSellCoinBinding

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
        binding = FragmentSellCoinBinding.inflate(inflater, container, false)
        arguments?.let {
            wallet = it.getSerializable("object_coin") as Wallet
        }
//        loadData()
        loadDataCoin()
        loadQuantiyCoin()
        binding.tvCryptoName.text = wallet.name
        binding.nameCoin.text = wallet.symbol
        loadImageCoin(binding.ivCryptoImage, wallet.id.toInt())

        binding.btnCalculate.setOnClickListener{
            val amountSell = binding.etAmountToSell.text.toString().toDoubleOrNull()
            val walletBalanceText = binding.tvWalletBalance.text.toString()
            val priceNow = binding.tvCryptoPriceHinde.text.toString().toDoubleOrNull()


            val walletBalance = walletBalanceText.substringAfter("$").replace(",", "").toDoubleOrNull()
            if (amountSell != null && walletBalance != null && priceNow != null) {
                if (amountSell <= walletBalance) {

                    val totalVND = amountSell * (priceNow.times(25000))
                    val formattedNumber = String.format("%.10f",amountSell)
                    val formattedNumbertotalVND = String.format("%.3f", totalVND)

                    binding.tvCryptoAmount.text = "Số tiền Việt Nam Đồng quy đổi từ $formattedNumber" + " ${wallet.symbol} là : $formattedNumbertotalVND"
                    binding.tvCryptoBalanceHinden.text = totalVND.toString()
                    binding.tvCryptoAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.tvCryptoAmount.text = "Số dư  ${wallet.name} không đủ"
                    binding.tvCryptoAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            } else {
                binding.tvCryptoAmount.text = "Vui lòng nhập số lượng ${wallet.name} hợp lệ"
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
    private fun loadQuantiyCoin() {
        val phone: String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone)
                .child("wallets").child(wallet.name)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val wallet = snapshot.getValue(Wallet::class.java)
                    Log.e("VÍ mày",wallet.toString() )
                    val balance = wallet?.quantity_curency
                    Log.e("Số lượng mày có",balance.toString() )
                    val formattedNumber = String.format("%.10f", balance)
                    binding.tvWalletBalance.text = formattedNumber

//                    val newBalance = balance?.plus(amountToBuy!!)
//                    database.setValue(newBalance)
                    database = phone.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1) }
                    database.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            if (amountToBuy != null) {
//                                val currentDate = LocalDate.now()
//                                val price = dataItem.quote.usd.price
//                                val transactionFeeRate = 0.005
//                                val cryptoAmount = amountToBuy / (price * 25000) - ((amountToBuy / (price * 25000)) * transactionFeeRate)
//                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            Log.e("Số điện thoại hiện tại ", "Không có đâu")
        }
    }
    private fun handleTransaction() {
        val phone: String? = sessionManager.getPhoneInstall()
        if (phone != null) {
            database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone).child("balance")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val balance = snapshot.getValue(Double::class.java)
                    val amountBalanceNew = binding.tvCryptoBalanceHinden.text.toString().toDoubleOrNull()
                    val newBalance = balance?.plus(amountBalanceNew!!)
                    Log.e("nhập 1",balance.toString() )
                    Log.e("nhập 2",amountBalanceNew.toString() )
                    Log.e("nhập 3",newBalance.toString() )
                    database.setValue(newBalance)
                    database = FirebaseDatabase.getInstance().getReference("Accounts").child(phone)
                        .child("wallets").child(wallet.name)
                    database.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val wallet = dataSnapshot.getValue(Wallet::class.java)
                            if (amountBalanceNew != null) {
                                val quantitySell = binding.etAmountToSell.text.toString().toDoubleOrNull()
                                val newQuantity = quantitySell?.let {
                                    wallet?.quantity_curency?.minus(it)
                                }
                                binding.tvWalletBalance.text = newQuantity?.let { formatNumber(it) }
                                val updates = HashMap<String, Any>()
                                newQuantity?.let {
                                    updates["quantity_curency"] = it
                                }

                            database.updateChildren(updates).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Cập nhật thành công
                                    Log.d("Firebase", "Cập nhật quantity thành công")
                                } else {
                                    // Cập nhật thất bại
                                    Log.e("Firebase", "Cập nhật quantity thất bại", task.exception)
                                }
                            }
                        }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }

                    })

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
    private fun loadDataCoin() {
        ApiCoinMarket.apiCoinMarket.convertMarket(
            "2eb6903c-5421-4c55-82f5-4b034647e300",
            "market_cap", 1, 30, "all", "USD"
        ).enqueue(object : retrofit2.Callback<Market?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Market?>, response: Response<Market?>) {
                val market: Market? = response.body()
                Log.e("Mày lấy về ", wallet.name)
                val bitcoin : DataItem? = market?.data?.find { it.name == wallet.name }
                Log.e("Giá mày lấy về nè", bitcoin.toString())
                val priceText = bitcoin?.quote?.usd?.price.toString()
                val price = priceText.substringAfter("$").replace(",", "").toDoubleOrNull()
                binding.tvCryptoPrice.text = "%.3f".format(price) + " USD"
                binding.tvCryptoPriceHinde.text = price.toString()
            }

            override fun onFailure(call: Call<Market?>, t: Throwable) {
                Toast.makeText(requireActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show()
            }
        })
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