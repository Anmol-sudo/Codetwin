package com.example.codetwin

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etContent: TextInputEditText
    private lateinit var btnPost: MaterialButton
    private lateinit var toolbar: Toolbar
    
    private lateinit var cvImagePreview: MaterialCardView
    private lateinit var ivPreview: ImageView
    private lateinit var btnRemoveImage: ImageButton
    private lateinit var btnAddImage: MaterialButton
    
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            ivPreview.setImageURI(uri)
            cvImagePreview.visibility = View.VISIBLE
            btnAddImage.visibility = View.GONE
        }
    }

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
        
        cvImagePreview = findViewById(R.id.cvImagePreview)
        ivPreview = findViewById(R.id.ivPreview)
        btnRemoveImage = findViewById(R.id.btnRemoveImage)
        btnAddImage = findViewById(R.id.btnAddImage)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            cvImagePreview.visibility = View.GONE
            btnAddImage.visibility = View.VISIBLE
        }

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadPost(title, content)
        }
    }

    private fun uploadPost(title: String, content: String) {
        btnPost.isEnabled = false
        
        val titleRB = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentRB = content.toRequestBody("text/plain".toMediaTypeOrNull())
        
        var imagePart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val file = getFileFromUri(uri)
            if (file != null) {
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            }
        }

        RetrofitClient.getClient(this).createPost(titleRB, contentRB, imagePart)
            .enqueue(object : Callback<ApiResponse<Post>> {
                override fun onResponse(call: Call<ApiResponse<Post>>, response: Response<ApiResponse<Post>>) {
                    btnPost.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CreatePostActivity, "Posted Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreatePostActivity, "Error: ${response.body()?.message ?: "Server Error"}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    btnPost.isEnabled = true
                    Toast.makeText(this@CreatePostActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getFileFromUri(uri: Uri): File? {
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
