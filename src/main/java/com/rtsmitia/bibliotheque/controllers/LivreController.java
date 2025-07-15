package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.dto.LivreDetailsDto;
import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.services.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/livres")
public class LivreController {

    private final LivreService livreService;

    @Autowired
    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    /**
     * Custom date time formatter for form binding
     */
    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.trim().isEmpty()) {
                    try {
                        // Try datetime-local format first (YYYY-MM-DDTHH:MM)
                        setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e1) {
                        try {
                            // Try date only format (YYYY-MM-DD) and set time to midnight
                            setValue(LocalDateTime.parse(text + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception e2) {
                            throw new IllegalArgumentException("Invalid date format: " + text);
                        }
                    }
                } else {
                    setValue(null);
                }
            }
        });
    }

    /**
     * Show form to add a new livre
     */
    @GetMapping("/add")
    public String showAddLivreForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        model.addAttribute("livre", new Livre());
        model.addAttribute("auteurs", livreService.getAllAuteurs());
        model.addAttribute("genres", livreService.getAllGenres());
        model.addAttribute("contraintes", livreService.getAllContraints());
        model.addAttribute("contentPage", "livre-form");
        model.addAttribute("pageTitle", "Ajouter un Livre");
        return "layout";
    }

    /**
     * Process the add livre form
     */
    @PostMapping("/add")
    public String addLivre(@ModelAttribute Livre livre,
                          @RequestParam Long auteurId,
                          @RequestParam(required = false) List<Long> genreIds,
                          @RequestParam(required = false) List<Long> contraintIds,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateSortie,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            // Set the date if not already set
            if (livre.getDateSortie() == null) {
                livre.setDateSortie(dateSortie);
            }

            Livre savedLivre = livreService.createLivre(
                livre.getTitre(),
                livre.getResume(),
                livre.getDateSortie(),
                auteurId,
                genreIds,
                contraintIds
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                "Livre '" + savedLivre.getTitre() + "' ajouté avec succès!");
            return "redirect:/livres/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'ajout du livre: " + e.getMessage());
            return "redirect:/livres/add";
        }
    }

    /**
     * Show list of all livres
     */
    @GetMapping("/list")
    public String listLivres(Model model) {
        model.addAttribute("livres", livreService.getAllLivres());
        model.addAttribute("contentPage", "livre-list");
        model.addAttribute("pageTitle", "Liste des Livres");
        return "layout";
    }

    /**
     * Show details of a specific livre
     */
    @GetMapping("/{id}")
    public String showLivreDetails(@PathVariable Long id, Model model) {
        return livreService.getLivreById(id)
                .map(livre -> {
                    model.addAttribute("livre", livre);
                    model.addAttribute("contentPage", "livre-details");
                    model.addAttribute("pageTitle", "Détails - " + livre.getTitre());
                    return "layout";
                })
                .orElse("redirect:/livres/list");
    }

    /**
     * Show edit form for a livre
     */
    @GetMapping("/edit/{id}")
    public String showEditLivreForm(@PathVariable Long id, Model model) {
        return livreService.getLivreById(id)
                .map(livre -> {
                    model.addAttribute("livre", livre);
                    model.addAttribute("auteurs", livreService.getAllAuteurs());
                    model.addAttribute("genres", livreService.getAllGenres());
                    model.addAttribute("contraintes", livreService.getAllContraints());
                    model.addAttribute("contentPage", "livre-form");
                    model.addAttribute("pageTitle", "Modifier le Livre");
                    return "layout";
                })
                .orElse("redirect:/livres/list");
    }

    /**
     * Process the edit livre form
     */
    @PostMapping("/edit/{id}")
    public String updateLivre(@PathVariable Long id,
                             @ModelAttribute Livre livre,
                             @RequestParam Long auteurId,
                             @RequestParam(required = false) List<Long> genreIds,
                             @RequestParam(required = false) List<Long> contraintIds,
                             RedirectAttributes redirectAttributes) {
        try {
            livre.setId(id);
            Livre updatedLivre = livreService.createLivre(
                livre.getTitre(),
                livre.getResume(),
                livre.getDateSortie(),
                auteurId,
                genreIds,
                contraintIds
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                "Livre '" + updatedLivre.getTitre() + "' mis à jour avec succès!");
            return "redirect:/livres/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la mise à jour du livre: " + e.getMessage());
            return "redirect:/livres/edit/" + id;
        }
    }

    /**
     * Delete a livre
     */
    @PostMapping("/delete/{id}")
    public String deleteLivre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            livreService.deleteLivre(id);
            redirectAttributes.addFlashAttribute("successMessage", "Livre supprimé avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la suppression du livre: " + e.getMessage());
        }
        return "redirect:/livres/list";
    }

    /**
     * Search livres
     */
    @GetMapping("/search")
    public String searchLivres(@RequestParam String searchTerm, Model model) {
        List<Livre> livres = livreService.searchByTitleOrAuthor(searchTerm);
        model.addAttribute("livres", livres);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("contentPage", "livre-list");
        model.addAttribute("pageTitle", "Résultats de Recherche");
        return "layout";
    }

    /**
     * Display the JSON API demo page
     */
    @GetMapping("/api-demo")
    public String showApiDemo(Model model) {
        model.addAttribute("contentPage", "livres-api-demo");
        model.addAttribute("pageTitle", "API Livres - Démonstration");
        return "layout";
    }

    // ================= JSON API ENDPOINTS =================

    /**
     * Get all books as JSON with basic information
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<LivreDetailsDto>> getAllLivresJson() {
        List<Livre> livres = livreService.getAllLivres();
        List<LivreDetailsDto> livresDto = livres.stream()
                .map(LivreDetailsDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(livresDto);
    }

    /**
     * Get detailed information about a specific book including its exemplaires and availability
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<LivreDetailsDto> getLivreDetailsJson(@PathVariable Long id) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (!livreOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LivreDetailsDto livreDto = new LivreDetailsDto(livreOpt.get());
        return ResponseEntity.ok(livreDto);
    }

    /**
     * Search books by title or author and return JSON
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<LivreDetailsDto>> searchLivresJson(@RequestParam String searchTerm) {
        List<Livre> livres = livreService.searchByTitleOrAuthor(searchTerm);
        List<LivreDetailsDto> livresDto = livres.stream()
                .map(LivreDetailsDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(livresDto);
    }
}
