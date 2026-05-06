package com.example.codetwin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.codetwin.backend.model.Post;
import com.example.codetwin.backend.model.User;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    long countByUser(User user);
    List<Post> findByUserOrderByCreatedAtDesc(User user);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(String title, String content);
    List<Post> findAllByOrderByCreatedAtDesc();
}
