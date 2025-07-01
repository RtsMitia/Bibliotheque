package com.rtsmitia.bibliotheque.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.repositories.AdherentRepository;

@Service
public class AdherentService {
    private final AdherentRepository adherentRepository;
    
    @Autowired
    public AdherentService(AdherentRepository adherentRepository) {
        this.adherentRepository = adherentRepository;
    }

    public Adherent saveAdherent(Adherent adherent) {
        Optional<Adherent> existingAdherent = adherentRepository.findByEmail(adherent.getEmail());
        if (existingAdherent.isPresent()) {
            throw new IllegalArgumentException("Un adhérent avec cet email existe déjà.");
        } else {
            return adherentRepository.save(adherent);
        }
    }
    
}
