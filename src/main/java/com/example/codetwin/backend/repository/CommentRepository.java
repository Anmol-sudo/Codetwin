package com.example.codetwin.backend.repository;

import com.example.codetwin.backend.model.Comment;
import com.example.codetwin.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}