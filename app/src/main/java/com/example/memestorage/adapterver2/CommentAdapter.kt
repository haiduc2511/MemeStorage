package com.example.memestorage.adapterver2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memestorage.databinding.ItemCommentBinding
import com.example.memestorage.test.model.MediaCommentModel

class CommentAdapter(
    private val comments: List<MediaCommentModel>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.tvComment.text = comments[position].comment
    }

    override fun getItemCount(): Int = comments.size
}
