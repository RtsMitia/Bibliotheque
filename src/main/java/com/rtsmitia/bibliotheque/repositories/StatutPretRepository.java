package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.StatutPret;
import com.rtsmitia.bibliotheque.models.StatutPret.StatutPretEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatutPretRepository extends JpaRepository<StatutPret, Long> {
    
    /**
     * Find all status entries for a specific loan
     */
    List<StatutPret> findByPret(Pret pret);
    
    /**
     * Find all status entries for a specific loan ordered by date
     */
    @Query("SELECT sp FROM StatutPret sp WHERE sp.pret = :pret ORDER BY sp.dateChangement DESC")
    List<StatutPret> findByPretOrderByDateChangementDesc(@Param("pret") Pret pret);
    
    /**
     * Find the current (latest) status for a specific loan
     */
    @Query("SELECT sp FROM StatutPret sp WHERE sp.pret = :pret ORDER BY sp.dateChangement DESC")
    Optional<StatutPret> findCurrentStatusByPret(@Param("pret") Pret pret);
    
    /**
     * Find all loans with a specific status
     */
    List<StatutPret> findByStatut(StatutPretEnum statut);
    
    /**
     * Find all loans with pending status (demande or en_attente)
     */
    @Query("SELECT sp FROM StatutPret sp WHERE sp.statut IN ('demande', 'en_attente')")
    List<StatutPret> findPendingStatuses();
    
    /**
     * Find all loans with valid status
     */
    @Query("SELECT sp FROM StatutPret sp WHERE sp.statut = 'valide'")
    List<StatutPret> findValidStatuses();
    
    /**
     * Find all loans with refused status
     */
    @Query("SELECT sp FROM StatutPret sp WHERE sp.statut = 'refuse'")
    List<StatutPret> findRefusedStatuses();
}
