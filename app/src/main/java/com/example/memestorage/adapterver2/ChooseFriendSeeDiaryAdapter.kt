package com.example.memestorage.adapterver2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemChooseFriendSeeDiaryBinding
import com.example.memestorage.test.model.UserFriendshipModel

class ChooseFriendSeeDiaryAdapter(
    private val items: List<UserFriendshipModel>,
    private val onFriendSelected: (UserFriendshipModel, Boolean) -> Unit
) : RecyclerView.Adapter<ChooseFriendSeeDiaryAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ItemChooseFriendSeeDiaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemChooseFriendSeeDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvFriendName.text = item.user2Id ?: item.user1Id
        holder.binding.cbSelectFriend.setOnCheckedChangeListener { _, isChecked ->
            onFriendSelected(item, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size
}
