package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.TypePret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypePretRepository extends JpaRepository<TypePret, Long> {
    
    /**
     * Find TypePret by libelle
     */
    Optional<TypePret> findByLibelle(String libelle);
    
    /**
     * Check if TypePret exists by libelle
     */
    boolean existsByLibelle(String libelle);
}
