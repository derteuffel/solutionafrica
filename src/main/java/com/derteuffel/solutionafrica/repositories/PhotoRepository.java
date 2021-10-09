package com.derteuffel.solutionafrica.repositories;

import com.derteuffel.solutionafrica.entities.Photo;
import com.derteuffel.solutionafrica.enums.PhotoCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByTypeAndUser_Id(String type, Long id);
    List<Photo> findAllByType(String type);
    Page<Photo> findAllByCategoryAndTypeAndStatusOrderByIdDesc(Pageable pageable, String category, String type, Boolean status);
    Page<Photo> findAllByTypeAndStatusOrderByIdDesc(Pageable pageable, String type, Boolean status);
}
