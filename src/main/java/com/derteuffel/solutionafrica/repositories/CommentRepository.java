package com.derteuffel.solutionafrica.repositories;

import com.derteuffel.solutionafrica.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost_IdOrderByIdDesc(Long id);
    List<Comment> findAllByPost_IdOrderByIdAsc(Long id);
}
