package com.example.codetwin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.databinding.ActivityPostDetailBinding
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Comment
import com.example.codetwin.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.codetwin.utils.TimeUtils

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private var postId: Long = -1
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getLongExtra("POST_ID", -1)
        if (postId == -1L) {
            Toast.makeText(this, "Invalid Post ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        loadPostDetails()

        binding.swipeRefresh.setOnRefreshListener {
            loadPostDetails()
        }

        binding.btnSendComment.setOnClickListener {
            val content = binding.etComment.text.toString().trim()
            if (content.isNotEmpty()) {
                addComment(content)
            }
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
                    if (response.isSuccessful) {
                        val post = response.body()?.data
                        post?.let {
                            displayPost(it)
                        }
                    } else {
                        Toast.makeText(this@PostDetailActivity, "Error loading post", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this@PostDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayPost(post: Post) {
        binding.tvTitle.text = post.title
        binding.tvContent.text = post.content
        binding.tvUsername.text = post.user?.username ?: "Anonymous"
        binding.tvTime.text = TimeUtils.getRelativeTime(post.createdAt)
        commentAdapter.updateComments(post.comments)
    }

    private fun addComment(content: String) {
        val commentRequest = Comment(content = content)
        RetrofitClient.getClient(this).addComment(postId, commentRequest)
            .enqueue(object : Callback<ApiResponse<Comment>> {
                override fun onResponse(call: Call<ApiResponse<Comment>>, response: Response<ApiResponse<Comment>>) {
                    if (response.isSuccessful) {
                        binding.etComment.text.clear()
                        loadPostDetails() // Refresh to see the new comment
                    } else {
                        Toast.makeText(this@PostDetailActivity, "Error adding comment", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Comment>>, t: Throwable) {
                    Toast.makeText(this@PostDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}