package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.LesContraints;
import com.rtsmitia.bibliotheque.repositories.LesContraintsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LesContraintsService {

    private final LesContraintsRepository contraintsRepository;

    @Autowired
    public LesContraintsService(LesContraintsRepository contraintsRepository) {
        this.contraintsRepository = contraintsRepository;
    }

    public List<LesContraints> getAllContraintes() {
        return contraintsRepository.findAllOrderByTypeContrainte();
    }

    public Optional<LesContraints> getContrainteById(Long id) {
        return contraintsRepository.findById(id);
    }

    public LesContraints saveContrainte(LesContraints contrainte) {
        return contraintsRepository.save(contrainte);
    }

    public void deleteContrainte(Long id) {
        contraintsRepository.deleteById(id);
    }

    public boolean existsByTypeContrainte(String typeContrainte) {
        return contraintsRepository.existsByTypeContrainte(typeContrainte);
    }

    public List<LesContraints> searchContraintes(String searchText) {
        return contraintsRepository.findByTypeContrainteContainingIgnoreCase(searchText);
    }
}
