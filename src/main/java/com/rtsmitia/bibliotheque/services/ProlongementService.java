package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Prolongement;
import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.StatutProlongement;
import com.rtsmitia.bibliotheque.models.Adherent;
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
    private final QuotaPretService quotaPretService;
    private final PenaliteService penaliteService;
    private final PretService pretService;

    @Autowired
    public ProlongementService(ProlongementRepository prolongementRepository, 
                               StatutProlongementService statutProlongementService,
                               QuotaPretService quotaPretService,
                               PenaliteService penaliteService,
                               PretService pretService) {
        this.prolongementRepository = prolongementRepository;
        this.statutProlongementService = statutProlongementService;
        this.quotaPretService = quotaPretService;
        this.penaliteService = penaliteService;
        this.pretService = pretService;
    }

    /**
     * Create a new prolongement request with validation
     */
    public Prolongement createProlongement(Pret pret, LocalDateTime dateProlongement) {
        // Validate that the request is made before the due date
        if (dateProlongement.isAfter(pret.getDateFin())) {
            throw new RuntimeException("La demande de prolongement ne peut pas être faite après la date d'échéance du prêt");
        }
        
        // Check if adherent has any active penalties
        if (hasActivePenalities(pret.getAdherent())) {
            throw new RuntimeException("Impossible de prolonger : l'adhérent a des pénalités actives");
        }
        
        // Check if there's already a pending request
        if (hasPendingExtensionRequest(pret)) {
            throw new RuntimeException("Une demande de prolongement est déjà en cours pour ce prêt");
        }
        
        // Automatically determine the prolongement duration based on adherent's quota
        Integer prolongementDays = quotaPretService.getLoanDurationForType(pret.getAdherent().getTypeAdherent());
        LocalDateTime nouvelleDateRetour = pret.getDateFin().plusDays(prolongementDays);
        
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
     * Create a new prolongement request with custom duration (for admin use)
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
            
            // Validate that the request was made before the due date
            if (prolongement.getDateProlongement().isAfter(prolongement.getPret().getDateFin())) {
                throw new RuntimeException("Impossible d'approuver : la demande a été faite après la date d'échéance");
            }
            
            // Check if adherent still has no active penalties
            if (hasActivePenalities(prolongement.getPret().getAdherent())) {
                throw new RuntimeException("Impossible d'approuver : l'adhérent a des pénalités actives");
            }
            
            // Create approval status
            statutProlongementService.createStatutProlongement(
                prolongement,
                StatutProlongement.StatutType.VALIDE,
                dateApprobation
            );
            
            // Update the loan's end date
            Pret pret = prolongement.getPret();
            pret.setDateFin(prolongement.getNouvelleDateRetour());
            pretService.save(pret);
            
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

    /**
     * Check if a loan has a pending extension request
     */
    public boolean hasPendingExtensionRequest(Pret pret) {
        List<Prolongement> prolongements = getProlongementsByPret(pret);
        for (Prolongement prolongement : prolongements) {
            StatutProlongement currentStatus = statutProlongementService.getCurrentStatus(prolongement);
            if (currentStatus != null && 
                ("demande".equals(currentStatus.getStatut()) || "en attente".equals(currentStatus.getStatut()))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if an adherent has active penalties
     */
    private boolean hasActivePenalities(Adherent adherent) {
        return penaliteService.hasActivePenalties(adherent);
    }

    /**
     * Get all pending prolongement requests for admin approval
     */
    public List<Prolongement> getPendingProlongementRequests() {
        List<Prolongement> allProlongements = getAllProlongements();
        return allProlongements.stream()
            .filter(prolongement -> {
                StatutProlongement currentStatus = statutProlongementService.getCurrentStatus(prolongement);
                return currentStatus != null && 
                       ("demande".equals(currentStatus.getStatut()) || "en attente".equals(currentStatus.getStatut()));
            })
            .toList();
    }
}
