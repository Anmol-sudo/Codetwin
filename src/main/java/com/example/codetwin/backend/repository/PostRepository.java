package com.example.codetwin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.codetwin.backend.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
}
