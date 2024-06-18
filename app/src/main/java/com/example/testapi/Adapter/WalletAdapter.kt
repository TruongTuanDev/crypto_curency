package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.R
import com.example.testapi.databinding.ItemWalletBinding
import com.example.testapi.model.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL

class WalletAdapter(private var coinList: List<Wallet>) : RecyclerView.Adapter<WalletAdapter.CoinViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val view = ItemWalletBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoinViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = coinList[position]
        holder.bind(coin)
    }

    override fun getItemCount() = coinList.size

    inner class CoinViewHolder(private val binding: ItemWalletBinding) : RecyclerView.ViewHolder(binding.root) {
        private val coinImage: ImageView = binding.ivCoinImage
        private val coinName : TextView = binding.tvCoinName
        private val coinQuantity: TextView = binding.tvCoinQuantity
        private val totalVND: TextView = binding.tvTotalVND

        @SuppressLint("SetTextI18n")
        fun bind(coin: Wallet) {
            Log.e("Coiiiii",coin.toString())
            totalVND.text =  String.format("%.4f", coin.quantity_curency?.times(coin.price)) + " USD"
            binding.tvCoinName.text = coin.name
            val formattedPrice = String.format("%.9f", coin.quantity_curency)
            binding.tvCoinQuantity.text = formattedPrice +"  "+ coin.symbol
            val loadImage = LoadImage(coinImage)
            loadImage.loadImage("https://s2.coinmarketcap.com/static/img/coins/64x64/${coin.id}.png")
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
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<Wallet>) {
        coinList = newDataList
        notifyDataSetChanged()
    }
}
