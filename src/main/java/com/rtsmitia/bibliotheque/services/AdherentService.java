package com.rtsmitia.bibliotheque.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement;
import com.rtsmitia.bibliotheque.repositories.AdherentRepository;

@Service
public class AdherentService {
    private final AdherentRepository adherentRepository;
    private final HistoriqueStatutAbonnementService historiqueStatutService;
    private final AdherentAbonnementService abonnementService;
    
    @Autowired
    public AdherentService(AdherentRepository adherentRepository, 
                          HistoriqueStatutAbonnementService historiqueStatutService,
                          AdherentAbonnementService abonnementService) {
        this.adherentRepository = adherentRepository;
        this.historiqueStatutService = historiqueStatutService;
        this.abonnementService = abonnementService;
    }

    @Transactional
    public Adherent saveAdherent(Adherent adherent, LocalDateTime dateInscription) {
        Optional<Adherent> existingAdherent = adherentRepository.findByEmail(adherent.getEmail());
        if (existingAdherent.isPresent()) {
            throw new IllegalArgumentException("Un adhérent avec cet email existe déjà.");
        } else {
            Adherent savedAdherent = adherentRepository.save(adherent);
            
            historiqueStatutService.createStatusHistory(savedAdherent, StatutAbonnement.demande, dateInscription);
            
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
    public void approveAdherent(Long adherentId, LocalDateTime dateChangement) {
        Adherent adherent = adherentRepository.findById(adherentId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Adherent not found with ID: " + adherentId));
        
        if (!isAdherentInDemande(adherent)) {
            throw new IllegalArgumentException("Cet adhérent n'est pas en attente de validation");
        }
        
        // Update status to 'valide'
        historiqueStatutService.createStatusHistory(adherent, StatutAbonnement.valide, dateChangement);
        
        // Create abonnement (1 year subscription by default)
        abonnementService.createAbonnement(adherent, dateChangement);
    }
    
    @Transactional
    public void rejectAdherent(Long adherentId, LocalDateTime dateChangement) {
        Adherent adherent = adherentRepository.findById(adherentId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Adherent not found with ID: " + adherentId));
        
        if (!isAdherentInDemande(adherent)) {
            throw new IllegalArgumentException("Cet adhérent n'est pas en attente de validation");
        }
        
        historiqueStatutService.createStatusHistory(adherent, StatutAbonnement.refuse, dateChangement);
    }
    
    private boolean isAdherentInDemande(Adherent adherent) {
        List<Adherent> adherentsWithDemande = getAdherentsWithStatusDemande();
        return adherentsWithDemande.stream()
                .anyMatch(a -> a.getId().equals(adherent.getId()));
    }
}
