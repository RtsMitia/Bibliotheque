package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    // Find genres by libelle (case insensitive)
    List<Genre> findByLibelleContainingIgnoreCase(String libelle);

    // Find genre by exact libelle
    Genre findByLibelle(String libelle);

    // Check if genre exists by libelle
    boolean existsByLibelle(String libelle);

    // Find genres with books
    @Query("SELECT DISTINCT g FROM Genre g JOIN g.livres l")
    List<Genre> findGenresWithBooks();

    // Count books by genre
    @Query("SELECT COUNT(l) FROM Livre l JOIN l.genres g WHERE g = :genre")
    long countBooksByGenre(@Param("genre") Genre genre);

    // Find genres ordered by libelle
    List<Genre> findAllByOrderByLibelleAsc();

    // Find most popular genres (by book count)
    @Query("SELECT g FROM Genre g JOIN g.livres l GROUP BY g ORDER BY COUNT(l) DESC")
    List<Genre> findMostPopularGenres();
}
