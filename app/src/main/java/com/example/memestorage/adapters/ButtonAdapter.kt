package com.example.memestorage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemButtonBinding

class ButtonAdapter(
    private val items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    inner class ButtonViewHolder(val binding: ItemButtonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val binding = ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ButtonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val item = items[position]
        holder.binding.btnItem.text = item
        holder.binding.btnItem.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}