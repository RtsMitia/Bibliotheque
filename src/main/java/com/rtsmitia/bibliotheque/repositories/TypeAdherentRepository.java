package com.rtsmitia.bibliotheque.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtsmitia.bibliotheque.models.TypeAdherent;

@Repository
public interface TypeAdherentRepository extends JpaRepository<TypeAdherent, Long> {
   
}
