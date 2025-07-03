package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Prolongement;
import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.StatutProlongement;
import com.rtsmitia.bibliotheque.repositories.ProlongementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProlongementService {

    private final ProlongementRepository prolongementRepository;
    private final StatutProlongementService statutProlongementService;

    @Autowired
    public ProlongementService(ProlongementRepository prolongementRepository, StatutProlongementService statutProlongementService) {
        this.prolongementRepository = prolongementRepository;
        this.statutProlongementService = statutProlongementService;
    }

    /**
     * Create a new prolongement request
     */
    public Prolongement createProlongement(Pret pret, LocalDateTime dateProlongement, LocalDateTime nouvelleDateRetour) {
        Prolongement prolongement = new Prolongement(dateProlongement, nouvelleDateRetour, pret);
        Prolongement savedProlongement = prolongementRepository.save(prolongement);
        
        // Create initial status
        statutProlongementService.createStatutProlongement(
            savedProlongement, 
            StatutProlongement.StatutType.DEMANDE, 
            dateProlongement
        );
        
        return savedProlongement;
    }

    /**
     * Get all prolongements
     */
    public List<Prolongement> getAllProlongements() {
        return prolongementRepository.findAll();
    }

    /**
     * Get prolongement by ID
     */
    public Optional<Prolongement> getProlongementById(Long id) {
        return prolongementRepository.findById(id);
    }

    /**
     * Get all prolongements for a specific pret
     */
    public List<Prolongement> getProlongementsByPret(Pret pret) {
        return prolongementRepository.findByPret(pret);
    }

    /**
     * Get prolongements by pret ID
     */
    public List<Prolongement> getProlongementsByPretId(Long pretId) {
        return prolongementRepository.findByPretId(pretId);
    }

    /**
     * Get the most recent prolongement for a pret
     */
    public Prolongement getMostRecentProlongement(Pret pret) {
        return prolongementRepository.findMostRecentByPret(pret);
    }

    /**
     * Count prolongements for a specific pret
     */
    public long countProlongementsByPret(Pret pret) {
        return prolongementRepository.countByPret(pret);
    }

    /**
     * Get prolongements within a date range
     */
    public List<Prolongement> getProlongementsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return prolongementRepository.findProlongementsInDateRange(startDate, endDate);
    }

    /**
     * Get prolongements with return date before specified date
     */
    public List<Prolongement> getProlongementsWithRetourBefore(LocalDateTime date) {
        return prolongementRepository.findProlongementsWithRetourBefore(date);
    }

    /**
     * Update prolongement
     */
    public Prolongement updateProlongement(Prolongement prolongement) {
        return prolongementRepository.save(prolongement);
    }

    /**
     * Delete prolongement
     */
    public void deleteProlongement(Long id) {
        prolongementRepository.deleteById(id);
    }

    /**
     * Check if a pret can be extended (business logic)
     */
    public boolean canExtendPret(Pret pret) {
        long currentProlongements = countProlongementsByPret(pret);
        // Example: maximum 2 prolongements per pret
        return currentProlongements < 2;
    }

    /**
     * Approve a prolongement request
     */
    public Prolongement approveProlongement(Long prolongementId, LocalDateTime dateApprobation) {
        Optional<Prolongement> optionalProlongement = getProlongementById(prolongementId);
        if (optionalProlongement.isPresent()) {
            Prolongement prolongement = optionalProlongement.get();
            
            // Create approval status
            statutProlongementService.createStatutProlongement(
                prolongement,
                StatutProlongement.StatutType.APPROUVE,
                dateApprobation
            );
            
            return prolongement;
        }
        throw new RuntimeException("Prolongement not found with id: " + prolongementId);
    }

    /**
     * Reject a prolongement request
     */
    public Prolongement rejectProlongement(Long prolongementId, LocalDateTime dateRejet) {
        Optional<Prolongement> optionalProlongement = getProlongementById(prolongementId);
        if (optionalProlongement.isPresent()) {
            Prolongement prolongement = optionalProlongement.get();
            
            // Create rejection status
            statutProlongementService.createStatutProlongement(
                prolongement,
                StatutProlongement.StatutType.REFUSE,
                dateRejet
            );
            
            return prolongement;
        }
        throw new RuntimeException("Prolongement not found with id: " + prolongementId);
    }
}
