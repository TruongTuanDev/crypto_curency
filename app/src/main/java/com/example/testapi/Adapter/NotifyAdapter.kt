package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemNotificationBinding
import com.example.testapi.model.Notification

class NotifyAdapter(private var lDataNotifys : List<Notification>) : RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder>() {
    inner class NotifyViewHolder(private val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        val title : TextView = binding.notificationTitle
        val description : TextView = binding.notificationDescription

        fun bind(notification: Notification){
            title.text = notification.title
            description.text = notification.description
            binding.notificationBack.setOnClickListener{
            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotifyViewHolder {
        val view = ItemNotificationBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return NotifyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lDataNotifys.size
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val dataNotification = lDataNotifys[position]
        holder.bind(dataNotification)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<Notification>) {
        lDataNotifys = newDataList
        notifyDataSetChanged()
    }

}