package com.example.codetwin

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.databinding.ActivityPostDetailBinding
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Comment
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

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private var postId: Long = -1
    private lateinit var commentAdapter: CommentAdapter
    
    private val markwon by lazy {
        Markwon.builder(this)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: io.noties.markwon.core.MarkwonTheme.Builder) {
                    builder.codeBlockBackgroundColor(Color.parseColor("#F5F5F5"))
                        .codeBlockTextColor(Color.parseColor("#00796B"))
                        .codeTypeface(android.graphics.Typeface.MONOSPACE)
                        .codeBlockMargin(16)
                }
            })
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getLongExtra("POST_ID", -1)
        if (postId == -1L) {
            finish()
            return
        }

        setupRecyclerView()
        loadPostDetails()

        binding.swipeRefresh.setOnRefreshListener { loadPostDetails() }
        binding.btnSendComment.setOnClickListener {
            val content = binding.etComment.text.toString().trim()
            if (content.isNotEmpty()) addComment(content)
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(emptyList())
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.adapter = commentAdapter
    }

    private fun loadPostDetails() {
        binding.swipeRefresh.isRefreshing = true
        RetrofitClient.getClient(this).getPostById(postId)
            .enqueue(object : Callback<ApiResponse<Post>> {
                override fun onResponse(call: Call<ApiResponse<Post>>, response: Response<ApiResponse<Post>>) {
                    binding.swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) response.body()?.data?.let { displayPost(it) }
                }
                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    binding.swipeRefresh.isRefreshing = false
                }
            })
    }

    private fun displayPost(post: Post) {
        binding.tvTitle.text = post.title
        markwon.setMarkdown(binding.tvContent, post.content)
        binding.tvUsername.text = post.user?.username ?: "Anonymous"
        binding.tvTime.text = TimeUtils.getRelativeTime(post.createdAt)

        if (!post.imageUrl.isNullOrEmpty()) {
            binding.cvPostImage.visibility = View.VISIBLE
            val fullImageUrl = if (post.imageUrl.startsWith("http")) post.imageUrl else "http://10.0.2.2:8080" + post.imageUrl
            Glide.with(this).load(fullImageUrl).placeholder(R.drawable.ic_launcher_background).into(binding.ivPostImage)
        } else {
            binding.cvPostImage.visibility = View.GONE
        }
        commentAdapter.updateComments(post.comments)
    }

    private fun addComment(content: String) {
        val commentRequest = Comment(content = content)
        RetrofitClient.getClient(this).addComment(postId, commentRequest)
            .enqueue(object : Callback<ApiResponse<Comment>> {
                override fun onResponse(call: Call<ApiResponse<Comment>>, response: Response<ApiResponse<Comment>>) {
                    if (response.isSuccessful) {
                        binding.etComment.text?.clear()
                        loadPostDetails()
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Comment>>, t: Throwable) {}
            })
    }
}
