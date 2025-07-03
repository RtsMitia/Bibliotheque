package com.rtsmitia.bibliotheque.services;

import com.rtsmitia.bibliotheque.models.TypePret;
import com.rtsmitia.bibliotheque.repositories.TypePretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypePretService {
    
    @Autowired
    private TypePretRepository typePretRepository;
    
    /**
     * Get all loan types
     */
    public List<TypePret> getAllTypePrets() {
        return typePretRepository.findAll();
    }
    
    /**
     * Find loan type by ID
     */
    public Optional<TypePret> findById(Long id) {
        return typePretRepository.findById(id);
    }
    
    /**
     * Find loan type by libelle
     */
    public Optional<TypePret> findByLibelle(String libelle) {
        return typePretRepository.findByLibelle(libelle);
    }
    
    /**
     * Save loan type
     */
    public TypePret save(TypePret typePret) {
        return typePretRepository.save(typePret);
    }
    
    /**
     * Create new loan type
     */
    public TypePret createTypePret(String libelle) {
        if (typePretRepository.existsByLibelle(libelle)) {
            throw new IllegalArgumentException("Type de prêt avec ce libellé existe déjà");
        }
        
        TypePret typePret = new TypePret(libelle);
        return typePretRepository.save(typePret);
    }
    
    /**
     * Delete loan type
     */
    public void delete(TypePret typePret) {
        typePretRepository.delete(typePret);
    }
    
    /**
     * Delete loan type by ID
     */
    public void deleteById(Long id) {
        typePretRepository.deleteById(id);
    }
    
    /**
     * Check if loan type exists
     */
    public boolean exists(Long id) {
        return typePretRepository.existsById(id);
    }
}
