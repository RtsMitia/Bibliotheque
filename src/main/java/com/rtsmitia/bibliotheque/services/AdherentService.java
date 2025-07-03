package com.rtsmitia.bibliotheque.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement;
import com.rtsmitia.bibliotheque.repositories.AdherentRepository;

@Service
public class AdherentService {
    private final AdherentRepository adherentRepository;
    private final HistoriqueStatutAbonnementService historiqueStatutService;
    
    @Autowired
    public AdherentService(AdherentRepository adherentRepository, 
                          HistoriqueStatutAbonnementService historiqueStatutService) {
        this.adherentRepository = adherentRepository;
        this.historiqueStatutService = historiqueStatutService;
    }

    @Transactional
    public Adherent saveAdherent(Adherent adherent) {
        Optional<Adherent> existingAdherent = adherentRepository.findByEmail(adherent.getEmail());
        if (existingAdherent.isPresent()) {
            throw new IllegalArgumentException("Un adhérent avec cet email existe déjà.");
        } else {
            Adherent savedAdherent = adherentRepository.save(adherent);
            
            historiqueStatutService.createStatusHistory(savedAdherent, StatutAbonnement.demande);
            
            return savedAdherent;
        }
    }
    
    public List<Adherent> getAdherentsWithStatusDemande() {
        return adherentRepository.findAdherentsWithStatusDemande();
    }
    
    public List<Adherent> getAdherentsByStatut(StatutAbonnement statut) {
        return adherentRepository.findAdherentsByStatut(statut);
    }
    
    public List<Adherent> getAllAdherents() {
        return adherentRepository.findAll();
    }
    
    @Transactional
    public void approveAdherent(Long adherentId) {
        Adherent adherent = adherentRepository.findById(adherentId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Adherent not found with ID: " + adherentId));
        
        if (!isAdherentInDemande(adherent)) {
            throw new IllegalArgumentException("Cet adhérent n'est pas en attente de validation");
        }
        
        historiqueStatutService.createStatusHistory(adherent, StatutAbonnement.valide);
    }
    
    @Transactional
    public void rejectAdherent(Long adherentId) {
        Adherent adherent = adherentRepository.findById(adherentId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Adherent not found with ID: " + adherentId));
        
        if (!isAdherentInDemande(adherent)) {
            throw new IllegalArgumentException("Cet adhérent n'est pas en attente de validation");
        }
        
        historiqueStatutService.createStatusHistory(adherent, StatutAbonnement.refuse);
    }
    
    private boolean isAdherentInDemande(Adherent adherent) {
        List<Adherent> adherentsWithDemande = getAdherentsWithStatusDemande();
        return adherentsWithDemande.stream()
                .anyMatch(a -> a.getId().equals(adherent.getId()));
    }
}
