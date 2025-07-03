package com.rtsmitia.bibliotheque.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement;
import com.rtsmitia.bibliotheque.services.AdherentService;
import com.rtsmitia.bibliotheque.services.HistoriqueStatutAbonnementService;

@RestController
@RequestMapping("/api/status-history-abonnenment")
public class HistoriqueStatutAbonnementController {
    
    private final HistoriqueStatutAbonnementService historiqueService;
    private final AdherentService adherentService;
    
    @Autowired
    public HistoriqueStatutAbonnementController(HistoriqueStatutAbonnementService historiqueService, 
                                               AdherentService adherentService) {
        this.historiqueService = historiqueService;
        this.adherentService = adherentService;
    }
    
    @GetMapping("/adherent/{adherentId}")
    public List<HistoriqueStatutAbonnement> getStatusHistoryByAdherent(@PathVariable Long adherentId) {
        return historiqueService.getStatusHistoryByAdherent(adherentId);
    }
    
    @GetMapping("/status/{statut}")
    public List<HistoriqueStatutAbonnement> getStatusHistoryByStatut(@PathVariable StatutAbonnement statut) {
        return historiqueService.getStatusHistoryByStatut(statut);
    }
    
    @GetMapping("/adherent/{adherentId}/latest")
    public HistoriqueStatutAbonnement getMostRecentStatusByAdherentId(@PathVariable Long adherentId) {
        return historiqueService.getMostRecentStatusByAdherentId(adherentId);
    }
    

    @PostMapping("/create")
    public HistoriqueStatutAbonnement createStatusHistory(@RequestParam Long adherentId, 
                                                         @RequestParam StatutAbonnement statut) {
        List<Adherent> allAdherents = adherentService.getAllAdherents();
        Adherent adherent = allAdherents.stream()
                .filter(a -> a.getId().equals(adherentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Adherent not found with ID: " + adherentId));
        
        return historiqueService.createStatusHistory(adherent, statut);
    }
}
