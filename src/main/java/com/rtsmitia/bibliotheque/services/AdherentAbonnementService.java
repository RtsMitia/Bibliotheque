package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.AdherentAbonnement;
import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.repositories.AdherentAbonnementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdherentAbonnementService {

    private final AdherentAbonnementRepository abonnementRepository;

    @Autowired
    public AdherentAbonnementService(AdherentAbonnementRepository abonnementRepository) {
        this.abonnementRepository = abonnementRepository;
    }

    /**
     * Create a new abonnement for an adherent
     * Default subscription period is 1 year from the start date
     */
    public AdherentAbonnement createAbonnement(Adherent adherent, LocalDateTime dateChangement) {
        LocalDate startDate = dateChangement.toLocalDate();
        LocalDate endDate = startDate.plusYears(1); // 1 year subscription by default
        
        AdherentAbonnement abonnement = new AdherentAbonnement(
            startDate,
            endDate,
            startDate,
            adherent
        );
        
        return abonnementRepository.save(abonnement);
    }

    /**
     * Create a new abonnement with custom duration
     */
    public AdherentAbonnement createAbonnement(Adherent adherent, LocalDateTime dateChangement, int durationInMonths) {
        LocalDate startDate = dateChangement.toLocalDate();
        LocalDate endDate = startDate.plusMonths(durationInMonths);
        
        AdherentAbonnement abonnement = new AdherentAbonnement(
            startDate,
            endDate,
            startDate,
            adherent
        );
        
        return abonnementRepository.save(abonnement);
    }

    /**
     * Get all abonnements for a specific adherent
     */
    public List<AdherentAbonnement> getAbonnementsByAdherent(Adherent adherent) {
        return abonnementRepository.findByAdherent(adherent);
    }

    /**
     * Get current active abonnement for an adherent
     */
    public Optional<AdherentAbonnement> getCurrentAbonnement(Adherent adherent) {
        return abonnementRepository.findCurrentAbonnementByAdherent(adherent, LocalDate.now());
    }

    /**
     * Check if an adherent has an active subscription
     */
    public boolean hasActiveSubscription(Adherent adherent) {
        return getCurrentAbonnement(adherent).isPresent();
    }

    /**
     * Get all active abonnements
     */
    public List<AdherentAbonnement> getActiveAbonnements() {
        return abonnementRepository.findActiveAbonnements(LocalDate.now());
    }

    /**
     * Get expired abonnements
     */
    public List<AdherentAbonnement> getExpiredAbonnements() {
        return abonnementRepository.findExpiredAbonnements(LocalDate.now());
    }

    /**
     * Get abonnements ending soon (within 30 days)
     */
    public List<AdherentAbonnement> getAbonnementsEndingSoon() {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(30);
        return abonnementRepository.findAbonnementsEndingSoon(currentDate, endDate);
    }

    /**
     * Renew an existing abonnement
     */
    public AdherentAbonnement renewAbonnement(AdherentAbonnement existingAbonnement, int durationInMonths) {
        LocalDate newStartDate = existingAbonnement.getFinAbonnement().plusDays(1);
        LocalDate newEndDate = newStartDate.plusMonths(durationInMonths);
        
        AdherentAbonnement newAbonnement = new AdherentAbonnement(
            newStartDate,
            newEndDate,
            LocalDate.now(),
            existingAbonnement.getAdherent()
        );
        
        return abonnementRepository.save(newAbonnement);
    }
}
