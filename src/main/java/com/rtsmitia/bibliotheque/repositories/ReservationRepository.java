package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find all reservations for a specific adherent
     */
    List<Reservation> findByAdherent(Adherent adherent);

    /**
     * Find all reservations for a specific exemplaire
     */
    List<Reservation> findByExemplaire(Exemplaire exemplaire);

    /**
     * Find reservations by adherent ID
     */
    List<Reservation> findByAdherentId(Long adherentId);

    /**
     * Find reservations by exemplaire ID
     */
    List<Reservation> findByExemplaireId(Long exemplaireId);

    /**
     * Find active reservations for an adherent (not yet processed)
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE r.adherent = :adherent " +
           "AND sr.statut IN ('en attente', 'confirmée') " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findActiveReservationsByAdherent(@Param("adherent") Adherent adherent);

    /**
     * Count active reservations for an adherent
     */
    @Query("SELECT COUNT(r) FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE r.adherent = :adherent " +
           "AND sr.statut IN ('en attente', 'confirmée') " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    int countActiveReservationsByAdherent(@Param("adherent") Adherent adherent);

    /**
     * Find reservations with a specific status
     */
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE sr.statut = :statut " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findReservationsByStatus(@Param("statut") String statut);

    /**
     * Find pending reservations (en attente status)
     */
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE sr.statut = 'en attente' " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findPendingReservations();

    /**
     * Find confirmed reservations
     */
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE sr.statut = 'confirmée' " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findConfirmedReservations();

    /**
     * Find expired reservations
     */
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE sr.statut = 'expirée' " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findExpiredReservations();

    /**
     * Find reservations due soon (within X days from dateDebutPret)
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateDebutPret BETWEEN :currentDate AND :endDate")
    List<Reservation> findReservationsDueSoon(@Param("currentDate") LocalDateTime currentDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Find reservations within a date range
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateReservation BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Check if an adherent has already reserved a specific exemplaire
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.adherent = :adherent AND r.exemplaire = :exemplaire " +
           "AND EXISTS (SELECT sr FROM StatutReservation sr WHERE sr.reservation = r " +
           "AND sr.statut IN ('en attente', 'confirmée') " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r))")
    boolean hasActiveReservationForExemplaire(@Param("adherent") Adherent adherent, 
                                            @Param("exemplaire") Exemplaire exemplaire);

    /**
     * Find reservations for a specific book (by livre ID)
     */
    @Query("SELECT r FROM Reservation r WHERE r.exemplaire.livre.id = :livreId")
    List<Reservation> findReservationsByLivreId(@Param("livreId") Long livreId);

    /**
     * Find all reservations ordered by reservation date (most recent first)
     */
    List<Reservation> findAllByOrderByDateReservationDesc();

    /**
     * Find reservations by adherent numero (string-based)
     */
    @Query("SELECT r FROM Reservation r WHERE r.adherent.numeroAdherent = :numeroAdherent")
    List<Reservation> findByAdherentNumeroAdherent(@Param("numeroAdherent") String numeroAdherent);

    /**
     * Find all active reservations (en attente or confirmée)
     */
    @Query("SELECT DISTINCT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE sr.statut IN ('en attente', 'confirmée') " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findActiveReservations();

    /**
     * Find reservations within a date range by reservation date
     */
    List<Reservation> findByDateReservationBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find active reservations for a specific exemplaire
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE r.exemplaire = :exemplaire " +
           "AND sr.statut IN ('en attente', 'confirmée') " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r) " +
           "ORDER BY r.dateReservation ASC")
    List<Reservation> findActiveReservationsByExemplaire(@Param("exemplaire") Exemplaire exemplaire);

    /**
     * Find reservations expiring soon
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN r.statutReservations sr " +
           "WHERE r.dateDebutPret <= :limitDate " +
           "AND sr.statut = 'confirmée' " +
           "AND sr.dateChangement = (SELECT MAX(sr2.dateChangement) FROM StatutReservation sr2 WHERE sr2.reservation = r)")
    List<Reservation> findReservationsExpiringSoon(@Param("limitDate") LocalDateTime limitDate);
}
