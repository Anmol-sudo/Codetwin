package com.example.codetwin.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.codetwin.backend.dto.ApiResponse;
import com.example.codetwin.backend.model.Comment;
import com.example.codetwin.backend.model.Post;
import com.example.codetwin.backend.service.PostService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/posts")
public class PostController {
  private final PostService postService;
  
  public PostController(PostService postService) {
    this.postService = postService;
  }


  

  // Create Post
@PostMapping
public ResponseEntity<ApiResponse<Post>> createPost(
        @RequestBody Post post,
        Principal principal) {
    System.out.println("Principal: " + principal);      
    String email = principal.getName();

    try {
      Post savedPost = postService.createPost(post, email);

      return ResponseEntity.ok(
          new ApiResponse<>(
                  true,
                  "Post created successfully",
                  savedPost
          )
      );
    } catch (Exception e) {
        e.printStackTrace();

        return ResponseEntity.badRequest().body(
            new ApiResponse<>(false, e.getMessage(), null)
        );
    }
}

  // Get All Posts
@GetMapping
public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {

    List<Post> posts = postService.getAllPosts();

    return ResponseEntity.ok(
        new ApiResponse<>(
                true,
                "Posts fetched successfully",
                posts
        )
    );
}

// Toggle Like
@PostMapping("/{id}/like")
public ResponseEntity<ApiResponse<Void>> toggleLike(
        @PathVariable Long id,
        Principal principal) {
    try {
        postService.toggleLike(id, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Toggled like", null));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
    }
}

// Add Comment
@PostMapping("/{id}/comment")
public ResponseEntity<ApiResponse<Comment>> addComment(
        @PathVariable Long id,
        @RequestBody Comment comment,
        Principal principal) {
    try {
        Comment savedComment = postService.addComment(id, principal.getName(), comment.getContent());
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment added", savedComment));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
    }
}
  
} 
