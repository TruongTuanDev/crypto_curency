package com.example.testapi.Adapter

import SessionManager
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.math.BigDecimal
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.R
import com.example.testapi.databinding.ItemWatchListBinding
import com.example.testapi.model.DataItem
import com.example.testapi.my_interface.ItemClickListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.util.Locale

class WatchListAdapter(context: Context, var lDataItems:  MutableList<DataItem>, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>(){
    private val sessionManager = SessionManager(context)
    private lateinit var database :  DatabaseReference

    inner class WatchListViewHolder(private var  binding : ItemWatchListBinding) : RecyclerView.ViewHolder(binding.root) {
        val mIconCoin : ImageView = binding.watchlistImg
        val starImg : ImageView = binding.watchlistStar
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            binding.watchlistSymbol.text = dataItem.symbol
            val formattedPriceString = String.format(Locale.US, "%.5f", dataItem.quote.usd.price)
            var bigDecimal = BigDecimal(formattedPriceString)
            if (bigDecimal < BigDecimal("0.00000001")) {
                bigDecimal = bigDecimal.multiply(BigDecimal("1000"))
                binding.watchlistSymbol.text = "1000"+ dataItem.symbol
            }
            val decimalFormat = DecimalFormat("#.########")
            val formattedPrice = decimalFormat.format(bigDecimal)
            binding.watchlistPrice.text = formattedPrice
            if(dataItem.quote.usd.percent_change_24h < 0){
                val percentChange : Double =  dataItem.quote.usd.percent_change_24h * -1
                binding.watchlistChange.text = "-"+ String.format("$%.3f",percentChange) + "%"
                binding.watchlistChange.setTextColor(Color.RED)
                binding.watchlistChangelogo.setImageResource(R.drawable.ic_caret_down)
            }else{
                binding.watchlistChange.text = "+"+ String.format("$%.3f",dataItem.quote.usd.percent_change_24h) + "%"
                binding.watchlistChange.setTextColor(Color.GREEN)
                binding.watchlistChangelogo.setImageResource(R.drawable.ic_caret_up)
            }
            binding.watchlistName.text = dataItem.name
            val loadImage = LoadImage(mIconCoin)
            loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/${dataItem.id}.png")
//            binding.watchlistStar.setImageResource(R.drawable.ic_star_fill)

            binding.itemWatchlist.setOnClickListener{
                itemClickListener.onClick(dataItem)
            }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListViewHolder {
        val view = ItemWatchListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WatchListViewHolder(view)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder : WatchListViewHolder, position: Int) {
        val dataItem  = lDataItems[position]
        val phone = sessionManager.getPhoneInstall()
        holder.bind(dataItem)
        holder.starImg.setOnClickListener {
            database = phone?.let { it1 -> FirebaseDatabase.getInstance().getReference("Accounts").child(it1
            ).child("watchlist").child(dataItem.symbol)
            }!!
            database.setValue(null).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lDataItems.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: MutableList<DataItem>) {
        lDataItems = newDataList
        notifyDataSetChanged()
    }

}