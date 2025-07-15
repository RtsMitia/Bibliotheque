package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
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
public class StatutReservationService {

    private final StatutReservationRepository statutReservationRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public StatutReservationService(StatutReservationRepository statutReservationRepository,
                                   ReservationRepository reservationRepository) {
        this.statutReservationRepository = statutReservationRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Create a new status change for a reservation
     */
    public StatutReservation createStatutReservation(Long reservationId, String statut) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();
        StatutReservation statutReservation = new StatutReservation(
            statut,
            LocalDateTime.now(),
            reservation
        );

        return statutReservationRepository.save(statutReservation);
    }

    /**
     * Create a new status change using enum
     */
    public StatutReservation createStatutReservation(Long reservationId, StatutReservation.StatutType statutType) {
        return createStatutReservation(reservationId, statutType.getLibelle());
    }

    /**
     * Create a new status change for a reservation with specific date
     */
    public StatutReservation createStatutReservation(Long reservationId, String statut, LocalDateTime dateChangement) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }

        Reservation reservation = reservationOpt.get();
        StatutReservation statutReservation = new StatutReservation(
            statut,
            dateChangement,
            reservation
        );

        return statutReservationRepository.save(statutReservation);
    }

    /**
     * Get all status changes
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getAllStatutReservations() {
        return statutReservationRepository.findAll();
    }

    /**
     * Get status change by ID
     */
    @Transactional(readOnly = true)
    public Optional<StatutReservation> getStatutReservationById(Long id) {
        return statutReservationRepository.findById(id);
    }

    /**
     * Get all status changes for a specific reservation
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatutsByReservation(Reservation reservation) {
        return statutReservationRepository.findByReservation(reservation);
    }

    /**
     * Get all status changes for a reservation by ID
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatutsByReservationId(Long reservationId) {
        return statutReservationRepository.findByReservationId(reservationId);
    }

    /**
     * Get status changes by status type
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatutsByStatus(String statut) {
        return statutReservationRepository.findByStatut(statut);
    }

    /**
     * Get status changes by status type using enum
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatutsByStatus(StatutReservation.StatutType statutType) {
        return getStatutsByStatus(statutType.getLibelle());
    }

    /**
     * Get status changes within a date range
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatutsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return statutReservationRepository.findByDateChangementBetween(startDate, endDate);
    }

    /**
     * Get the latest status for a specific reservation
     */
    @Transactional(readOnly = true)
    public Optional<StatutReservation> getLatestStatusByReservation(Reservation reservation) {
        List<StatutReservation> statuts = statutReservationRepository.findByReservationOrderByDateChangementDesc(reservation);
        return statuts.isEmpty() ? Optional.empty() : Optional.of(statuts.get(0));
    }

    /**
     * Get the latest status for a reservation by ID
     */
    @Transactional(readOnly = true)
    public Optional<StatutReservation> getLatestStatusByReservationId(Long reservationId) {
        List<StatutReservation> statuts = statutReservationRepository.findByReservationId(reservationId);
        if (statuts.isEmpty()) {
            return Optional.empty();
        }
        // Sort by date descending and return the first (latest)
        return statuts.stream()
                .sorted((s1, s2) -> s2.getDateChangement().compareTo(s1.getDateChangement()))
                .findFirst();
    }

    /**
     * Get all reservations with a specific current status
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getReservationsWithCurrentStatus(String statut) {
        return statutReservationRepository.findReservationsWithCurrentStatus(statut);
    }

    /**
     * Get all reservations with a specific current status using enum
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getReservationsWithCurrentStatus(StatutReservation.StatutType statutType) {
        return getReservationsWithCurrentStatus(statutType.getLibelle());
    }

    /**
     * Count status changes for a specific reservation
     */
    @Transactional(readOnly = true)
    public long countStatusChangesByReservation(Reservation reservation) {
        return statutReservationRepository.countByReservation(reservation);
    }

    /**
     * Get status changes for a reservation with specific status
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatusByReservationAndStatus(Reservation reservation, String statut) {
        return statutReservationRepository.findByReservationAndStatut(reservation, statut);
    }

    /**
     * Get the first status change for a reservation (creation status)
     */
    @Transactional(readOnly = true)
    public Optional<StatutReservation> getFirstStatusByReservation(Reservation reservation) {
        List<StatutReservation> statuts = statutReservationRepository.findByReservation(reservation);
        return statuts.stream()
                .sorted((s1, s2) -> s1.getDateChangement().compareTo(s2.getDateChangement()))
                .findFirst();
    }

    /**
     * Get status changes for multiple reservations
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatusByReservationIds(List<Long> reservationIds) {
        return statutReservationRepository.findByReservationIds(reservationIds);
    }

    /**
     * Get status changes by date and status
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatusByDateAndStatus(LocalDateTime date, String statut) {
        return statutReservationRepository.findByStatutAndDate(statut, date);
    }

    /**
     * Check if a reservation has a specific status
     */
    @Transactional(readOnly = true)
    public boolean hasReservationStatus(Reservation reservation, String statut) {
        return statutReservationRepository.existsByReservationAndStatut(reservation, statut);
    }

    /**
     * Check if a reservation has a specific status using enum
     */
    @Transactional(readOnly = true)
    public boolean hasReservationStatus(Reservation reservation, StatutReservation.StatutType statutType) {
        return hasReservationStatus(reservation, statutType.getLibelle());
    }

    /**
     * Update status change
     */
    public StatutReservation updateStatutReservation(Long id, String newStatut) {
        Optional<StatutReservation> statutOpt = statutReservationRepository.findById(id);
        if (!statutOpt.isPresent()) {
            throw new RuntimeException("StatutReservation not found with id: " + id);
        }

        StatutReservation statut = statutOpt.get();
        statut.setStatut(newStatut);
        statut.setDateChangement(LocalDateTime.now());

        return statutReservationRepository.save(statut);
    }

    /**
     * Update status change using enum
     */
    public StatutReservation updateStatutReservation(Long id, StatutReservation.StatutType statutType) {
        return updateStatutReservation(id, statutType.getLibelle());
    }

    /**
     * Delete a status change
     */
    public void deleteStatutReservation(Long id) {
        if (!statutReservationRepository.existsById(id)) {
            throw new RuntimeException("StatutReservation not found with id: " + id);
        }
        statutReservationRepository.deleteById(id);
    }

    /**
     * Delete all status changes for a specific reservation
     */
    public void deleteStatusByReservation(Reservation reservation) {
        statutReservationRepository.deleteByReservation(reservation);
    }

    /**
     * Get current status label for a reservation
     */
    @Transactional(readOnly = true)
    public String getCurrentStatusLabel(Long reservationId) {
        Optional<StatutReservation> latestStatus = getLatestStatusByReservationId(reservationId);
        return latestStatus.map(StatutReservation::getStatut).orElse("Aucun statut");
    }

    /**
     * Get status history for a reservation (ordered by date)
     */
    @Transactional(readOnly = true)
    public List<StatutReservation> getStatusHistoryByReservation(Reservation reservation) {
        return statutReservationRepository.findByReservationOrderByDateChangementDesc(reservation);
    }

    /**
     * Check if a reservation is in a pending state
     */
    @Transactional(readOnly = true)
    public boolean isReservationPending(Long reservationId) {
        Optional<StatutReservation> currentStatus = getLatestStatusByReservationId(reservationId);
        return currentStatus.isPresent() && 
               StatutReservation.StatutType.ATTENTE.getLibelle().equals(currentStatus.get().getStatut());
    }

    /**
     * Check if a reservation is confirmed
     */
    @Transactional(readOnly = true)
    public boolean isReservationConfirmed(Long reservationId) {
        Optional<StatutReservation> currentStatus = getLatestStatusByReservationId(reservationId);
        return currentStatus.isPresent() && 
               StatutReservation.StatutType.CONFIRMEE.getLibelle().equals(currentStatus.get().getStatut());
    }

    /**
     * Check if a reservation is cancelled
     */
    @Transactional(readOnly = true)
    public boolean isReservationCancelled(Long reservationId) {
        Optional<StatutReservation> currentStatus = getLatestStatusByReservationId(reservationId);
        return currentStatus.isPresent() && 
               StatutReservation.StatutType.ANNULEE.getLibelle().equals(currentStatus.get().getStatut());
    }

    /**
     * Check if a reservation is expired
     */
    @Transactional(readOnly = true)
    public boolean isReservationExpired(Long reservationId) {
        Optional<StatutReservation> currentStatus = getLatestStatusByReservationId(reservationId);
        return currentStatus.isPresent() && 
               StatutReservation.StatutType.EXPIREE.getLibelle().equals(currentStatus.get().getStatut());
    }
}
