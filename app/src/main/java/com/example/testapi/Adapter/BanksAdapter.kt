package com.example.testapi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.testapi.model.Banks
import com.example.testapi.model.DataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.URL

class BanksAdapter(context: Context, private var banks: List<Banks>) : ArrayAdapter<Banks>(context, 0, banks) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup
    ): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    @SuppressLint("ResourceType")
    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bank, parent, false)
        }

        val imageView = view!!.findViewById<ImageView>(R.id.bank_image)
        val textView = view.findViewById<TextView>(R.id.bank_short_name)

        val bank = banks[position]

        val loadImage = LoadImage(imageView)
        loadImage.loadImage(bank.logo)

        textView.text = bank.shortName
        return view
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
    fun updateData(newDataList: List<Banks>) {
        banks = newDataList
        notifyDataSetChanged()
    }
}
