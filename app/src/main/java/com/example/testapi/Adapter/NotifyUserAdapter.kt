package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemNotificationBinding
import com.example.testapi.model.Notification

class NotifyUserAdapter(private var lDataNotifyUsers : List<Notification>) : RecyclerView.Adapter<NotifyUserAdapter.NotifyUserViewHolder>() {
    inner class NotifyUserViewHolder(private val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        val title : TextView = binding.notificationTitle
        val description : TextView = binding.notificationDescription

        fun bind(notification: Notification){
            title.text = notification.title
            description.text = notification.description
            binding.notificationBack.setOnClickListener{
            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotifyUserViewHolder {
        val view = ItemNotificationBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return NotifyUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lDataNotifyUsers.size
    }

    override fun onBindViewHolder(holder: NotifyUserViewHolder, position: Int) {
        val dataNotification = lDataNotifyUsers[position]
        holder.bind(dataNotification)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<Notification>) {
        lDataNotifyUsers = newDataList
        notifyDataSetChanged()
    }

}