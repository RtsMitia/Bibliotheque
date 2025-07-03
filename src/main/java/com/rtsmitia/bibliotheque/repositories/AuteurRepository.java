package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Auteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {

    // Find authors by name (case insensitive)
    List<Auteur> findByNomContainingIgnoreCase(String nom);

    // Find author by exact name
    Auteur findByNom(String nom);

    // Check if author exists by name
    boolean existsByNom(String nom);

    // Find authors with books
    @Query("SELECT DISTINCT a FROM Auteur a JOIN a.livres l")
    List<Auteur> findAuthorsWithBooks();

    // Count books by author
    @Query("SELECT COUNT(l) FROM Livre l WHERE l.auteur = :auteur")
    long countBooksByAuthor(@Param("auteur") Auteur auteur);

    // Find authors ordered by name
    List<Auteur> findAllByOrderByNomAsc();
}
