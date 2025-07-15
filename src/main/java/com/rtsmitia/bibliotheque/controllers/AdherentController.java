package com.rtsmitia.bibliotheque.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rtsmitia.bibliotheque.dto.AdherentDetailsDto;
import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.AdherentAbonnement;
import com.rtsmitia.bibliotheque.models.Penalite;
import com.rtsmitia.bibliotheque.services.AdherentService;
import com.rtsmitia.bibliotheque.services.LesContraintsService;
import com.rtsmitia.bibliotheque.services.PenaliteService;
import com.rtsmitia.bibliotheque.services.TypeAdherentService;

@Controller
@RequestMapping("/adherents")
public class AdherentController {

    private final AdherentService adherentService;
    private final TypeAdherentService typeAdherentService;
    private final LesContraintsService contraintsService;
    private final PenaliteService penaliteService;

    @Autowired
     public AdherentController(AdherentService adherentService,
                              TypeAdherentService typeAdherentService,
                              LesContraintsService contraintsService,
                              PenaliteService penaliteService) {
        this.adherentService = adherentService;
        this.typeAdherentService = typeAdherentService;
        this.contraintsService = contraintsService;
        this.penaliteService = penaliteService;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("adherent", new Adherent());
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypes());
        model.addAttribute("contraintes", contraintsService.getAllContraintes());
        model.addAttribute("contentPage", "adherent-form");

        return "layout";
    }

    @PostMapping("/save")
    public String saveAdherent(@ModelAttribute Adherent adherent, Model model) {
        try {
            adherentService.saveAdherent(adherent, adherent.getDateInscription());
            model.addAttribute("successMessage", "Adhérent enregistré avec succès !");
            model.addAttribute("adherent", new Adherent()); // empty form for next input
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("adherent", adherent); // preserve form values
        }
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypes());
        model.addAttribute("contraintes", contraintsService.getAllContraintes());
        model.addAttribute("contentPage", "adherent-form");

        return "layout";
    }

    @GetMapping("/inscription-a-valider")
    public String showInscriptionAValider(Model model) {
        List<Adherent> adherentsAValider = adherentService.getAdherentsWithStatusDemande();
        model.addAttribute("adherentsAValider", adherentsAValider);
        model.addAttribute("contentPage", "inscription-a-valider");
        return "layout";
    }

    @GetMapping("/status/demande")
    @ResponseBody
    public List<Adherent> getAdherentsWithStatusDemande() {
        return adherentService.getAdherentsWithStatusDemande();
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Adherent> getAllAdherents() {
        return adherentService.getAllAdherents();
    }
    
    @PostMapping("/approve-adherent/{id}")
    public String approveAdherant(@PathVariable Long id, 
                                 @RequestParam("dateChangement") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateChangement,
                                 Model model) {
        try {
            adherentService.approveAdherent(id, dateChangement);
        } catch (IllegalArgumentException e) {
            
        }
        return "redirect:/adherents/inscription-a-valider";
    }

    // ================= JSON API ENDPOINTS =================

    /**
     * Get all adherents as JSON with basic information
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<AdherentDetailsDto>> getAllAdherentsJson() {
        List<Adherent> adherents = adherentService.getAllAdherents();
        List<AdherentDetailsDto> adherentsDto = adherents.stream()
                .map(this::convertToDto)
                .toList();
        return ResponseEntity.ok(adherentsDto);
    }

    /**
     * Get detailed information about a specific adherent
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<AdherentDetailsDto> getAdherentDetailsJson(@PathVariable Long id) {
        Optional<Adherent> adherentOpt = adherentService.getAdherentWithContraintes(id);
        if (!adherentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        AdherentDetailsDto adherentDto = convertToDto(adherentOpt.get());
        return ResponseEntity.ok(adherentDto);
    }

    /**
     * Get adherent by numero adherent
     */
    @GetMapping("/api/numero/{numeroAdherent}")
    @ResponseBody
    public ResponseEntity<AdherentDetailsDto> getAdherentByNumeroJson(@PathVariable String numeroAdherent) {
        Optional<Adherent> adherentOpt = adherentService.validateClientNumber(numeroAdherent);
        if (!adherentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        AdherentDetailsDto adherentDto = convertToDto(adherentOpt.get());
        return ResponseEntity.ok(adherentDto);
    }

    /**
     * Convert Adherent entity to AdherentDetailsDto
     */
    private AdherentDetailsDto convertToDto(Adherent adherent) {
        // Check if adherent has valid subscription
        boolean isAbonne = adherentService.hasValidSubscription(adherent);
        
        // Get subscription end date
        LocalDate dateFinAbonnement = null;
        if (adherent.getAbonnements() != null && !adherent.getAbonnements().isEmpty()) {
            dateFinAbonnement = adherent.getAbonnements().stream()
                    .filter(ab -> ab.getFinAbonnement().isAfter(LocalDate.now().minusDays(1)))
                    .map(AdherentAbonnement::getFinAbonnement)
                    .max(LocalDate::compareTo)
                    .orElse(null);
        }
        
        // Get active penalties
        List<Penalite> penalitesActives = penaliteService.getActivePenalties(adherent);
        
        // Check if can borrow
        boolean peutEmprunter = adherentService.canBorrowBooks(adherent);
        
        // Get subscription status
        String statutAbonnement = adherentService.getBorrowingStatus(adherent);
        
        return new AdherentDetailsDto(adherent, isAbonne, dateFinAbonnement, 
                                    penalitesActives, peutEmprunter, statutAbonnement);
    }
}
