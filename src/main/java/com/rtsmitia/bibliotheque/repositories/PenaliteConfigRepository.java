package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.PenaliteConfig;
import com.rtsmitia.bibliotheque.models.TypeAdherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenaliteConfigRepository extends JpaRepository<PenaliteConfig, Long> {
    
    /**
     * Find the current penalty configuration for a specific adherent type
     */
    @Query("SELECT pc FROM PenaliteConfig pc WHERE pc.typeAdherent = :typeAdherent ORDER BY pc.dateChangement DESC")
    Optional<PenaliteConfig> findCurrentByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
    
    /**
     * Find all penalty configurations for a specific adherent type
     */
    @Query("SELECT pc FROM PenaliteConfig pc WHERE pc.typeAdherent = :typeAdherent ORDER BY pc.dateChangement DESC")
    java.util.List<PenaliteConfig> findAllByTypeAdherent(@Param("typeAdherent") TypeAdherent typeAdherent);
}
