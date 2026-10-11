package com.makartos.systemui_logger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makartos.systemui_logger.databinding.ItemLogBinding

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private val logs = mutableListOf<String>()

    fun addLog(log: String) {
        logs.add(log)
        notifyItemInserted(logs.size - 1)
    }

    fun getLogs(): List<String> = logs.toList()

    fun clear() {
        logs.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount() = logs.size

    class LogViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: String) {
            binding.logText.text = log
            binding.logText.textSize = 10f
        }
    }
}
