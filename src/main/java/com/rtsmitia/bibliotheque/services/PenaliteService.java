package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Penalite;
import com.rtsmitia.bibliotheque.models.PenaliteConfig;
import com.rtsmitia.bibliotheque.repositories.PenaliteRepository;
import com.rtsmitia.bibliotheque.repositories.PenaliteConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PenaliteService {
    
    @Autowired
    private PenaliteRepository penaliteRepository;
    
    @Autowired
    private PenaliteConfigRepository penaliteConfigRepository;
    
    /**
     * Check if an adherent has active penalties
     */
    public boolean hasActivePenalties(Adherent adherent) {
        return penaliteRepository.hasActivePenalties(adherent);
    }
    
    /**
     * Get all active penalties for an adherent
     */
    public List<Penalite> getActivePenalties(Adherent adherent) {
        return penaliteRepository.findActivePenaltiesForAdherent(adherent);
    }
    
    /**
     * Get all penalties for an adherent
     */
    public List<Penalite> getAllPenalties(Adherent adherent) {
        return penaliteRepository.findByAdherent(adherent);
    }
    
    /**
     * Create a new penalty for an adherent
     */
    public Penalite createPenalty(Adherent adherent, LocalDate dateDebut, LocalDate dateFin) {
        Penalite penalite = new Penalite(dateDebut, dateFin, adherent);
        return penaliteRepository.save(penalite);
    }
    
    /**
     * Create a penalty based on the adherent's type configuration
     */
    public Penalite createPenaltyFromConfig(Adherent adherent) {
        Optional<PenaliteConfig> configOpt = penaliteConfigRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
        
        if (configOpt.isPresent()) {
            PenaliteConfig config = configOpt.get();
            LocalDate dateDebut = LocalDate.now();
            LocalDate dateFin = dateDebut.plusDays(config.getNombreJour());
            
            return createPenalty(adherent, dateDebut, dateFin);
        }
        
        // Default penalty of 7 days if no configuration found
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusDays(7);
        return createPenalty(adherent, dateDebut, dateFin);
    }
    
    /**
     * Get penalty configuration for an adherent type
     */
    public Optional<PenaliteConfig> getPenalityConfig(Adherent adherent) {
        return penaliteConfigRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
    }
    
    /**
     * Check if an adherent can borrow books (no active penalties)
     */
    public boolean canBorrow(Adherent adherent) {
        return !hasActivePenalties(adherent);
    }
    
    /**
     * Get penalties expiring soon
     */
    public List<Penalite> getPenaltiesExpiringSoon(int daysAhead) {
        LocalDate endDate = LocalDate.now().plusDays(daysAhead);
        return penaliteRepository.findPenaltiesExpiringSoon(endDate);
    }
    
    /**
     * Save a penalty
     */
    public Penalite save(Penalite penalite) {
        return penaliteRepository.save(penalite);
    }
    
    /**
     * Delete a penalty
     */
    public void delete(Penalite penalite) {
        penaliteRepository.delete(penalite);
    }
    
    /**
     * Find penalty by ID
     */
    public Optional<Penalite> findById(Long id) {
        return penaliteRepository.findById(id);
    }
    
    /**
     * Apply penalty for late book return
     */
    public String applyLatePenalty(Adherent adherent, int daysLate) {
        // Get penalty configuration for adherent type
        Optional<PenaliteConfig> configOpt = penaliteConfigRepository.findByTypeAdherent(adherent.getTypeAdherent());
        if (configOpt.isEmpty()) {
            return "Configuration de pénalité non trouvée - aucune pénalité appliquée.";
        }
        
        PenaliteConfig config = configOpt.get();
        int penaltyDays = config.getNombreJour();
        
        // Calculate penalty period
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(penaltyDays);
        
        // Create penalty
        Penalite penalty = createPenalty(adherent, startDate, endDate);
        
        return String.format("Pénalité appliquée: %d jour(s) d'interdiction d'emprunt (jusqu'au %s)", 
            penaltyDays, endDate.toString());
    }
}
