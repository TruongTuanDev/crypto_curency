package com.example.testapi.Adapter

import SessionManager
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.R
import com.example.testapi.databinding.ItemMarketBinding
import com.example.testapi.model.Account
import com.example.testapi.model.DataItem
import com.example.testapi.my_interface.ITransmitData
import com.example.testapi.my_interface.ItemClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.Locale

class MarketAdapter(private val context: Context,private var lDataItems: ArrayList<DataItem>,private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>(), Filterable{
    private val sessionManager = SessionManager(context)
    private lateinit var database : DatabaseReference
    private var ldataItemOlds : ArrayList<DataItem> = lDataItems


    inner class MarketViewHolder(private val binding: ItemMarketBinding) : RecyclerView.ViewHolder(binding.root),View.OnLongClickListener {
        var item_maket : ConstraintLayout = binding.itemMarket
        val symbol : TextView = binding.currencySymbolTextView
        val price : TextView = binding.currencyPriceTextView
        val name : TextView = binding.currencyNameTextView
        val change : TextView = binding.currencyChangeTextView
        val changeImg : ImageView = binding.currencyChangeImageView
        val chartView : ImageView = binding.currencyChartImageView
        val mIconCoin : ImageView = binding.currencyImageView
        val iConStart : CheckBox = binding.ckStar
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            symbol.text = dataItem.symbol
            val formattedPrice = String.format("%.4f", dataItem.quote.usd.price)
            price.text = formattedPrice
            name.text = dataItem.name
            if(dataItem.quote.usd.percent_change_24h < 0){
                val percentChange : Double =  dataItem.quote.usd.percent_change_24h * -1
                change.text = "-"+ String.format("$%.5f",percentChange) + "%"
                change.setTextColor(Color.RED)
                changeImg.setImageResource(R.drawable.ic_caret_down)
            }else{
                change.text = "+"+ String.format("$%.5f",dataItem.quote.usd.percent_change_24h) + "%"
                change.setTextColor(Color.GREEN)
                changeImg.setImageResource(R.drawable.ic_caret_up)
            }
            val loadImage = LoadImage(mIconCoin)
            loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/${dataItem.id}.png")
            val loadImageChart = LoadImage(chartView)
            loadImageChart.loadImage("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.id + ".png")

            binding.itemMarket.setOnClickListener{
                itemClickListener.onClick(dataItem)
            }
        }



        override fun onLongClick(v: View?): Boolean {
            return false
        }

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
    override fun getItemCount(): Int {
        return lDataItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val view = ItemMarketBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MarketViewHolder(view)
    }


    override fun onBindViewHolder(holder : MarketViewHolder, position: Int) {
        val dataItem  = lDataItems[position]
        holder.bind(dataItem)
        checkWatchListStart(holder.iConStart, dataItem)
        holder.iConStart.setOnClickListener{
            if (holder.iConStart.isChecked){
                val phone = sessionManager.getPhoneInstall()
                database = phone?.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1) }!!
                database.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val account = dataSnapshot.getValue(Account::class.java)
                        if (account?.watchlist == null){
                            val wList =  HashMap<String, Boolean>()
                            wList[dataItem.symbol] = true
                            database.child("watchlist").setValue(wList)
                        }else{
                            account.watchlist!![dataItem.symbol] = true
                            database.child("watchlist").setValue(account.watchlist)
                        }
                        Toast.makeText(context,"Đã thêm ${dataItem.name} vào danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }else{
                val phone = sessionManager.getPhoneInstall()
                database = phone?.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1) }!!
                database.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val account = dataSnapshot.getValue(Account::class.java)
                        var wList = HashMap<String, Boolean>()
                        wList = account?.watchlist!!
                        wList.remove(dataItem.symbol,true)
                        database.child("watchlist").setValue(wList)
                        Toast.makeText(context,"Đã xóa khỏi danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<DataItem>) {
        lDataItems = newDataList as ArrayList<DataItem>
        notifyDataSetChanged()
    }
    fun checkWatchListStart(checkBox: CheckBox, dataItem: DataItem){
        val phone = sessionManager.getPhoneInstall()
        database = phone?.let { FirebaseDatabase.getInstance().getReference("Accounts").child(it) }!!
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val account : Account? = dataSnapshot.getValue(Account::class.java)
                if (account?.watchlist != null){
                    for ((symbol, isWatched) in account.watchlist!!) {
                        if (symbol == dataItem.symbol) {
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

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                var strSearch = constraint.toString()
                if (strSearch.isEmpty()){
                    lDataItems = ldataItemOlds
                }else{
                    val listData  = ArrayList<DataItem>()
                    for (dataItem in ldataItemOlds){
                        if (dataItem.name.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true
                            || dataItem.symbol.toLowerCase(Locale.ROOT)?.contains(strSearch.toLowerCase(Locale.ROOT)) == true
                        ) {
                           listData.add(dataItem)
                       }
                    }
                    lDataItems = listData
                }
                val filterResults = FilterResults()
                filterResults.values = lDataItems
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                lDataItems = results?.values as ArrayList<DataItem>
                notifyDataSetChanged()
            }

        }
    }

}