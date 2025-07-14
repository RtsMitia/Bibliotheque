package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
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

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                             StatutReservationRepository statutReservationRepository,
                             AdherentRepository adherentRepository,
                             ExemplaireRepository exemplaireRepository) {
        this.reservationRepository = reservationRepository;
        this.statutReservationRepository = statutReservationRepository;
        this.adherentRepository = adherentRepository;
        this.exemplaireRepository = exemplaireRepository;
    }

    /**
     * Create a new reservation
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

        // Check if adherent can make more reservations
        if (!canAdherentMakeReservation(adherent)) {
            throw new RuntimeException("Adherent has reached maximum number of active reservations");
        }

        // Check if exemplaire is available for reservation
        if (!isExemplaireAvailableForReservation(exemplaire)) {
            throw new RuntimeException("Exemplaire is not available for reservation");
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setDateDebutPret(dateDebutPret);
        reservation.setExemplaire(exemplaire);
        reservation.setAdherent(adherent);

        // Save reservation
        reservation = reservationRepository.save(reservation);

        // Create initial status
        StatutReservation statutInitial = new StatutReservation(
            StatutReservation.StatutType.ATTENTE,
            LocalDateTime.now(),
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
}
