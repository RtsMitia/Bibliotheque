package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.TypePret;
import com.rtsmitia.bibliotheque.repositories.AdherentRepository;
import com.rtsmitia.bibliotheque.repositories.ExemplaireRepository;
import com.rtsmitia.bibliotheque.repositories.ReservationRepository;
import com.rtsmitia.bibliotheque.repositories.StatutReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StatutReservationRepository statutReservationRepository;
    private final AdherentRepository adherentRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final PenaliteService penaliteService;
    private final PretService pretService;
    private final TypePretService typePretService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                             StatutReservationRepository statutReservationRepository,
                             AdherentRepository adherentRepository,
                             ExemplaireRepository exemplaireRepository,
                             PenaliteService penaliteService,
                             PretService pretService,
                             TypePretService typePretService) {
        this.reservationRepository = reservationRepository;
        this.statutReservationRepository = statutReservationRepository;
        this.adherentRepository = adherentRepository;
        this.exemplaireRepository = exemplaireRepository;
        this.penaliteService = penaliteService;
        this.pretService = pretService;
        this.typePretService = typePretService;
    }

    /**
     * Create a new reservation (no business rules validation - goes directly to admin for approval)
     */
    public Reservation createReservation(String numeroAdherent, Long exemplaireId, LocalDateTime dateDebutPret) {
        // Validate adherent exists
        Optional<Adherent> adherentOpt = adherentRepository.findByNumeroAdherent(numeroAdherent);
        if (!adherentOpt.isPresent()) {
            throw new RuntimeException("Adherent not found with numero: " + numeroAdherent);
        }

        // Validate exemplaire exists
        Optional<Exemplaire> exemplaireOpt = exemplaireRepository.findById(exemplaireId);
        if (!exemplaireOpt.isPresent()) {
            throw new RuntimeException("Exemplaire not found with id: " + exemplaireId);
        }

        Adherent adherent = adherentOpt.get();
        Exemplaire exemplaire = exemplaireOpt.get();

        // Create reservation without validation - goes to admin for approval
        Reservation reservation = new Reservation();
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setDateDebutPret(dateDebutPret);
        reservation.setExemplaire(exemplaire);
        reservation.setAdherent(adherent);

        // Save reservation
        reservation = reservationRepository.save(reservation);

        // Create initial status - always starts as "en attente" for admin approval
        StatutReservation statutInitial = new StatutReservation(
            StatutReservation.StatutType.ATTENTE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutInitial);

        return reservation;
    }

    /**
     * Create a new reservation with custom reservation date (for testing)
     */
    public Reservation createReservation(String numeroAdherent, Long exemplaireId, LocalDateTime dateDebutPret, LocalDateTime dateReservation) {
        // Validate adherent exists
        Optional<Adherent> adherentOpt = adherentRepository.findByNumeroAdherent(numeroAdherent);
        if (!adherentOpt.isPresent()) {
            throw new RuntimeException("Adherent not found with numero: " + numeroAdherent);
        }

        // Validate exemplaire exists
        Optional<Exemplaire> exemplaireOpt = exemplaireRepository.findById(exemplaireId);
        if (!exemplaireOpt.isPresent()) {
            throw new RuntimeException("Exemplaire not found with id: " + exemplaireId);
        }

        Adherent adherent = adherentOpt.get();
        Exemplaire exemplaire = exemplaireOpt.get();

        // Create reservation without validation - goes to admin for approval
        Reservation reservation = new Reservation();
        reservation.setDateReservation(dateReservation != null ? dateReservation : LocalDateTime.now());
        reservation.setDateDebutPret(dateDebutPret);
        reservation.setExemplaire(exemplaire);
        reservation.setAdherent(adherent);

        // Save reservation
        reservation = reservationRepository.save(reservation);

        // Create initial status - always starts as "en attente" for admin approval
        StatutReservation statutInitial = new StatutReservation(
            StatutReservation.StatutType.ATTENTE,
            dateReservation != null ? dateReservation : LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutInitial);

        return reservation;
    }

    /**
     * Get all reservations
     */
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Get reservation by ID
     */
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Get reservations by adherent
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAdherent(Adherent adherent) {
        return reservationRepository.findByAdherent(adherent);
    }

    /**
     * Get reservations by adherent numero
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAdherentNumero(String numeroAdherent) {
        return reservationRepository.findByAdherentNumeroAdherent(numeroAdherent);
    }

    /**
     * Get reservations by exemplaire
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByExemplaire(Exemplaire exemplaire) {
        return reservationRepository.findByExemplaire(exemplaire);
    }

    /**
     * Get active reservations
     */
    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservations() {
        return reservationRepository.findActiveReservations();
    }

    /**
     * Get reservations within date range
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findByDateReservationBetween(startDate, endDate);
    }

    /**
     * Update reservation
     */
    public Reservation updateReservation(Long id, LocalDateTime dateDebutPret) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        Reservation reservation = reservationOpt.get();
        reservation.setDateDebutPret(dateDebutPret);
        
        return reservationRepository.save(reservation);
    }

    /**
     * Confirm a reservation
     */
    public void confirmReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();

        // Create confirmed status
        StatutReservation statutConfirme = new StatutReservation(
            StatutReservation.StatutType.CONFIRMEE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutConfirme);
    }

    /**
     * Cancel a reservation
     */
    public void cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();

        // Create cancelled status
        StatutReservation statutAnnule = new StatutReservation(
            StatutReservation.StatutType.ANNULEE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutAnnule);
    }

    /**
     * Expire a reservation
     */
    public void expireReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();

        // Create expired status
        StatutReservation statutExpire = new StatutReservation(
            StatutReservation.StatutType.EXPIREE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutExpire);
    }

    /**
     * Delete a reservation
     */
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        // Delete associated status changes first
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isPresent()) {
            statutReservationRepository.deleteByReservation(reservationOpt.get());
        }

        reservationRepository.deleteById(id);
    }

    /**
     * Get current status of a reservation
     */
    @Transactional(readOnly = true)
    public Optional<StatutReservation> getCurrentStatus(Long reservationId) {
        return statutReservationRepository.findLatestByReservationId(reservationId);
    }

    /**
     * Check if an adherent can make a new reservation
     */
    @Transactional(readOnly = true)
    public boolean canAdherentMakeReservation(Adherent adherent) {
        // Get active reservations for this adherent
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByAdherent(adherent);
        
        // Check against quota (assuming a maximum of 3 active reservations per adherent)
        // This could be configurable from a settings table
        return activeReservations.size() < 3;
    }

    /**
     * Check if an exemplaire is available for reservation
     */
    @Transactional(readOnly = true)
    public boolean isExemplaireAvailableForReservation(Exemplaire exemplaire) {
        // Check if exemplaire has active reservations
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByExemplaire(exemplaire);
        
        // An exemplaire can have multiple reservations (queue system)
        // But let's limit to 5 reservations per exemplaire
        return activeReservations.size() < 5;
    }

    /**
     * Get reservation queue for an exemplaire
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationQueue(Exemplaire exemplaire) {
        return reservationRepository.findActiveReservationsByExemplaire(exemplaire);
    }

    /**
     * Count active reservations for an adherent
     */
    @Transactional(readOnly = true)
    public long countActiveReservationsByAdherent(Adherent adherent) {
        return reservationRepository.countActiveReservationsByAdherent(adherent);
    }

    /**
     * Find reservations expiring soon
     */
    @Transactional(readOnly = true)
    public List<Reservation> findReservationsExpiringSoon(int daysAhead) {
        LocalDateTime limitDate = LocalDateTime.now().plusDays(daysAhead);
        return reservationRepository.findReservationsExpiringSoon(limitDate);
    }

    /**
     * Get pending reservations waiting for admin approval
     */
    @Transactional(readOnly = true)
    public List<Reservation> getPendingReservationsForApproval() {
        return reservationRepository.findPendingReservations();
    }

    /**
     * Admin approves a reservation (with business rules validation)
     */
    public void approveReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();
        Adherent adherent = reservation.getAdherent();
        Exemplaire exemplaire = reservation.getExemplaire();
        
        // Now apply business rules validation before approval
        if (!canAdherentMakeReservation(adherent)) {
            throw new RuntimeException("Adherent has reached maximum number of active reservations");
        }

        /*if (!isExemplaireAvailableForReservation(exemplaire)) {
            throw new RuntimeException("Exemplaire is not available for reservation");
        }

        // Check if adherent has any penalties or issues
        if (hasAdherentPenalties(adherent)) {
            throw new RuntimeException("Adherent has pending penalties or restrictions");
        }*/

        // Create confirmed status for reservation
        StatutReservation statutConfirme = new StatutReservation(
            StatutReservation.StatutType.CONFIRMEE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutConfirme);

        try {
            Optional<TypePret> typePretOpt = typePretService.findById(1L);
            if (!typePretOpt.isPresent()) {
                throw new RuntimeException("TypePret 'à emporter' with ID 1 not found");
            }
            TypePret typePretAEmporter = typePretOpt.get();

            // Create the loan using PretService - this will create it with DEMANDE status
            pretService.requestLoan(adherent, exemplaire, typePretAEmporter);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create loan from approved reservation: " + e.getMessage());
        }
    }

    /**
     * Admin rejects a reservation
     */
    public void rejectReservation(Long reservationId, String reason) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();

        // Create rejected status (using ANNULEE as rejection)
        StatutReservation statutRejete = new StatutReservation(
            StatutReservation.StatutType.ANNULEE,
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutRejete);
    }

    /**
     * Check if adherent has pending penalties (integrates with actual penalty system)
     */
    @Transactional(readOnly = true)
    public boolean hasAdherentPenalties(Adherent adherent) {
        // Check for active penalties
        return penaliteService.hasActivePenalties(adherent);
    }

    /**
     * Convert confirmed reservation to actual loan (pret) - this is where all business rules are checked
     */
    public void convertReservationToPret(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();
        
        // Check if reservation is confirmed
        Optional<StatutReservation> currentStatus = getCurrentStatus(reservationId);
        if (!currentStatus.isPresent() || 
            !StatutReservation.StatutType.CONFIRMEE.getLibelle().equals(currentStatus.get().getStatut())) {
            throw new RuntimeException("Reservation must be confirmed before converting to loan");
        }

        Adherent adherent = reservation.getAdherent();
        Exemplaire exemplaire = reservation.getExemplaire();

        // Apply ALL business rules here (both client and admin checks)
        validateForPretCreation(adherent, exemplaire);

        // Create actual Pret entity using existing PretService
        try {
            // Get loan eligibility to validate business rules
            PretService.LoanEligibilityResult eligibility = pretService.checkLoanEligibility(adherent, exemplaire);
            if (!eligibility.isEligible()) {
                throw new RuntimeException("Cannot create loan from reservation: " + eligibility.getReason());
            }

            // Get default TypePret with ID 1
            Optional<TypePret> typePretOpt = typePretService.findById(1L);
            if (!typePretOpt.isPresent()) {
                throw new RuntimeException("Default TypePret with ID 1 not found");
            }
            TypePret defaultTypePret = typePretOpt.get();

            // Create the loan using PretService - this will handle all the business logic
            // Use the desired start date from the reservation and default type "a emporter"
            Pret pret = pretService.requestLoan(adherent, exemplaire, defaultTypePret, reservation.getDateDebutPret());
            
            // Auto-approve the loan since it came from an approved reservation
            PretService.LoanApprovalResult approvalResult = pretService.approveLoan(pret.getId());
            if (!approvalResult.isSuccessful()) {
                throw new RuntimeException("Failed to approve loan from reservation: " + approvalResult.getMessage());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create loan from reservation: " + e.getMessage());
        }
        
        // Mark the reservation as completed
        StatutReservation statutComplete = new StatutReservation(
            StatutReservation.StatutType.EXPIREE, // You might want to create a new status like "COMPLETED"
            LocalDateTime.now(),
            reservation
        );
        statutReservationRepository.save(statutComplete);
    }

    /**
     * Validate all business rules for pret creation
     */
    private void validateForPretCreation(Adherent adherent, Exemplaire exemplaire) {
        // Use PretService's comprehensive eligibility check
        PretService.LoanEligibilityResult eligibility = pretService.checkLoanEligibility(adherent, exemplaire);
        if (!eligibility.isEligible()) {
            throw new RuntimeException("Cannot create loan: " + eligibility.getReason());
        }
        
        // Additional reservation-specific validations if needed
        if (hasAdherentPenalties(adherent)) {
            throw new RuntimeException("Client: Has pending penalties or restrictions");
        }
    }

    /**
     * Get reservations by status
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStatus(String status) {
        return reservationRepository.findReservationsByStatus(status);
    }
}
