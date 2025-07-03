package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.models.Auteur;
import com.rtsmitia.bibliotheque.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    // Find books by title (case insensitive)
    List<Livre> findByTitreContainingIgnoreCase(String titre);

    // Find books by author
    List<Livre> findByAuteur(Auteur auteur);

    // Find books by author ID
    List<Livre> findByAuteurId(Long auteurId);

    // Find books published after a certain date
    @Query("SELECT l FROM Livre l WHERE l.dateSortie >= :date")
    List<Livre> findBooksPublishedAfter(@Param("date") LocalDateTime date);

    // Find books published before a certain date
    @Query("SELECT l FROM Livre l WHERE l.dateSortie <= :date")
    List<Livre> findBooksPublishedBefore(@Param("date") LocalDateTime date);

    // Find books by genre
    @Query("SELECT l FROM Livre l JOIN l.genres g WHERE g = :genre")
    List<Livre> findByGenre(@Param("genre") Genre genre);

    // Find books by genre name
    @Query("SELECT l FROM Livre l JOIN l.genres g WHERE g.libelle = :genreLibelle")
    List<Livre> findByGenreName(@Param("genreLibelle") String genreLibelle);

    // Find books that have available exemplaires
    @Query("SELECT DISTINCT l FROM Livre l JOIN l.exemplaires e WHERE e.id NOT IN " +
           "(SELECT p.exemplaire.id FROM Pret p WHERE p.dateRetour IS NULL)")
    List<Livre> findBooksWithAvailableExemplaires();

    // Search books by title or author name
    @Query("SELECT l FROM Livre l WHERE LOWER(l.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(l.auteur.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Livre> searchByTitleOrAuthor(@Param("searchTerm") String searchTerm);

    // Count books by author
    long countByAuteur(Auteur auteur);

    // Find books published in a date range
    @Query("SELECT l FROM Livre l WHERE l.dateSortie BETWEEN :startDate AND :endDate")
    List<Livre> findBooksPublishedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
