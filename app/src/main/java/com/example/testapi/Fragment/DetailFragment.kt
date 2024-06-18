package com.example.testapi.Fragment

import SessionManager
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.API.ApiCoinMarket
import com.example.testapi.Adapter.MarketAdapter
import com.example.testapi.R
import com.example.testapi.databinding.FragmentDetailBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.model.DetailItem
import com.example.testapi.model.Market
import com.example.testapi.my_interface.ITransmitData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.Collections


class DetailFragment : Fragment() {

    private lateinit var _binding : FragmentDetailBinding
    private val binding get() = _binding
    private lateinit var sessionManager: SessionManager

    private lateinit var database : DatabaseReference

    private lateinit var dataItem: DataItem



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        arguments?.let {
            dataItem = it.getSerializable("object_coin") as DataItem
        }
        val ckStar : CheckBox = binding.ckStarDetail
        val phone = sessionManager.getPhoneInstall()
        database = phone?.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1) }!!
        ckStar.setOnClickListener{
            if (ckStar.isChecked){
                database.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val account = dataSnapshot.getValue(Account::class.java)
                        if (account?.watchlist == null){
                            val wList : HashMap<String, Boolean> = HashMap()
                            wList[dataItem.symbol] = true
                            database.child("watchlist").setValue(wList)
                        }else{
                            account.watchlist!![dataItem.symbol] = true
                            database.child("watchlist").setValue(account.watchlist)
                        }
                        Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }else{
                database.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val account = dataSnapshot.getValue(Account::class.java)
                        val wList : HashMap<String, Boolean>? = account?.watchlist
                        wList?.remove(dataItem.symbol,true)
                        database.child("watchlist").setValue(wList)
                        Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }
        binding.btnBuy.setOnClickListener{
            onClickToBuy(dataItem)
        }
        binding.btn1h.setOnClickListener{
            loadChartDetailTime(dataItem, "1H")
        }
        binding.btn24h.setOnClickListener{
            loadChartDetailTime(dataItem, "W")
        }
        binding.btn7d.setOnClickListener{
            loadChartDetailTime(dataItem, "D")
        }
        binding.btn30d.setOnClickListener{
            loadChartDetailTime(dataItem, "M")
        }
        binding.imgBackDetail.setOnClickListener{
            if (sessionManager.getKeyIDCheckDetail() == 1){
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.body_container, MarketFragment())?.commit()
                sessionManager.setKeyIDCheckDetail(0)
            }else{
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.body_container, WatchListFragment())?.commit()
            }
        }
        loadDataDetail(dataItem)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun loadDataDetail(dataItem: DataItem){
        val coinId : Int = dataItem.id
        loadData(dataItem)
        loadImageCoin(binding.imgDetail,coinId)
        checkStar(binding.ckStarDetail, dataItem)
        loadChartDetail(dataItem)
        Log.e("Nayyyyyyyyy",dataItem.circulating_supply.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    @SuppressLint("SetTextI18n")
    fun loadData(itemDetai : DataItem){
        binding.txtNameDetail.text = itemDetai.name
        binding.txtSymbolDetail.text = itemDetai.symbol
        fun formatLargeNumber(value: Double): String {
            return when {
                value >= 1_000_000_000 -> String.format("$%.2fB", value / 1_000_000_000)
                value >= 1_000_000 -> String.format("$%.2fM", value / 1_000_000)
                else -> value.toString()
            }
        }
        fun updatePercentChangeTextView(textView: TextView, percentChange: Double) {
            if (percentChange < 0) {
                textView.text = String.format("-$%.2f%%", -percentChange)
                textView.setTextColor(Color.RED)
            } else {
                textView.text = String.format("+$%.2f%%", percentChange)
                textView.setTextColor(Color.GREEN)
            }
        }

        val percentChange30d = itemDetai.quote.usd.percent_change_30d
        updatePercentChangeTextView(binding.tView1Month, percentChange30d)

        val percentChange7d = itemDetai.quote.usd.percent_change_7d
        updatePercentChangeTextView(binding.tView1Week, percentChange7d)

        val percentChange24H = itemDetai.quote.usd.percent_change_24h
        updatePercentChangeTextView(binding.tView24H, percentChange24H)

        val percentChange1H = itemDetai.quote.usd.percent_change_1h
        updatePercentChangeTextView(binding.tView1H, percentChange1H)



        val marketCap = itemDetai.quote.usd.market_cap.toDouble()
        binding.tViewMarketCap.text = formatLargeNumber(marketCap)

        val circulating = itemDetai.circulating_supply.toDouble()
        binding.tViewCirculating.text = formatLargeNumber(circulating)

        val supply = itemDetai.total_supply.toDouble()
//        Log.e("Tổng cung",supply.toString())
        binding.tViewSupply.text = formatLargeNumber(supply)

        val view24HVol = itemDetai.quote.usd.volume_24h.toDouble()
        binding.tView24hVol.text = formatLargeNumber(view24HVol)



//        binding.tView24hVol.text = itemDetai.quote["USD"]

        val formattedPrice = String.format("%.2f", itemDetai.quote.usd.price)
        binding.txtUSDDetail.text = formattedPrice
        val percent24hDetail = String.format("%.2f", itemDetai.quote.usd.percent_change_24h) + "%"
        binding.percent24hDetail.text = percent24hDetail
        if (itemDetai.quote.usd.percent_change_24h <0){
            binding.percent24hDetail.setBackgroundResource(R.drawable.border_percent_red)
        }else{
            binding.percent24hDetail.setBackgroundResource(R.drawable.border_percent_green)
        }
    }
    fun checkStar(checkBox: CheckBox, dataItem: DataItem){
        val phone = sessionManager.getPhoneInstall()
        database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it) }!!
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val account = dataSnapshot.getValue(Account::class.java)
                if (account?.watchlist != null){
                    for (item in account.watchlist!!.entries) {
                        if (item.key == dataItem.symbol) {
                            checkBox.isChecked = true
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun loadImageCoin(imageView : ImageView, coinId: Int){
        val loadImage = LoadImage(imageView)
        loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/$coinId.png")
    }
    @SuppressLint("SetJavaScriptEnabled")
    fun loadChartDetail(dataItem: DataItem){
          binding.webViewChart.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + dataItem.symbol + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")

        val webSettings: WebSettings = binding.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true

        binding.webViewChart.webViewClient = WebViewClient()


        // URL của biểu đồ TradingView
//        val tradingViewUrl = "https://www.binance.com/vi/trade/"+ dataItem.symbol+"_USDT?_from=markets&type=spot"
        val tradingViewUrl = "https://gocharting.com/terminal?ticker=COINBASE:"+ dataItem.symbol+"USD"

        // Tải URL vào WebView
        binding.webViewChart.loadUrl(tradingViewUrl)

//        binding.webViewChart.loadUrl("https://tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + dataItem.symbol + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")

    }
    private fun onClickToBuy(dataItem: DataItem) {
        val bundle = Bundle()
        bundle.putSerializable("object_coin", dataItem)
        val exchangeCoinFragment = ExchangeCoinFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.body_container, exchangeCoinFragment)
            .addToBackStack(null)
            .commit()
    }
    @SuppressLint("SetJavaScriptEnabled")
    fun loadChartDetailTime(dataItem: DataItem, time: String){
        binding.webViewChart.settings.javaScriptEnabled = true
        binding.webViewChart.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.webViewChart.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + dataItem.symbol + "USD&interval=" + time + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")
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
}