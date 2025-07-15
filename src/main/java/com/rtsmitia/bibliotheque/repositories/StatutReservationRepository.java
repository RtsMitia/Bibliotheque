package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatutReservationRepository extends JpaRepository<StatutReservation, Long> {

    /**
     * Find all status changes for a specific reservation
     */
    List<StatutReservation> findByReservation(Reservation reservation);

    /**
     * Find status changes by reservation ID
     */
    List<StatutReservation> findByReservationId(Long reservationId);

    /**
     * Find status changes by status type
     */
    List<StatutReservation> findByStatut(String statut);

    /**
     * Find status changes within a date range
     */
    List<StatutReservation> findByDateChangementBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find the latest status for a specific reservation
     */
    @Query("SELECT sr FROM StatutReservation sr WHERE sr.reservation = :reservation " +
           "ORDER BY sr.dateChangement DESC")
    List<StatutReservation> findByReservationOrderByDateChangementDesc(@Param("reservation") Reservation reservation);

    /**
     * Find the latest status for a specific reservation (single result)
     */
    @Query(value = "SELECT sr FROM StatutReservation sr WHERE sr.reservation = :reservation " +
           "ORDER BY sr.dateChangement DESC")
    List<StatutReservation> findByReservationOrderByDateChangementDescList(@Param("reservation") Reservation reservation);
    
    /**
     * Find the latest status for a specific reservation (single result)
     */
    default Optional<StatutReservation> findLatestByReservation(Reservation reservation) {
        List<StatutReservation> results = findByReservationOrderByDateChangementDescList(reservation);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find the latest status for a reservation by ID
     */
    @Query(value = "SELECT sr FROM StatutReservation sr WHERE sr.reservation.id = :reservationId " +
           "ORDER BY sr.dateChangement DESC")
    List<StatutReservation> findByReservationIdOrderByDateChangementDesc(@Param("reservationId") Long reservationId);
    
    /**
     * Find the latest status for a reservation by ID (single result)
     */
    default Optional<StatutReservation> findLatestByReservationId(Long reservationId) {
        List<StatutReservation> results = findByReservationIdOrderByDateChangementDesc(reservationId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find all reservations with a specific current status
     */
    @Query("SELECT sr1 FROM StatutReservation sr1 WHERE sr1.statut = :statut " +
           "AND sr1.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 " +
           "WHERE sr2.reservation = sr1.reservation)")
    List<StatutReservation> findReservationsWithCurrentStatus(@Param("statut") String statut);

    /**
     * Count status changes for a specific reservation
     */
    long countByReservation(Reservation reservation);

    /**
     * Find status changes by reservation and status
     */
    List<StatutReservation> findByReservationAndStatut(Reservation reservation, String statut);

    /**
     * Find the first status change for a reservation (creation)
     */
    @Query("SELECT sr FROM StatutReservation sr WHERE sr.reservation = :reservation " +
           "ORDER BY sr.dateChangement ASC")
    Optional<StatutReservation> findFirstByReservation(@Param("reservation") Reservation reservation);

    /**
     * Find status changes for multiple reservations
     */
    @Query("SELECT sr FROM StatutReservation sr WHERE sr.reservation.id IN :reservationIds " +
           "ORDER BY sr.dateChangement DESC")
    List<StatutReservation> findByReservationIds(@Param("reservationIds") List<Long> reservationIds);

    /**
     * Delete all status changes for a specific reservation
     */
    void deleteByReservation(Reservation reservation);

    /**
     * Find status changes by date and status
     */
    @Query("SELECT sr FROM StatutReservation sr WHERE sr.statut = :statut " +
           "AND DATE(sr.dateChangement) = DATE(:date)")
    List<StatutReservation> findByStatutAndDate(@Param("statut") String statut, @Param("date") LocalDateTime date);

    /**
     * Check if a reservation has a specific status
     */
    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN true ELSE false END " +
           "FROM StatutReservation sr WHERE sr.reservation = :reservation AND sr.statut = :statut")
    boolean existsByReservationAndStatut(@Param("reservation") Reservation reservation, @Param("statut") String statut);
}
