package com.rtsmitia.bibliotheque.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rtsmitia.bibliotheque.models.TypeAdherent;
import com.rtsmitia.bibliotheque.repositories.TypeAdherentRepository;

import java.util.List;

@Service
public class TypeAdherentService {

    private final TypeAdherentRepository typeAdherentRepository;

    @Autowired
    public TypeAdherentService(TypeAdherentRepository typeAdherentRepository) {
        this.typeAdherentRepository = typeAdherentRepository;
    }
    
    public List<TypeAdherent> getAllTypes() {
        return typeAdherentRepository.findAll();
    }


}
