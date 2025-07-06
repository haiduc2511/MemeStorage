package com.example.memestorage.adapterver2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemUserFriendDiaryMediaBinding
import com.example.memestorage.test.model.UserFriendDiaryMediaModel

class UserFriendDiaryMediaAdapter(
    private val items: List<UserFriendDiaryMediaModel>
) : RecyclerView.Adapter<UserFriendDiaryMediaAdapter.UserFriendDiaryMediaViewHolder>() {

    inner class UserFriendDiaryMediaViewHolder(val binding: ItemUserFriendDiaryMediaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFriendDiaryMediaViewHolder {
        val binding = ItemUserFriendDiaryMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFriendDiaryMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFriendDiaryMediaViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvUserId.text = "User ID: ${item.userId}"
        holder.binding.tvUserFriendshipId.text = "time: ${item.time}"
        holder.binding.tvUserDiaryMediaId.text = "Diary Media ID: ${item.userDiaryMediaId}"
    }

    override fun getItemCount(): Int = items.size
}
