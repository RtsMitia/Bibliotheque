package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.StatutPret;
import com.rtsmitia.bibliotheque.models.StatutPret.StatutPretEnum;
import com.rtsmitia.bibliotheque.repositories.StatutPretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class StatutPretService {
    
    @Autowired
    private StatutPretRepository statutPretRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Create a new status entry for a loan
     */
    public StatutPret createStatus(Pret pret, StatutPretEnum statut, LocalDateTime dateChangement) {
        StatutPret statutPret = new StatutPret();
        statutPret.setPret(pret);
        statutPret.setStatut(statut);
        statutPret.setDateChangement(dateChangement.format(DATE_FORMATTER));
        
        return statutPretRepository.save(statutPret);
    }
    
    /**
     * Create a new status entry for a loan with current timestamp
     */
    public StatutPret createStatus(Pret pret, StatutPretEnum statut) {
        return createStatus(pret, statut, LocalDateTime.now());
    }
    
    /**
     * Get the current status of a loan
     */
    public Optional<StatutPret> getCurrentStatus(Pret pret) {
        return statutPretRepository.findCurrentStatusByPret(pret);
    }
    
    /**
     * Get all status history for a loan
     */
    public List<StatutPret> getStatusHistory(Pret pret) {
        return statutPretRepository.findByPretOrderByDateChangementDesc(pret);
    }
    
    /**
     * Check if a loan is currently valid
     */
    public boolean isLoanValid(Pret pret) {
        Optional<StatutPret> currentStatus = getCurrentStatus(pret);
        return currentStatus.isPresent() && currentStatus.get().getStatut() == StatutPretEnum.valide;
    }
    
    /**
     * Check if a loan is pending
     */
    public boolean isLoanPending(Pret pret) {
        Optional<StatutPret> currentStatus = getCurrentStatus(pret);
        return currentStatus.isPresent() && currentStatus.get().isPending();
    }
    
    /**
     * Check if a loan is refused
     */
    public boolean isLoanRefused(Pret pret) {
        Optional<StatutPret> currentStatus = getCurrentStatus(pret);
        return currentStatus.isPresent() && currentStatus.get().isRejected();
    }
    
    /**
     * Approve a loan (change status to 'valide')
     */
    public StatutPret approveLoan(Pret pret) {
        return createStatus(pret, StatutPretEnum.valide);
    }
    
    /**
     * Refuse a loan (change status to 'refuse')
     */
    public StatutPret refuseLoan(Pret pret) {
        return createStatus(pret, StatutPretEnum.refuse);
    }
    
    /**
     * Put loan on hold (change status to 'en_attente')
     */
    public StatutPret putLoanOnHold(Pret pret) {
        return createStatus(pret, StatutPretEnum.en_attente);
    }
    
    /**
     * Request a loan (change status to 'demande')
     */
    public StatutPret requestLoan(Pret pret) {
        return createStatus(pret, StatutPretEnum.demande);
    }
    
    /**
     * Get all loans with pending status
     */
    public List<StatutPret> getPendingLoans() {
        return statutPretRepository.findPendingStatuses();
    }
    
    /**
     * Get all loans with valid status
     */
    public List<StatutPret> getValidLoans() {
        return statutPretRepository.findValidStatuses();
    }
    
    /**
     * Get all loans with refused status
     */
    public List<StatutPret> getRefusedLoans() {
        return statutPretRepository.findRefusedStatuses();
    }
    
    /**
     * Get loans by status
     */
    public List<StatutPret> getLoansByStatus(StatutPretEnum statut) {
        return statutPretRepository.findByStatut(statut);
    }
    
    /**
     * Save a status entry
     */
    public StatutPret save(StatutPret statutPret) {
        return statutPretRepository.save(statutPret);
    }
    
    /**
     * Delete a status entry
     */
    public void delete(StatutPret statutPret) {
        statutPretRepository.delete(statutPret);
    }
    
    /**
     * Find status by ID
     */
    public Optional<StatutPret> findById(Long id) {
        return statutPretRepository.findById(id);
    }
}
