package com.derteuffel.solutionafrica.repositories;

import com.derteuffel.solutionafrica.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findAllByBoutique_Id(Long id);
}
