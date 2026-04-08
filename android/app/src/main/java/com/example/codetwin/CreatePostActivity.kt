package com.example.codetwin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etContent: TextInputEditText
    private lateinit var btnPost: MaterialButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        initViews()
        setupToolbar()
        setupListeners()
    }

    private fun initViews() {
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnPost = findViewById(R.id.btnPost)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = Post(title = title, content = content)
            createPost(post)
        }
    }

    private fun createPost(post: Post) {
        btnPost.isEnabled = false
        RetrofitClient.getClient(this).createPost(post)
            .enqueue(object : Callback<ApiResponse<Post>> {
                override fun onResponse(
                    call: Call<ApiResponse<Post>>,
                    response: Response<ApiResponse<Post>>
                ) {
                    btnPost.isEnabled = true
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!
                        if (apiResponse.success) {
                            Toast.makeText(this@CreatePostActivity, "Posted Successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreatePostActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CreatePostActivity, "Server Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    btnPost.isEnabled = true
                    Toast.makeText(this@CreatePostActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}