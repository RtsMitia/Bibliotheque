package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.QuotaPret;
import com.rtsmitia.bibliotheque.models.QuotaReservation;
import com.rtsmitia.bibliotheque.repositories.QuotaPretRepository;
import com.rtsmitia.bibliotheque.repositories.QuotaReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuotaService {
    
    @Autowired
    private QuotaPretRepository quotaPretRepository;
    
    @Autowired
    private QuotaReservationRepository quotaReservationRepository;
    
    /**
     * Get the maximum number of books an adherent can borrow
     */
    public int getMaxBooksAllowed(Adherent adherent) {
        Optional<QuotaPret> quotaOpt = quotaPretRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
        return quotaOpt.map(QuotaPret::getNombreLivre).orElse(3); // Default: 3 books
    }
    
    /**
     * Get the maximum loan period in days for an adherent
     */
    public int getMaxLoanDays(Adherent adherent) {
        Optional<QuotaPret> quotaOpt = quotaPretRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
        return quotaOpt.map(QuotaPret::getNombreJourPret).orElse(14); // Default: 14 days
    }
    
    /**
     * Get the maximum number of books an adherent can reserve
     */
    public int getMaxReservationsAllowed(Adherent adherent) {
        Optional<QuotaReservation> quotaOpt = quotaReservationRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
        return quotaOpt.map(QuotaReservation::getNombreLivre).orElse(2); // Default: 2 reservations
    }
    
    /**
     * Check if an adherent can borrow more books
     */
    public boolean canBorrowMoreBooks(Adherent adherent, int currentBorrowedCount) {
        int maxAllowed = getMaxBooksAllowed(adherent);
        return currentBorrowedCount < maxAllowed;
    }
    
    /**
     * Check if an adherent can make more reservations
     */
    public boolean canMakeMoreReservations(Adherent adherent, int currentReservationCount) {
        int maxAllowed = getMaxReservationsAllowed(adherent);
        return currentReservationCount < maxAllowed;
    }
    
    /**
     * Get loan quota for an adherent
     */
    public Optional<QuotaPret> getLoanQuota(Adherent adherent) {
        return quotaPretRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
    }
    
    /**
     * Get reservation quota for an adherent
     */
    public Optional<QuotaReservation> getReservationQuota(Adherent adherent) {
        return quotaReservationRepository.findCurrentByTypeAdherent(adherent.getTypeAdherent());
    }
    
    /**
     * Get remaining books an adherent can borrow
     */
    public int getRemainingBorrowAllowance(Adherent adherent, int currentBorrowedCount) {
        int maxAllowed = getMaxBooksAllowed(adherent);
        return Math.max(0, maxAllowed - currentBorrowedCount);
    }
    
    /**
     * Get remaining reservations an adherent can make
     */
    public int getRemainingReservationAllowance(Adherent adherent, int currentReservationCount) {
        int maxAllowed = getMaxReservationsAllowed(adherent);
        return Math.max(0, maxAllowed - currentReservationCount);
    }
}
