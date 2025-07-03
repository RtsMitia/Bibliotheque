package com.rtsmitia.bibliotheque.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement;
import com.rtsmitia.bibliotheque.repositories.HistoriqueStatutAbonnementRepository;

@Service
public class HistoriqueStatutAbonnementService {
    
    private final HistoriqueStatutAbonnementRepository historiqueRepository;
    
    @Autowired
    public HistoriqueStatutAbonnementService(HistoriqueStatutAbonnementRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }
    
    public HistoriqueStatutAbonnement createStatusHistory(Adherent adherent, StatutAbonnement statut) {
        HistoriqueStatutAbonnement historique = new HistoriqueStatutAbonnement();
        historique.setAdherent(adherent);
        historique.setStatut(statut);
        historique.setDateChangement(LocalDate.now());
        
        return historiqueRepository.save(historique);
    }
    
    public List<HistoriqueStatutAbonnement> getStatusHistoryByAdherent(Long adherentId) {
        return historiqueRepository.findByAdherentId(adherentId);
    }
    
    public List<HistoriqueStatutAbonnement> getStatusHistoryByStatut(StatutAbonnement statut) {
        return historiqueRepository.findByStatut(statut);
    }
    

    public HistoriqueStatutAbonnement getMostRecentStatusByAdherentId(Long adherentId) {
        List<HistoriqueStatutAbonnement> statuses = historiqueRepository.findMostRecentStatusByAdherentId(adherentId);
        return statuses.isEmpty() ? null : statuses.get(0);
    }
}
