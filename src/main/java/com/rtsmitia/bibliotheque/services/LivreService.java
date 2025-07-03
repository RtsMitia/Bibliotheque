package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.models.Auteur;
import com.rtsmitia.bibliotheque.models.Genre;
import com.rtsmitia.bibliotheque.models.LesContraints;
import com.rtsmitia.bibliotheque.repositories.LivreRepository;
import com.rtsmitia.bibliotheque.repositories.AuteurRepository;
import com.rtsmitia.bibliotheque.repositories.GenreRepository;
import com.rtsmitia.bibliotheque.repositories.LesContraintsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LivreService {

    private final LivreRepository livreRepository;
    private final AuteurRepository auteurRepository;
    private final GenreRepository genreRepository;
    private final LesContraintsRepository contraintsRepository;

    @Autowired
    public LivreService(LivreRepository livreRepository, AuteurRepository auteurRepository, 
                       GenreRepository genreRepository, LesContraintsRepository contraintsRepository) {
        this.livreRepository = livreRepository;
        this.auteurRepository = auteurRepository;
        this.genreRepository = genreRepository;
        this.contraintsRepository = contraintsRepository;
    }

    /**
     * Create a new livre
     */
    public Livre createLivre(String titre, String resume, LocalDateTime dateSortie, Long auteurId, 
                            List<Long> genreIds, List<Long> contraintIds) {
        // Get the author
        Optional<Auteur> auteurOpt = auteurRepository.findById(auteurId);
        if (!auteurOpt.isPresent()) {
            throw new RuntimeException("Auteur not found with id: " + auteurId);
        }

        // Create the livre
        Livre livre = new Livre(titre, resume, dateSortie, auteurOpt.get());

        // Set genres if provided
        if (genreIds != null && !genreIds.isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(genreIds);
            livre.setGenres(genres);
        }

        // Set constraints if provided
        if (contraintIds != null && !contraintIds.isEmpty()) {
            List<LesContraints> contraintes = contraintsRepository.findAllById(contraintIds);
            livre.setContraintes(contraintes);
        }

        return livreRepository.save(livre);
    }

    /**
     * Save or update a livre
     */
    public Livre saveLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    /**
     * Get all livres
     */
    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    /**
     * Get livre by ID
     */
    public Optional<Livre> getLivreById(Long id) {
        return livreRepository.findById(id);
    }

    /**
     * Search livres by title
     */
    public List<Livre> searchByTitle(String titre) {
        return livreRepository.findByTitreContainingIgnoreCase(titre);
    }

    /**
     * Get livres by author
     */
    public List<Livre> getLivresByAuteur(Auteur auteur) {
        return livreRepository.findByAuteur(auteur);
    }

    /**
     * Get livres by author ID
     */
    public List<Livre> getLivresByAuteurId(Long auteurId) {
        return livreRepository.findByAuteurId(auteurId);
    }

    /**
     * Search livres by title or author name
     */
    public List<Livre> searchByTitleOrAuthor(String searchTerm) {
        return livreRepository.searchByTitleOrAuthor(searchTerm);
    }

    /**
     * Get livres by genre
     */
    public List<Livre> getLivresByGenre(Genre genre) {
        return livreRepository.findByGenre(genre);
    }

    /**
     * Get livres by genre name
     */
    public List<Livre> getLivresByGenreName(String genreLibelle) {
        return livreRepository.findByGenreName(genreLibelle);
    }

    /**
     * Get livres with available exemplaires
     */
    public List<Livre> getLivresWithAvailableExemplaires() {
        return livreRepository.findBooksWithAvailableExemplaires();
    }

    /**
     * Get livres published after a date
     */
    public List<Livre> getLivresPublishedAfter(LocalDateTime date) {
        return livreRepository.findBooksPublishedAfter(date);
    }

    /**
     * Get livres published before a date
     */
    public List<Livre> getLivresPublishedBefore(LocalDateTime date) {
        return livreRepository.findBooksPublishedBefore(date);
    }

    /**
     * Get livres published between two dates
     */
    public List<Livre> getLivresPublishedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return livreRepository.findBooksPublishedBetween(startDate, endDate);
    }

    /**
     * Count livres by author
     */
    public long countLivresByAuteur(Auteur auteur) {
        return livreRepository.countByAuteur(auteur);
    }

    /**
     * Update livre
     */
    public Livre updateLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    /**
     * Delete livre
     */
    public void deleteLivre(Long id) {
        livreRepository.deleteById(id);
    }

    /**
     * Get all auteurs for dropdown
     */
    public List<Auteur> getAllAuteurs() {
        return auteurRepository.findAll();
    }

    /**
     * Get all genres for checkboxes
     */
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    /**
     * Get all constraints for checkboxes
     */
    public List<LesContraints> getAllContraints() {
        return contraintsRepository.findAll();
    }
}
