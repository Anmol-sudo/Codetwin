package com.example.codetwin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.databinding.ItemPostBinding
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import com.example.codetwin.utils.TimeUtils
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.Prop
import org.commonmark.node.FencedCodeBlock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter(private var posts: List<Post>, private val context: Context) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val sessionManager = com.example.codetwin.utils.SessionManager(context)
    private val currentUserId = sessionManager.getUserId()
    
    private val markwon = Markwon.builder(context)
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: io.noties.markwon.core.MarkwonTheme.Builder) {
                builder.codeBlockBackgroundColor(Color.parseColor("#F5F5F5"))
                    .codeBlockTextColor(Color.parseColor("#00796B"))
                    .codeTypeface(android.graphics.Typeface.MONOSPACE)
                    .codeBlockMargin(16) // Added margin for better spacing
            }
        })
        .build()

    fun updatePosts(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.tvTitle.text = post.title
        markwon.setMarkdown(holder.binding.tvContent, post.content)

        val username = post.user?.username ?: "Anonymous"
        holder.binding.tvUsername.text = username
        holder.binding.tvTime.text = TimeUtils.getRelativeTime(post.createdAt)

        if (!post.imageUrl.isNullOrEmpty()) {
            holder.binding.cvPostImage.visibility = View.VISIBLE
            val fullImageUrl = if (post.imageUrl.startsWith("http")) post.imageUrl else "http://10.0.2.2:8080" + post.imageUrl
            Glide.with(context).load(fullImageUrl).placeholder(R.drawable.ic_launcher_background).into(holder.binding.ivPostImage)
        } else {
            holder.binding.cvPostImage.visibility = View.GONE
        }

        val firstChar = username.firstOrNull()?.uppercaseChar() ?: '?'
        holder.binding.tvAvatar.text = firstChar.toString()
        val bg = holder.binding.tvAvatar.background as? GradientDrawable
        val colors = intArrayOf(Color.parseColor("#FF5722"), Color.parseColor("#E91E63"), Color.parseColor("#9C27B0"))
        bg?.setColor(colors[username.hashCode().coerceAtLeast(0) % colors.size])

        val likeCount = post.likes.size
        holder.binding.btnLike.text = if (likeCount > 0) "$likeCount" else "Like"

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
        
        holder.binding.btnComment.text = if (post.comments.size > 0) "${post.comments.size}" else "Comment"
        holder.binding.btnLike.setOnClickListener { toggleLike(post.id, position) }
        holder.binding.btnComment.setOnClickListener { showCommentDialog(post.id) }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java)
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
        val container = android.widget.FrameLayout(context)
        val params = android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(50, 20, 50, 20)
        container.addView(input, params)
        builder.setView(container)
        builder.setPositiveButton("Post") { dialog, _ ->
            if (input.text.isNotEmpty()) addComment(postId, input.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun addComment(postId: Long, content: String) {
        val commentRequest = com.example.codetwin.model.Comment(content = content)
        RetrofitClient.getClient(context).addComment(postId, commentRequest)
            .enqueue(object : Callback<ApiResponse<com.example.codetwin.model.Comment>> {
                override fun onResponse(call: Call<ApiResponse<com.example.codetwin.model.Comment>>, response: Response<ApiResponse<com.example.codetwin.model.Comment>>) {
                    if (response.isSuccessful && context is HomeActivity) context.loadPosts()
                }
                override fun onFailure(call: Call<ApiResponse<com.example.codetwin.model.Comment>>, t: Throwable) {}
            })
    }

    private fun toggleLike(postId: Long?, position: Int) {
        if (postId == null) return
        RetrofitClient.getClient(context).toggleLike(postId)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful && context is HomeActivity) context.loadPosts()
                }
                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {}
            })
    }

    override fun getItemCount() = posts.size
}
