package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.AdherentAbonnement;
import com.rtsmitia.bibliotheque.models.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentAbonnementRepository extends JpaRepository<AdherentAbonnement, Long> {

    // Find all abonnements for a specific adherent
    List<AdherentAbonnement> findByAdherent(Adherent adherent);

    // Find active abonnements (current date is between debut and fin)
    @Query("SELECT a FROM AdherentAbonnement a WHERE :currentDate BETWEEN a.debutAbonnement AND a.finAbonnement")
    List<AdherentAbonnement> findActiveAbonnements(@Param("currentDate") LocalDate currentDate);

    // Find current active abonnement for a specific adherent
    @Query("SELECT a FROM AdherentAbonnement a WHERE a.adherent = :adherent AND :currentDate BETWEEN a.debutAbonnement AND a.finAbonnement")
    Optional<AdherentAbonnement> findCurrentAbonnementByAdherent(@Param("adherent") Adherent adherent, @Param("currentDate") LocalDate currentDate);

    // Find expired abonnements
    @Query("SELECT a FROM AdherentAbonnement a WHERE a.finAbonnement < :currentDate")
    List<AdherentAbonnement> findExpiredAbonnements(@Param("currentDate") LocalDate currentDate);

    // Find abonnements ending soon (within specified days)
    @Query("SELECT a FROM AdherentAbonnement a WHERE a.finAbonnement BETWEEN :currentDate AND :endDate")
    List<AdherentAbonnement> findAbonnementsEndingSoon(@Param("currentDate") LocalDate currentDate, @Param("endDate") LocalDate endDate);
}
