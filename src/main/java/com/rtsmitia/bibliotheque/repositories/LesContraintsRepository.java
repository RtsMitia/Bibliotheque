package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.LesContraints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LesContraintsRepository extends JpaRepository<LesContraints, Long> {

    // Find constraint by type
    Optional<LesContraints> findByTypeContrainte(String typeContrainte);

    // Find constraints containing a specific text (case-insensitive)
    @Query("SELECT c FROM LesContraints c WHERE LOWER(c.typeContrainte) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<LesContraints> findByTypeContrainteContainingIgnoreCase(@Param("text") String text);

    // Check if a constraint type exists
    boolean existsByTypeContrainte(String typeContrainte);

    // Find all constraints ordered by type
    @Query("SELECT c FROM LesContraints c ORDER BY c.typeContrainte ASC")
    List<LesContraints> findAllOrderByTypeContrainte();
}
