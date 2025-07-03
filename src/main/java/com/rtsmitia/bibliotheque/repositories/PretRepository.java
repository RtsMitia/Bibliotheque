package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.models.StatutPret.StatutPretEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PretRepository extends JpaRepository<Pret, Long> {
    
    /**
     * Find all loans for a specific adherent
     */
    List<Pret> findByAdherent(Adherent adherent);
    
    /**
     * Find active loans for an adherent (not returned yet)
     */
    @Query("SELECT p FROM Pret p WHERE p.adherent = :adherent AND p.dateRetour IS NULL AND p.dateDebut IS NOT NULL")
    List<Pret> findActiveLoansByAdherent(@Param("adherent") Adherent adherent);
    
    /**
     * Count active loans for an adherent
     */
    @Query("SELECT COUNT(p) FROM Pret p WHERE p.adherent = :adherent AND p.dateRetour IS NULL AND p.dateDebut IS NOT NULL")
    int countActiveLoansByAdherent(@Param("adherent") Adherent adherent);
    
    /**
     * Find loans with a specific status
     */
    @Query("SELECT DISTINCT p FROM Pret p JOIN p.statutsPret sp WHERE sp.statut = :statut " +
           "AND sp.dateChangement = (SELECT MAX(sp2.dateChangement) FROM StatutPret sp2 WHERE sp2.pret = p)")
    List<Pret> findLoansWithStatus(@Param("statut") StatutPretEnum statut);
    
    /**
     * Find overdue loans
     */
    @Query("SELECT p FROM Pret p WHERE p.dateRetour IS NULL AND p.dateFin < CURRENT_TIMESTAMP")
    List<Pret> findOverdueLoans();
    
    /**
     * Find loans due soon (within X days)
     */
    @Query("SELECT p FROM Pret p WHERE p.dateRetour IS NULL AND p.dateFin BETWEEN CURRENT_TIMESTAMP AND :endDate")
    List<Pret> findLoansDueSoon(@Param("endDate") java.time.LocalDateTime endDate);
    
    /**
     * Find all pending loan requests (demande status)
     */
    @Query("SELECT DISTINCT p FROM Pret p JOIN p.statutsPret sp WHERE sp.statut = 'demande' " +
           "AND sp.dateChangement = (SELECT MAX(sp2.dateChangement) FROM StatutPret sp2 WHERE sp2.pret = p)")
    List<Pret> findPendingLoanRequests();
    
    /**
     * Find loans by adherent and status
     */
    @Query("SELECT DISTINCT p FROM Pret p JOIN p.statutsPret sp WHERE p.adherent = :adherent AND sp.statut = :statut " +
           "AND sp.dateChangement = (SELECT MAX(sp2.dateChangement) FROM StatutPret sp2 WHERE sp2.pret = p)")
    List<Pret> findByAdherentAndStatus(@Param("adherent") Adherent adherent, @Param("statut") StatutPretEnum statut);
    
    /**
     * Find overdue loans for a specific adherent
     */
    @Query("SELECT p FROM Pret p WHERE p.adherent = :adherent AND p.dateRetour IS NULL AND p.dateFin < CURRENT_TIMESTAMP")
    List<Pret> findOverdueLoansForAdherent(@Param("adherent") Adherent adherent);
}
