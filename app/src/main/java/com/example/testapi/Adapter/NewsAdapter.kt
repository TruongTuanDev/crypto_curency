package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemNewsBinding
import com.example.testapi.model.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.time.Instant
import java.util.TimeZone

class NewsAdapter(private var lNewsItems: List<NewsModel>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        val newsImage : ImageView = binding.newsImg

        fun bind(newsItem: NewsModel) {
            val unixTimestamp = newsItem.published_on.toLong()
            val instant = Instant.ofEpochSecond(unixTimestamp)
            val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh").toZoneId()
            val dateTimeInVietnam = instant.atZone(vietnamTimeZone)
            val year = dateTimeInVietnam.year
            val month = dateTimeInVietnam.monthValue
            val dayOfMonth = dateTimeInVietnam.dayOfMonth
            val hour = dateTimeInVietnam.hour
            val minute = dateTimeInVietnam.minute
            val second = dateTimeInVietnam.second
            val day : String = "$year-$month-$dayOfMonth $hour:$minute:$second"
            binding.newsTitle.text = newsItem.title
            binding.newsDay.text = day
            val loadImage = LoadImage(newsImage)
            loadImage.loadImage(newsItem.imageurl)

            binding.root.setOnClickListener {
                val position :Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dataItem = lNewsItems[position]
                    val href = dataItem.url
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(href))
                    binding.root.context.startActivity(browserIntent)
                } else {
                    Log.d("NewsAdapter", "onClick: NO_POSITION")
                }
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
        return lNewsItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewsViewHolder(view)
    }


    override fun onBindViewHolder(holder : NewsViewHolder, position: Int) {
        val dataItem  = lNewsItems[position]
        holder.bind(dataItem)

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<NewsModel>) {
        lNewsItems = newDataList
        notifyDataSetChanged()
    }

}