package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.StatutProlongement;
import com.rtsmitia.bibliotheque.models.Prolongement;
import com.rtsmitia.bibliotheque.repositories.StatutProlongementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StatutProlongementService {

    private final StatutProlongementRepository statutProlongementRepository;

    @Autowired
    public StatutProlongementService(StatutProlongementRepository statutProlongementRepository) {
        this.statutProlongementRepository = statutProlongementRepository;
    }

    /**
     * Create a new statut prolongement
     */
    public StatutProlongement createStatutProlongement(Prolongement prolongement, StatutProlongement.StatutType statutType, LocalDateTime dateChangement) {
        StatutProlongement statutProlongement = new StatutProlongement(statutType, dateChangement, prolongement);
        return statutProlongementRepository.save(statutProlongement);
    }

    /**
     * Create a new statut prolongement with string status
     */
    public StatutProlongement createStatutProlongement(Prolongement prolongement, String statut, LocalDateTime dateChangement) {
        StatutProlongement statutProlongement = new StatutProlongement(statut, dateChangement, prolongement);
        return statutProlongementRepository.save(statutProlongement);
    }

    /**
     * Get all statuts prolongement
     */
    public List<StatutProlongement> getAllStatutsProlongement() {
        return statutProlongementRepository.findAll();
    }

    /**
     * Get statut prolongement by ID
     */
    public Optional<StatutProlongement> getStatutProlongementById(Long id) {
        return statutProlongementRepository.findById(id);
    }

    /**
     * Get all statuts for a specific prolongement
     */
    public List<StatutProlongement> getStatutsByProlongement(Prolongement prolongement) {
        return statutProlongementRepository.findByProlongement(prolongement);
    }

    /**
     * Get statuts by prolongement ID
     */
    public List<StatutProlongement> getStatutsByProlongementId(Long prolongementId) {
        return statutProlongementRepository.findByProlongementId(prolongementId);
    }

    /**
     * Get the most recent statut for a prolongement
     */
    public StatutProlongement getMostRecentStatut(Prolongement prolongement) {
        return statutProlongementRepository.findMostRecentByProlongement(prolongement);
    }

    /**
     * Get statuts by status type
     */
    public List<StatutProlongement> getStatutsByType(String statut) {
        return statutProlongementRepository.findByStatut(statut);
    }

    /**
     * Get statuts by status type using enum
     */
    public List<StatutProlongement> getStatutsByType(StatutProlongement.StatutType statutType) {
        return statutProlongementRepository.findByStatut(statutType.getLibelle());
    }

    /**
     * Get statuts within a date range
     */
    public List<StatutProlongement> getStatutsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return statutProlongementRepository.findStatutsInDateRange(startDate, endDate);
    }

    /**
     * Get statuts by status and date range
     */
    public List<StatutProlongement> getStatutsByTypeAndDateRange(String statut, LocalDateTime startDate, LocalDateTime endDate) {
        return statutProlongementRepository.findByStatutAndDateRange(statut, startDate, endDate);
    }

    /**
     * Get statuts by status ordered by date (most recent first)
     */
    public List<StatutProlongement> getStatutsByTypeOrderedByDate(String statut) {
        return statutProlongementRepository.findByStatutOrderByDateDesc(statut);
    }

    /**
     * Get pending prolongement requests (statut = "demandé")
     */
    public List<StatutProlongement> getPendingRequests() {
        return getStatutsByType(StatutProlongement.StatutType.DEMANDE);
    }

    /**
     * Get approved prolongements (statut = "valide")
     */
    public List<StatutProlongement> getApprovedRequests() {
        return getStatutsByType(StatutProlongement.StatutType.VALIDE);
    }

    /**
     * Get rejected prolongements (statut = "refusé")
     */
    public List<StatutProlongement> getRejectedRequests() {
        return getStatutsByType(StatutProlongement.StatutType.REFUSE);
    }

    /**
     * Update statut prolongement
     */
    public StatutProlongement updateStatutProlongement(StatutProlongement statutProlongement) {
        return statutProlongementRepository.save(statutProlongement);
    }

    /**
     * Delete statut prolongement
     */
    public void deleteStatutProlongement(Long id) {
        statutProlongementRepository.deleteById(id);
    }

    /**
     * Check if a prolongement has a specific status
     */
    public boolean hasStatus(Prolongement prolongement, StatutProlongement.StatutType statutType) {
        StatutProlongement currentStatut = getMostRecentStatut(prolongement);
        return currentStatut != null && currentStatut.getStatut().equals(statutType.getLibelle());
    }

    /**
     * Check if a prolongement is pending
     */
    public boolean isPending(Prolongement prolongement) {
        return hasStatus(prolongement, StatutProlongement.StatutType.DEMANDE);
    }

    /**
     * Check if a prolongement is approved
     */
    public boolean isApproved(Prolongement prolongement) {
        return hasStatus(prolongement, StatutProlongement.StatutType.VALIDE);
    }

    /**
     * Check if a prolongement is rejected
     */
    public boolean isRejected(Prolongement prolongement) {
        return hasStatus(prolongement, StatutProlongement.StatutType.REFUSE);
    }

    /**
     * Get the current (most recent) status for a prolongement
     */
    public StatutProlongement getCurrentStatus(Prolongement prolongement) {
        List<StatutProlongement> statuts = getStatutsByProlongement(prolongement);
        if (statuts.isEmpty()) {
            return null;
        }

        // Find the most recent status
        StatutProlongement currentStatus = statuts.get(0);
        for (StatutProlongement statut : statuts) {
            if (statut.getDateChangement().isAfter(currentStatus.getDateChangement())) {
                currentStatus = statut;
            }
        }
        return currentStatus;
    }
}
