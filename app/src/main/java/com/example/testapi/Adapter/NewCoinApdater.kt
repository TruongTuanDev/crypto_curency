package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.math.BigDecimal
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemCoinBinding
import com.example.testapi.model.DataItem
import com.example.testapi.my_interface.ItemClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.util.Locale

class NewCoinApdater(private var lDataItems: List<DataItem>, private var itemClickListener: ItemClickListener) : RecyclerView.Adapter<NewCoinApdater.NewCoinViewHolder>(){

    inner class NewCoinViewHolder(private val binding: ItemCoinBinding) : RecyclerView.ViewHolder(binding.root) {
        val mIconCoin : ImageView = binding.watchlistImg
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            binding.watchlistSymbol.text = dataItem.symbol
            val formattedPriceString = String.format(Locale.US, "%.9f", dataItem.quote.usd.price)
            var bigDecimal = BigDecimal(formattedPriceString)

            if (bigDecimal < BigDecimal("0.00000001")) {
                bigDecimal = bigDecimal.multiply(BigDecimal("1000"))
                binding.watchlistSymbol.text = "1000"+ dataItem.symbol
            }
            val decimalFormat = DecimalFormat("#.########")
            val formattedPrice = decimalFormat.format(bigDecimal)

            binding.watchlistPrice.text = formattedPrice
            val loadImage = LoadImage(mIconCoin)
            loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/${dataItem.id}.png")

            binding.itemCoin.setOnClickListener{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewCoinViewHolder {
        val view = ItemCoinBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewCoinViewHolder(view)
    }


    override fun onBindViewHolder(holder : NewCoinViewHolder, position: Int) {
        val dataItem  = lDataItems[position]
        holder.bind(dataItem)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<DataItem>) {
        lDataItems = newDataList
        notifyDataSetChanged()
    }

}