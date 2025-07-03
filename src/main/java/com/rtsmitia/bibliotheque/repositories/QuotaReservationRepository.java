package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.QuotaReservation;
import com.rtsmitia.bibliotheque.models.TypeAdherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotaReservationRepository extends JpaRepository<QuotaReservation, Long> {
    
    /**
     * Find the current reservation quota for a specific adherent type
     */
    @Query("SELECT qr FROM QuotaReservation qr WHERE qr.typeAdherent = :typeAdherent ORDER BY qr.dateChangement DESC")
    Optional<QuotaReservation> findCurrentByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
    
    /**
     * Find all reservation quotas for a specific adherent type
     */
    @Query("SELECT qr FROM QuotaReservation qr WHERE qr.typeAdherent = :typeAdherent ORDER BY qr.dateChangement DESC")
    java.util.List<QuotaReservation> findAllByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
}
