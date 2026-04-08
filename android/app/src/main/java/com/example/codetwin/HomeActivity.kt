package com.example.codetwin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import com.example.codetwin.utils.SessionManager
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.view.Menu
import android.view.MenuItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: ExtendedFloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var sessionManager: SessionManager

    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRecyclerView()
        setupListeners()

        loadPosts() // initial load
    }

    override fun onResume() {
        super.onResume()
        loadPosts() // 🔥 auto refresh when returning
    }

    // 🔧 Initialize views
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerPosts)
        fab = findViewById(R.id.fabAddPost)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sessionManager = SessionManager(this)
        swipeRefresh = findViewById(R.id.swipeRefresh)
    }

    // 🔧 Setup RecyclerView
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // 🔧 Setup Click Listeners
    private fun setupListeners() {

        fab.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        swipeRefresh.setOnRefreshListener {
            loadPosts()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                sessionManager.clearSession()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 🔥 API Call separated
    fun loadPosts() {

        RetrofitClient.getClient(this).getPosts()
            .enqueue(object : Callback<ApiResponse<List<Post>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<Post>>>,
                    response: Response<ApiResponse<List<Post>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val apiResponse = response.body()!!

                        if (apiResponse.success) {
                            recyclerView.adapter = PostAdapter(apiResponse.data, this@HomeActivity)
                            showToast("Posts Loaded!")
                            swipeRefresh.isRefreshing = false
                        } else {
                            showToast(apiResponse.message)
                        }

                    } else {
                        showToast("Server Error")
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<Post>>>,
                    t: Throwable
                ) {
                    showToast("Error: ${t.message}")
                    swipeRefresh.isRefreshing = false
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}