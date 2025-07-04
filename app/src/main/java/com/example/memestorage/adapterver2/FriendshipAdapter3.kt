package com.example.memestorage.adapterver2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemFriendship2Binding
import com.example.memestorage.databinding.ItemFriendshipBinding
import com.example.memestorage.test.model.UserFriendshipModel

class FriendshipAdapter3(
    private val items: List<UserFriendshipModel>,
    private val onItemClick: (UserFriendshipModel) -> Unit
) : RecyclerView.Adapter<FriendshipAdapter3.FriendshipViewHolder>() {

    inner class FriendshipViewHolder(val binding: ItemFriendship2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipViewHolder {
        val binding = ItemFriendship2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendshipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendshipViewHolder, position: Int) {
        val item = items[position]
        holder.binding.friendNameTextView.text = "Friend: ${item.user2Id ?: item.user1Id}"

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}