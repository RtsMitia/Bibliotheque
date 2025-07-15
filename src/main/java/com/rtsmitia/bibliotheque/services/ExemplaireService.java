package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.repositories.ExemplaireRepository;
import com.rtsmitia.bibliotheque.repositories.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExemplaireService {

    private final ExemplaireRepository exemplaireRepository;
    private final LivreRepository livreRepository;

    @Autowired
    public ExemplaireService(ExemplaireRepository exemplaireRepository, LivreRepository livreRepository) {
        this.exemplaireRepository = exemplaireRepository;
        this.livreRepository = livreRepository;
    }

    /**
     * Create a new exemplaire for a book
     */
    public Exemplaire createExemplaire(Long livreId, LocalDateTime dateArrivee) {
        Optional<Livre> livreOpt = livreRepository.findById(livreId);
        if (livreOpt.isEmpty()) {
            throw new RuntimeException("Livre not found with id: " + livreId);
        }

        Livre livre = livreOpt.get();
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setLivre(livre);
        exemplaire.setDateArrivee(dateArrivee != null ? dateArrivee : LocalDateTime.now());

        return exemplaireRepository.save(exemplaire);
    }

    /**
     * Create a new exemplaire with current date
     */
    public Exemplaire createExemplaire(Long livreId) {
        return createExemplaire(livreId, LocalDateTime.now());
    }

    /**
     * Get all exemplaires
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> getAllExemplaires() {
        return exemplaireRepository.findAllByOrderByDateArriveeDesc();
    }

    /**
     * Get exemplaire by ID
     */
    @Transactional(readOnly = true)
    public Optional<Exemplaire> getExemplaireById(Long id) {
        return exemplaireRepository.findById(id);
    }

    /**
     * Get all exemplaires for a specific book
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> getExemplairesByLivreId(Long livreId) {
        return exemplaireRepository.findByLivreId(livreId);
    }

    /**
     * Get available exemplaires for a book
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> getAvailableExemplairesByLivreId(Long livreId) {
        return exemplaireRepository.findAvailableByLivreId(livreId);
    }

    /**
     * Get borrowed exemplaires for a book
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> getBorrowedExemplairesByLivreId(Long livreId) {
        return exemplaireRepository.findBorrowedByLivreId(livreId);
    }

    /**
     * Count total exemplaires for a book
     */
    @Transactional(readOnly = true)
    public long countExemplairesByLivreId(Long livreId) {
        return exemplaireRepository.countByLivreId(livreId);
    }

    /**
     * Check if a specific exemplaire is available
     */
    @Transactional(readOnly = true)
    public boolean isAvailable(Exemplaire exemplaire) {
        if (exemplaire == null) {
            return false;
        }
        
        // Check if the exemplaire is in the available list for its book
        List<Exemplaire> availableExemplaires = getAvailableExemplairesByLivreId(exemplaire.getLivre().getId());
        return availableExemplaires.stream()
                .anyMatch(ex -> ex.getId().equals(exemplaire.getId()));
    }

    /**
     * Mark exemplaire as borrowed (this method might be implemented in the repository)
     */
    public void markAsBorrowed(Exemplaire exemplaire) {
        // This method assumes that availability is determined by active loans
        // The actual borrowing status is managed through the Pret entity
        // No direct action needed here as availability is calculated based on active loans
    }

    /**
     * Mark exemplaire as available (this method might be implemented in the repository)
     */
    public void markAsAvailable(Exemplaire exemplaire) {
        // This method assumes that availability is determined by loan returns
        // The actual availability status is managed through the Pret entity
        // No direct action needed here as availability is calculated based on returned loans
    }

    /**
     * Check if a specific exemplaire is currently borrowed
     */
    @Transactional(readOnly = true)
    public boolean isExemplaireBorrowed(Long exemplaireId) {
        Optional<Exemplaire> exemplaireOpt = getExemplaireById(exemplaireId);
        if (exemplaireOpt.isEmpty()) {
            return false;
        }
        
        List<Exemplaire> borrowedExemplaires = exemplaireRepository.findBorrowedByLivreId(
            exemplaireOpt.get().getLivre().getId()
        );
        return borrowedExemplaires.stream()
                .anyMatch(ex -> ex.getId().equals(exemplaireId));
    }

    /**
     * Update an exemplaire
     */
    public Exemplaire updateExemplaire(Long id, LocalDateTime dateArrivee) {
        Optional<Exemplaire> exemplaireOpt = exemplaireRepository.findById(id);
        if (exemplaireOpt.isEmpty()) {
            throw new RuntimeException("Exemplaire not found with id: " + id);
        }

        Exemplaire exemplaire = exemplaireOpt.get();
        if (dateArrivee != null) {
            exemplaire.setDateArrivee(dateArrivee);
        }

        return exemplaireRepository.save(exemplaire);
    }

    /**
     * Delete an exemplaire if it's not currently borrowed
     */
    public void deleteExemplaire(Long id) {
        Optional<Exemplaire> exemplaireOpt = exemplaireRepository.findById(id);
        if (exemplaireOpt.isEmpty()) {
            throw new RuntimeException("Exemplaire not found with id: " + id);
        }

        if (isExemplaireBorrowed(id)) {
            throw new RuntimeException("Cannot delete exemplaire that is currently borrowed");
        }

        exemplaireRepository.deleteById(id);
    }

    /**
     * Search exemplaires by book title
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> searchExemplairesByBookTitle(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllExemplaires();
        }
        return exemplaireRepository.findByLivreTitreContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Get exemplaire statistics for a book
     */
    @Transactional(readOnly = true)
    public ExemplaireStats getExemplaireStats(Long livreId) {
        long total = countExemplairesByLivreId(livreId);
        long available = getAvailableExemplairesByLivreId(livreId).size();
        long borrowed = getBorrowedExemplairesByLivreId(livreId).size();

        return new ExemplaireStats(total, available, borrowed);
    }

    /**
     * Get all exemplaires with prets loaded for availability checking
     */
    @Transactional(readOnly = true)
    public List<Exemplaire> getAllExemplairesWithPrets() {
        List<Exemplaire> exemplaires = exemplaireRepository.findAllByOrderByDateArriveeDesc();
        // Force loading of prets for availability checking
        exemplaires.forEach(exemplaire -> {
            if (exemplaire.getPrets() != null) {
                exemplaire.getPrets().size(); // This triggers loading
            }
        });
        return exemplaires;
    }

    /**
     * Inner class for exemplaire statistics
     */
    public static class ExemplaireStats {
        private final long total;
        private final long available;
        private final long borrowed;

        public ExemplaireStats(long total, long available, long borrowed) {
            this.total = total;
            this.available = available;
            this.borrowed = borrowed;
        }

        public long getTotal() { return total; }
        public long getAvailable() { return available; }
        public long getBorrowed() { return borrowed; }
    }
}
