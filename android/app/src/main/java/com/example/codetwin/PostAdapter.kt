package com.example.codetwin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.databinding.ItemPostBinding
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.codetwin.utils.TimeUtils
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.bumptech.glide.Glide

class PostAdapter(private var posts: List<Post>, private val context: Context) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val sessionManager = com.example.codetwin.utils.SessionManager(context)
    private val currentUserId = sessionManager.getUserId()

    fun updatePosts(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.tvTitle.text = post.title
        holder.binding.tvContent.text = post.content
        val username = post.user?.username ?: "Anonymous"
        holder.binding.tvUsername.text = username
        holder.binding.tvTime.text = TimeUtils.getRelativeTime(post.createdAt)

        // Post Image
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.binding.cvPostImage.visibility = View.VISIBLE
            Glide.with(context)
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.binding.ivPostImage)
        } else {
            holder.binding.cvPostImage.visibility = View.GONE
        }

        // Generate Avatar
        val firstChar = username.firstOrNull()?.uppercaseChar() ?: '?'
        holder.binding.tvAvatar.text = firstChar.toString()
        val bg = holder.binding.tvAvatar.background as? GradientDrawable
        val colors = intArrayOf(
            Color.parseColor("#FF5722"), Color.parseColor("#E91E63"), 
            Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"), 
            Color.parseColor("#009688"), Color.parseColor("#4CAF50")
        )
        bg?.setColor(colors[username.hashCode().coerceAtLeast(0) % colors.size])

        val likeCount = post.likes.size
        holder.binding.btnLike.text = if (likeCount > 0) "$likeCount" else "Like"

        // Check if current user liked this post
        val isLiked = post.likes.any { it.user?.id == currentUserId }
        if (isLiked) {
            holder.binding.btnLike.setIconResource(android.R.drawable.btn_star_big_on)
            holder.binding.btnLike.setTextColor(context.getColor(R.color.primary))
            holder.binding.btnLike.iconTint = android.content.res.ColorStateList.valueOf(context.getColor(R.color.primary))
        } else {
            holder.binding.btnLike.setIconResource(android.R.drawable.btn_star_big_off)
            holder.binding.btnLike.setTextColor(context.getColor(R.color.textSecondary))
            holder.binding.btnLike.iconTint = android.content.res.ColorStateList.valueOf(context.getColor(R.color.textSecondary))
        }
        
        val commentCount = post.comments.size
        holder.binding.btnComment.text = if (commentCount > 0) "$commentCount" else "Comment"

        holder.binding.btnLike.setOnClickListener {
            toggleLike(post.id, position)
        }

        holder.binding.btnComment.setOnClickListener {
            showCommentDialog(post.id)
        }

        holder.binding.tvAvatar.setOnClickListener {
            val intent = android.content.Intent(context, ProfileActivity::class.java)
            intent.putExtra("USER_ID", post.user?.id)
            context.startActivity(intent)
        }

        holder.binding.tvUsername.setOnClickListener {
            val intent = android.content.Intent(context, ProfileActivity::class.java)
            intent.putExtra("USER_ID", post.user?.id)
            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(context, PostDetailActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            context.startActivity(intent)
        }
    }

    private fun showCommentDialog(postId: Long?) {
        if (postId == null) return
        
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Add Comment")

        val input = android.widget.EditText(context)
        input.hint = "Write a comment..."
        val lp = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        input.layoutParams = lp
        
        val container = android.widget.FrameLayout(context)
        val params = android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 50
        params.rightMargin = 50
        container.addView(input, params)
        
        builder.setView(container)

        builder.setPositiveButton("Post") { dialog, _ ->
            val commentText = input.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(postId, commentText)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addComment(postId: Long, content: String) {
        val commentRequest = com.example.codetwin.model.Comment(content = content)
        RetrofitClient.getClient(context).addComment(postId, commentRequest)
            .enqueue(object : Callback<ApiResponse<com.example.codetwin.model.Comment>> {
                override fun onResponse(
                    call: Call<ApiResponse<com.example.codetwin.model.Comment>>,
                    response: Response<ApiResponse<com.example.codetwin.model.Comment>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Comment added!", Toast.LENGTH_SHORT).show()
                        if (context is HomeActivity) {
                            context.loadPosts()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<com.example.codetwin.model.Comment>>, t: Throwable) {
                    Toast.makeText(context, "Error adding comment", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun toggleLike(postId: Long?, position: Int) {
        if (postId == null) return
        
        RetrofitClient.getClient(context).toggleLike(postId)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        // Refresh posts to show updated like status
                        if (context is HomeActivity) {
                            context.loadPosts()
                        }
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(context, "Error liking post", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getItemCount() = posts.size
}