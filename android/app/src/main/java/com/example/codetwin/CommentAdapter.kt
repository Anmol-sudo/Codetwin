package com.example.codetwin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codetwin.databinding.ItemCommentBinding
import com.example.codetwin.model.Comment

class CommentAdapter(private var comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    fun updateComments(newComments: List<Comment>) {
        this.comments = newComments
        notifyDataSetChanged()
    }

    class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.binding.tvCommentUsername.text = comment.user?.username ?: "Anonymous"
        holder.binding.tvCommentContent.text = comment.content
        holder.binding.tvCommentTime.text = comment.createdAt?.substringBefore("T") ?: ""
    }

    override fun getItemCount() = comments.size
}