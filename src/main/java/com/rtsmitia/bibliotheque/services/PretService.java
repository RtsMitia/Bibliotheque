package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.*;
import com.rtsmitia.bibliotheque.models.StatutPret.StatutPretEnum;
import com.rtsmitia.bibliotheque.repositories.PretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PretService {
    
    @Autowired
    private PretRepository pretRepository;
    
    @Autowired
    private StatutPretService statutPretService;
    
    @Autowired
    private AdherentService adherentService;
    
    @Autowired
    private PenaliteService penaliteService;
    
    @Autowired
    private QuotaService quotaService;
    
    @Autowired
    private ExemplaireService exemplaireService;
    
    /**
     * Client requests to borrow a book - creates loan request without dates
     */
    @Transactional
    public Pret requestLoan(Adherent adherent, Exemplaire exemplaire, TypePret typePret) {
        // Create loan without dates (will be set when approved)
        Pret pret = new Pret();
        pret.setAdherent(adherent);
        pret.setExemplaire(exemplaire);
        pret.setTypePret(typePret);
        // dateDebut, dateFin, dateRetour remain null until approved
        
        // Save the loan request
        Pret savedPret = pretRepository.save(pret);
        
        // Create initial status as "demande"
        statutPretService.createStatus(savedPret, StatutPretEnum.demande);
        
        return savedPret;
    }
    
    /**
     * Admin puts loan request on hold
     */
    @Transactional
    public boolean putLoanOnHold(Long pretId) {
        Optional<Pret> pretOpt = pretRepository.findById(pretId);
        if (pretOpt.isPresent()) {
            Pret pret = pretOpt.get();
            statutPretService.putLoanOnHold(pret);
            return true;
        }
        return false;
    }
    
    /**
     * Admin approves loan request - validates all constraints and sets dates
     */
    @Transactional
    public LoanApprovalResult approveLoan(Long pretId) {
        Optional<Pret> pretOpt = pretRepository.findById(pretId);
        if (pretOpt.isEmpty()) {
            return new LoanApprovalResult(false, "Prêt introuvable");
        }
        
        Pret pret = pretOpt.get();
        Adherent adherent = pret.getAdherent();
        
        // Validate all constraints
        LoanEligibilityResult eligibility = checkLoanEligibility(adherent, pret.getExemplaire());
        if (!eligibility.isEligible()) {
            // Create rejected status with reason
            statutPretService.refuseLoan(pret);
            return new LoanApprovalResult(false, eligibility.getReason());
        }
        
        // Set loan dates
        LocalDateTime dateDebut = LocalDateTime.now();
        int loanDays = quotaService.getMaxLoanDays(adherent);
        LocalDateTime dateFin = dateDebut.plusDays(loanDays);
        
        pret.setDateDebut(dateDebut);
        pret.setDateFin(dateFin);
        
        // Save updated loan
        pretRepository.save(pret);
        
        // Update status to approved
        statutPretService.approveLoan(pret);
        
        // Mark exemplaire as borrowed
        exemplaireService.markAsBorrowed(pret.getExemplaire());
        
        return new LoanApprovalResult(true, "Prêt approuvé avec succès");
    }
    
    /**
     * Admin rejects loan request
     */
    @Transactional
    public boolean rejectLoan(Long pretId, String reason) {
        Optional<Pret> pretOpt = pretRepository.findById(pretId);
        if (pretOpt.isPresent()) {
            Pret pret = pretOpt.get();
            statutPretService.refuseLoan(pret);
            return true;
        }
        return false;
    }
    
    /**
     * Check if adherent is eligible for a loan
     */
    @Transactional(readOnly = true)
    public LoanEligibilityResult checkLoanEligibility(Adherent adherent, Exemplaire exemplaire) {
        // Check if adherent has valid subscription
        if (!adherentService.hasValidSubscription(adherent)) {
            return new LoanEligibilityResult(false, "Abonnement non valide");
        }
        
        // Check if adherent has overdue books
        List<Pret> overdueLoans = pretRepository.findOverdueLoansForAdherent(adherent);
        if (!overdueLoans.isEmpty()) {
            return new LoanEligibilityResult(false, 
                String.format("Vous avez %d livre(s) en retard. Veuillez les retourner avant d'emprunter.", 
                    overdueLoans.size()));
        }
        
        // Check if adherent has active penalties
        if (penaliteService.hasActivePenalties(adherent)) {
            return new LoanEligibilityResult(false, "Pénalité en cours - impossible d'emprunter");
        }
        
        // Check if exemplaire is available
        if (!exemplaireService.isAvailable(exemplaire)) {
            return new LoanEligibilityResult(false, "Exemplaire non disponible");
        }
        
        // Check current borrowed books count
        int currentBorrowedCount = getCurrentBorrowedBooksCount(adherent);
        if (!quotaService.canBorrowMoreBooks(adherent, currentBorrowedCount)) {
            int maxAllowed = quotaService.getMaxBooksAllowed(adherent);
            return new LoanEligibilityResult(false, 
                String.format("Limite d'emprunt atteinte (%d/%d livres)", currentBorrowedCount, maxAllowed));
        }
        
        // Check constraints (if any)
        List<LesContraints> bookConstraints = exemplaire.getLivre().getContraintes();
        if (bookConstraints != null && !bookConstraints.isEmpty()) {
            List<LesContraints> adherentConstraints = adherent.getContraintes();
            
            // Check if adherent has any conflicting constraints with the book
            if (adherentConstraints != null) {
                for (LesContraints bookConstraint : bookConstraints) {
                    boolean hasConflictingConstraint = adherentConstraints.stream()
                            .anyMatch(ac -> ac.getTypeContrainte().equals(bookConstraint.getTypeContrainte()));
                    
                    if (hasConflictingConstraint) {
                        return new LoanEligibilityResult(false, 
                            String.format("Contrainte restrictive: %s - Ce livre n'est pas accessible", 
                                bookConstraint.getTypeContrainte()));
                    }
                }
            }
        }
        
        return new LoanEligibilityResult(true, "Éligible pour l'emprunt");
    }
    
    /**
     * Get current number of borrowed books for an adherent
     */
    public int getCurrentBorrowedBooksCount(Adherent adherent) {
        return pretRepository.countActiveLoansByAdherent(adherent);
    }
    
    /**
     * Get all pending loan requests for admin review
     */
    public List<Pret> getPendingLoanRequests() {
        return pretRepository.findLoansWithStatus(StatutPretEnum.demande);
    }
    
    /**
     * Get all loans on hold
     */
    public List<Pret> getLoansOnHold() {
        return pretRepository.findLoansWithStatus(StatutPretEnum.en_attente);
    }
    
    /**
     * Get all approved loans
     */
    public List<Pret> getApprovedLoans() {
        return pretRepository.findLoansWithStatus(StatutPretEnum.valide);
    }
    
    /**
     * Get all rejected loans
     */
    public List<Pret> getRejectedLoans() {
        return pretRepository.findLoansWithStatus(StatutPretEnum.refuse);
    }
    
    /**
     * Get active loans for an adherent
     */
    public List<Pret> getActiveLoansForAdherent(Adherent adherent) {
        return pretRepository.findActiveLoansByAdherent(adherent);
    }
    
    /**
     * Return a book
     */
    @Transactional
    public boolean returnBook(Long pretId) {
        Optional<Pret> pretOpt = pretRepository.findById(pretId);
        if (pretOpt.isPresent()) {
            Pret pret = pretOpt.get();
            pret.setDateRetour(LocalDateTime.now());
            pretRepository.save(pret);
            
            // Mark exemplaire as available again
            exemplaireService.markAsAvailable(pret.getExemplaire());
            
            return true;
        }
        return false;
    }
    
    /**
     * Return a book with custom date and penalty calculation
     */
    @Transactional
    public BookReturnResult returnBookWithDate(Long pretId, LocalDateTime returnDate, Adherent adherent) {
        Optional<Pret> pretOpt = pretRepository.findById(pretId);
        if (pretOpt.isEmpty()) {
            return new BookReturnResult(false, "Prêt introuvable");
        }
        
        Pret pret = pretOpt.get();
        
        // Check if the loan belongs to the adherent
        if (!pret.getAdherent().getId().equals(adherent.getId())) {
            return new BookReturnResult(false, "Ce prêt ne vous appartient pas");
        }
        
        // Check if already returned
        if (pret.getDateRetour() != null) {
            return new BookReturnResult(false, "Ce livre a déjà été retourné");
        }
        
        // Set return date
        pret.setDateRetour(returnDate);
        pretRepository.save(pret);
        
        // Mark exemplaire as available again
        exemplaireService.markAsAvailable(pret.getExemplaire());
        
        // Check if return is late and apply penalty if needed
        if (pret.getDateFin() != null && returnDate.isAfter(pret.getDateFin())) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(pret.getDateFin().toLocalDate(), returnDate.toLocalDate());
            String penaltyMessage = penaliteService.applyLatePenalty(adherent, (int) daysLate);
            
            return new BookReturnResult(true, 
                String.format("Livre retourné avec succès. ATTENTION: Retour en retard de %d jour(s). %s", 
                    daysLate, penaltyMessage));
        }
        
        return new BookReturnResult(true, "Livre retourné avec succès dans les délais.");
    }

    /**
     * Get all loans for an adherent
     */
    public List<Pret> getLoansForAdherent(Adherent adherent) {
        return pretRepository.findByAdherent(adherent);
    }
    
    /**
     * Save a loan
     */
    public Pret save(Pret pret) {
        return pretRepository.save(pret);
    }
    
    /**
     * Find loan by ID
     */
    public Optional<Pret> findById(Long id) {
        return pretRepository.findById(id);
    }
    
    // Helper classes for results
    public static class LoanApprovalResult {
        private boolean successful;
        private String message;
        
        public LoanApprovalResult(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }
        
        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
    }
    
    public static class LoanEligibilityResult {
        private boolean eligible;
        private String reason;
        
        public LoanEligibilityResult(boolean eligible, String reason) {
            this.eligible = eligible;
            this.reason = reason;
        }
        
        public boolean isEligible() { return eligible; }
        public String getReason() { return reason; }
    }
    
    public static class BookReturnResult {
        private boolean successful;
        private String message;
        
        public BookReturnResult(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }
        
        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
    }
}
