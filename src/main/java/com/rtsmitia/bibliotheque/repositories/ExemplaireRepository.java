package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {

    /**
     * Find all exemplaires for a specific book
     */
    List<Exemplaire> findByLivre(Livre livre);

    /**
     * Find all exemplaires for a specific book by book ID
     */
    List<Exemplaire> findByLivreId(Long livreId);

    /**
     * Count exemplaires for a specific book
     */
    long countByLivreId(Long livreId);

    /**
     * Find available exemplaires (not currently borrowed)
     * This query checks for exemplaires that don't have active loans
     */
    @Query("SELECT e FROM Exemplaire e WHERE e.livre.id = :livreId " +
           "AND e.id NOT IN (SELECT p.exemplaire.id FROM Pret p WHERE p.dateRetour IS NULL)")
    List<Exemplaire> findAvailableByLivreId(@Param("livreId") Long livreId);

    /**
     * Find borrowed exemplaires (currently on loan)
     */
    @Query("SELECT e FROM Exemplaire e WHERE e.livre.id = :livreId " +
           "AND e.id IN (SELECT p.exemplaire.id FROM Pret p WHERE p.dateRetour IS NULL)")
    List<Exemplaire> findBorrowedByLivreId(@Param("livreId") Long livreId);

    /**
     * Check if an exemplaire is currently borrowed
     */
    @Query("SELECT COUNT(p) > 0 FROM Pret p WHERE p.exemplaire.id = :exemplaireId AND p.dateRetour IS NULL")
    boolean isCurrentlyBorrowed(@Param("exemplaireId") Long exemplaireId);

    /**
     * Find all exemplaires ordered by arrival date
     */
    List<Exemplaire> findAllByOrderByDateArriveeDesc();

    /**
     * Find exemplaires by book title (for search functionality)
     */
    @Query("SELECT e FROM Exemplaire e WHERE LOWER(e.livre.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Exemplaire> findByLivreTitreContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}
