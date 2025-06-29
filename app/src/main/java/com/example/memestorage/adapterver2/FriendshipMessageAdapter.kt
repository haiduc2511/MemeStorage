package com.example.memestorage.adapterver2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemMessageBinding
import com.example.memestorage.test.model.UserFriendshipMessageModel

class FriendshipMessageAdapter(
    private val items: List<UserFriendshipMessageModel>
) : RecyclerView.Adapter<FriendshipMessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvMessage.text = "${item.sender}: ${item.message}"
    }

    override fun getItemCount(): Int = items.size
}
