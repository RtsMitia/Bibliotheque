package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.services.ExemplaireService;
import com.rtsmitia.bibliotheque.services.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/exemplaires")
public class ExemplaireController {

    private final ExemplaireService exemplaireService;
    private final LivreService livreService;

    @Autowired
    public ExemplaireController(ExemplaireService exemplaireService, LivreService livreService) {
        this.exemplaireService = exemplaireService;
        this.livreService = livreService;
    }

    /**
     * Show form to add a new exemplaire
     */
    @GetMapping("/add")
    public String showAddExemplaireForm(@RequestParam(required = false) Long livreId, Model model) {
        model.addAttribute("exemplaire", new Exemplaire());
        
        if (livreId != null) {
            Optional<Livre> livre = livreService.getLivreById(livreId);
            if (livre.isPresent()) {
                model.addAttribute("selectedLivre", livre.get());
                model.addAttribute("livreId", livreId);
            }
        }
        
        model.addAttribute("livres", livreService.getAllLivres());
        model.addAttribute("contentPage", "exemplaire-form");
        model.addAttribute("pageTitle", "Ajouter un Exemplaire");
        return "layout";
    }

    /**
     * Process the add exemplaire form
     */
    @PostMapping("/add")
    public String addExemplaire(@RequestParam Long livreId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateArrivee,
                               RedirectAttributes redirectAttributes) {
        try {
            Exemplaire savedExemplaire = exemplaireService.createExemplaire(livreId, dateArrivee);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Exemplaire ajouté avec succès pour le livre '" + savedExemplaire.getLivre().getTitre() + "'!");
            
            // Redirect back to the book details page
            return "redirect:/livres/" + livreId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'ajout de l'exemplaire: " + e.getMessage());
            return "redirect:/exemplaires/add?livreId=" + livreId;
        }
    }

    /**
     * Show list of all exemplaires
     */
    @GetMapping("/list")
    public String listExemplaires(Model model) {
        model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
        model.addAttribute("contentPage", "exemplaire-list");
        model.addAttribute("pageTitle", "Liste des Exemplaires");
        return "layout";
    }

    /**
     * Show details of a specific exemplaire
     */
    @GetMapping("/{id}")
    public String showExemplaireDetails(@PathVariable Long id, Model model) {
        return exemplaireService.getExemplaireById(id)
                .map(exemplaire -> {
                    model.addAttribute("exemplaire", exemplaire);
                    model.addAttribute("isCurrentlyBorrowed", exemplaireService.isExemplaireBorrowed(id));
                    model.addAttribute("contentPage", "exemplaire-details");
                    model.addAttribute("pageTitle", "Détails de l'Exemplaire");
                    return "layout";
                })
                .orElse("redirect:/exemplaires/list");
    }

    /**
     * Show edit form for an exemplaire
     */
    @GetMapping("/edit/{id}")
    public String showEditExemplaireForm(@PathVariable Long id, Model model) {
        return exemplaireService.getExemplaireById(id)
                .map(exemplaire -> {
                    model.addAttribute("exemplaire", exemplaire);
                    model.addAttribute("livres", livreService.getAllLivres());
                    model.addAttribute("contentPage", "exemplaire-form");
                    model.addAttribute("pageTitle", "Modifier l'Exemplaire");
                    return "layout";
                })
                .orElse("redirect:/exemplaires/list");
    }

    /**
     * Process the edit exemplaire form
     */
    @PostMapping("/edit/{id}")
    public String updateExemplaire(@PathVariable Long id,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateArrivee,
                                  RedirectAttributes redirectAttributes) {
        try {
            exemplaireService.updateExemplaire(id, dateArrivee);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Exemplaire mis à jour avec succès!");
            return "redirect:/exemplaires/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la mise à jour de l'exemplaire: " + e.getMessage());
            return "redirect:/exemplaires/edit/" + id;
        }
    }

    /**
     * Delete an exemplaire
     */
    @PostMapping("/delete/{id}")
    public String deleteExemplaire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Exemplaire> exemplaire = exemplaireService.getExemplaireById(id);
            if (exemplaire.isPresent()) {
                Long livreId = exemplaire.get().getLivre().getId();
                exemplaireService.deleteExemplaire(id);
                
                redirectAttributes.addFlashAttribute("successMessage", "Exemplaire supprimé avec succès!");
                return "redirect:/livres/" + livreId;
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Exemplaire non trouvé!");
                return "redirect:/exemplaires/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la suppression de l'exemplaire: " + e.getMessage());
            return "redirect:/exemplaires/list";
        }
    }

    /**
     * Search exemplaires
     */
    @GetMapping("/search")
    public String searchExemplaires(@RequestParam String searchTerm, Model model) {
        List<Exemplaire> exemplaires = exemplaireService.searchExemplairesByBookTitle(searchTerm);
        model.addAttribute("exemplaires", exemplaires);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("contentPage", "exemplaire-list");
        model.addAttribute("pageTitle", "Résultats de Recherche - Exemplaires");
        return "layout";
    }

    /**
     * Get exemplaires for a specific book (AJAX endpoint)
     */
    @GetMapping("/livre/{livreId}")
    @ResponseBody
    public List<Exemplaire> getExemplairesByLivre(@PathVariable Long livreId) {
        return exemplaireService.getExemplairesByLivreId(livreId);
    }

    /**
     * Get available exemplaires for a specific book (AJAX endpoint)
     */
    @GetMapping("/livre/{livreId}/available")
    @ResponseBody
    public List<Exemplaire> getAvailableExemplairesByLivre(@PathVariable Long livreId) {
        return exemplaireService.getAvailableExemplairesByLivreId(livreId);
    }

    /**
     * Get exemplaire statistics for a book (AJAX endpoint)
     */
    @GetMapping("/livre/{livreId}/stats")
    @ResponseBody
    public ExemplaireService.ExemplaireStats getExemplaireStats(@PathVariable Long livreId) {
        return exemplaireService.getExemplaireStats(livreId);
    }
}
