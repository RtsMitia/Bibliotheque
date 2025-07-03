package com.rtsmitia.bibliotheque.services;

import java.time.LocalDateTime;
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
    private final AdherentAbonnementService abonnementService;
    
    @Autowired
    private PenaliteService penaliteService;
    
    @Autowired
    private QuotaService quotaService;
    
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
    
    /**
     * Validate client number and return adherent if valid
     */
    public Optional<Adherent> validateClientNumber(String numeroAdherent) {
        return adherentRepository.findByNumeroAdherent(numeroAdherent);
    }
    
    /**
     * Check if adherent has valid subscription status
     */
    public boolean hasValidSubscription(Adherent adherent) {
        // Check if adherent has 'valide' status (active subscription)
        List<Adherent> validAdherents = getAdherentsByStatut(StatutAbonnement.valide);
        return validAdherents.stream()
                .anyMatch(a -> a.getId().equals(adherent.getId()));
    }
    
    /**
     * Check if adherent can borrow books (has valid subscription and no penalties)
     */
    public boolean canBorrowBooks(Adherent adherent) {
        return hasValidSubscription(adherent) && penaliteService.canBorrow(adherent);
    }
    
    /**
     * Get borrowing information for an adherent
     */
    public String getBorrowingStatus(Adherent adherent) {
        if (!hasValidSubscription(adherent)) {
            return "Abonnement non valide";
        }
        
        if (penaliteService.hasActivePenalties(adherent)) {
            return "Pénalité en cours";
        }
        
        return "Peut emprunter";
    }
    
    /**
     * Get quota information for an adherent
     */
    public String getQuotaInfo(Adherent adherent) {
        int maxBooks = quotaService.getMaxBooksAllowed(adherent);
        int maxDays = quotaService.getMaxLoanDays(adherent);
        int maxReservations = quotaService.getMaxReservationsAllowed(adherent);
        
        return String.format("Max livres: %d, Durée: %d jours, Max réservations: %d", 
                           maxBooks, maxDays, maxReservations);
    }
    
    /**
     * Get adherent by ID with contraintes loaded
     */
    @Transactional(readOnly = true)
    public Optional<Adherent> getAdherentWithContraintes(Long adherentId) {
        Optional<Adherent> adherentOpt = adherentRepository.findById(adherentId.intValue());
        if (adherentOpt.isPresent()) {
            Adherent adherent = adherentOpt.get();
            // Force initialization of contraintes collection
            adherent.getContraintes().size();
            return Optional.of(adherent);
        }
        return Optional.empty();
    }
}
