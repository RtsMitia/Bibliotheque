package com.rtsmitia.bibliotheque.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.services.AdherentService;
import com.rtsmitia.bibliotheque.services.TypeAdherentService;

@Controller
@RequestMapping("/adherents")
public class AdherentController {

    private final AdherentService adherentService;
    private final TypeAdherentService typeAdherentService;

    @Autowired
     public AdherentController(AdherentService adherentService,
                              TypeAdherentService typeAdherentService) {
        this.adherentService = adherentService;
        this.typeAdherentService = typeAdherentService;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("adherent", new Adherent());
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypes());
        model.addAttribute("pageType", "adherent-form");

        return "layout";
    }

    @PostMapping("/save")
    public String saveAdherent(@ModelAttribute Adherent adherent, Model model) {
        try {
            adherentService.saveAdherent(adherent);
            model.addAttribute("successMessage", "Adhérent enregistré avec succès !");
            model.addAttribute("adherent", new Adherent()); // empty form for next input
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("adherent", adherent); // preserve form values
        }
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypes());
        // Always set the content fragment
        model.addAttribute("pageType", "adherent-form");

        return "layout";
    }

}
