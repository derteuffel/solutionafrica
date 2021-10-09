package com.derteuffel.solutionafrica.repositories;

import com.derteuffel.solutionafrica.entities.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCategoryOrderByIdDesc(String category);

}
