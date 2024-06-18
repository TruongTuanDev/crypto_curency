package com.example.testapi.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.databinding.ItemListFeedbackBinding
import com.example.testapi.model.FeedBack

class FeedbackAdapter(private var lDataFeedbacks : List<FeedBack>) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    inner class FeedbackViewHolder(binding : ItemListFeedbackBinding) : RecyclerView.ViewHolder(binding.root){
        val time : TextView = binding.timefeedback
        val description : TextView = binding.descfeedback
        val title : TextView = binding.titlefeedback

        fun bind(feedBack: FeedBack){
            title.text = feedBack.title
            description.text = feedBack.description
            time.text = feedBack.date
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FeedbackViewHolder {
        val view = ItemListFeedbackBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return FeedbackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lDataFeedbacks.size
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val dataFeedback = lDataFeedbacks[position]
        holder.bind(dataFeedback)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataList: List<FeedBack>) {
        lDataFeedbacks = newDataList
        notifyDataSetChanged()
    }

}