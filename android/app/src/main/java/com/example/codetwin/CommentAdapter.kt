package com.example.codetwin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codetwin.databinding.ItemCommentBinding
import com.example.codetwin.model.Comment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.example.codetwin.utils.TimeUtils

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
        val username = comment.user?.username ?: "Anonymous"
        holder.binding.tvCommentUsername.text = username
        holder.binding.tvCommentContent.text = comment.content
        holder.binding.tvCommentTime.text = TimeUtils.getRelativeTime(comment.createdAt)

        // Avatar
        val firstChar = username.firstOrNull()?.uppercaseChar() ?: '?'
        holder.binding.tvCommentAvatar.text = firstChar.toString()
        val bg = holder.binding.tvCommentAvatar.background as? GradientDrawable
        val colors = intArrayOf(
            Color.parseColor("#FF5722"), Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
            Color.parseColor("#009688"), Color.parseColor("#4CAF50")
        )
        bg?.setColor(colors[username.hashCode().coerceAtLeast(0) % colors.size])
    }

    override fun getItemCount() = comments.size
}