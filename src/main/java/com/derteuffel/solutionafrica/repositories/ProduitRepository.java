package com.derteuffel.solutionafrica.repositories;

import com.derteuffel.solutionafrica.entities.Produit;
import com.derteuffel.solutionafrica.enums.ProduitCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    List<Produit> findAllByCategory(ProduitCategories category);
    Page<Produit> findAllByCategory(ProduitCategories category, Pageable pageable);
    Page<Produit> findAllByCategoryAndBoutique_Id(ProduitCategories category, Long id, Pageable pageable);
    List<Produit> findAllByBoutique_Id(Long id);
    Page<Produit> findAllByBoutique_Id(Long id, Pageable pageable);
    Page<Produit> findAllByOrderByIdDesc(Pageable pageable);
}
