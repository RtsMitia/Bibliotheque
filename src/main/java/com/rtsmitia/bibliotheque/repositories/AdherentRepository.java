package com.rtsmitia.bibliotheque.repositories;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rtsmitia.bibliotheque.models.Adherent;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Integer>{
    
    Optional<Adherent> findByEmail(String email);
}
