package com.rtsmitia.bibliotheque.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.services.AdherentService;
import com.rtsmitia.bibliotheque.services.LesContraintsService;
import com.rtsmitia.bibliotheque.services.TypeAdherentService;

@Controller
@RequestMapping("/adherents")
public class AdherentController {

    private final AdherentService adherentService;
    private final TypeAdherentService typeAdherentService;
    private final LesContraintsService contraintsService;

    @Autowired
     public AdherentController(AdherentService adherentService,
                              TypeAdherentService typeAdherentService,
                              LesContraintsService contraintsService) {
        this.adherentService = adherentService;
        this.typeAdherentService = typeAdherentService;
        this.contraintsService = contraintsService;
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
    
}
