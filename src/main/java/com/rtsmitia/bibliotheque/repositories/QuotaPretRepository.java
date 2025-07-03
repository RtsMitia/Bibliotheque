package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.QuotaPret;
import com.rtsmitia.bibliotheque.models.TypeAdherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotaPretRepository extends JpaRepository<QuotaPret, Long> {
    
    /**
     * Find the current loan quota for a specific adherent type
     */
    @Query("SELECT qp FROM QuotaPret qp WHERE qp.typeAdherent = :typeAdherent ORDER BY qp.dateChangement DESC")
    Optional<QuotaPret> findCurrentByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
    
    /**
     * Find all loan quotas for a specific adherent type
     */
    @Query("SELECT qp FROM QuotaPret qp WHERE qp.typeAdherent = :typeAdherent ORDER BY qp.dateChangement DESC")
    java.util.List<QuotaPret> findAllByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
}
