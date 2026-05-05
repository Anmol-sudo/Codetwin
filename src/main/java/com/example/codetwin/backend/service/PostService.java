package com.example.codetwin.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.codetwin.backend.model.Comment;
import com.example.codetwin.backend.model.Like;
import com.example.codetwin.backend.model.Post;
import com.example.codetwin.backend.model.User;
import com.example.codetwin.backend.repository.CommentRepository;
import com.example.codetwin.backend.repository.LikeRepository;
import com.example.codetwin.backend.repository.PostRepository;
import com.example.codetwin.backend.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

@Service
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FileStorageService fileStorageService;

    public PostService(PostRepository postRepository, UserRepository userRepository, 
                       LikeRepository likeRepository, CommentRepository commentRepository,
                       FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.fileStorageService = fileStorageService;
    }

    public Post createPost(String title, String content, MultipartFile image, String email) {

        if (title == null || title.isEmpty()) {
            throw new RuntimeException("Title cannot be empty");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(image);
            post.setImageUrl(imageUrl);
        }

        return postRepository.save(post);
    }

    // Get All Posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Like/Unlike Post
    public void toggleLike(Long postId, String email) {
        Post post = getPostById(postId);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    // Add Comment
    public Comment addComment(Long postId, String email, String content) {
        Post post = getPostById(postId);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }
}
