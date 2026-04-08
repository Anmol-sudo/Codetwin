package com.example.codetwin.backend.repository;

import com.example.codetwin.backend.model.Like;
import com.example.codetwin.backend.model.Post;
import com.example.codetwin.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
}