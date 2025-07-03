package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.User;
import com.rtsmitia.bibliotheque.services.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final LivreService livreService;

    @Autowired
    public ClientController(LivreService livreService) {
        this.livreService = livreService;
    }

    /**
     * Show client book catalog
     */
    @GetMapping("/livres")
    public String showClientLivres(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        
        model.addAttribute("livres", livreService.getAllLivres());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("contentPage", "client-livres");
        model.addAttribute("pageTitle", "Catalogue des Livres");
        return "layout";
    }

    /**
     * Search books for client
     */
    @GetMapping("/livres/search")
    public String searchClientLivres(@RequestParam String searchTerm, 
                                   Model model, 
                                   HttpSession session, 
                                   RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        
        model.addAttribute("livres", livreService.searchByTitleOrAuthor(searchTerm));
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("contentPage", "client-livres");
        model.addAttribute("pageTitle", "Recherche - " + searchTerm);
        return "layout";
    }

    /**
     * Handle book borrowing (placeholder for now)
     */
    @PostMapping("/emprunter")
    public String borrowBook(@RequestParam Long livreId,
                           @RequestParam String numeroClient,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        
        // Verify the client number matches the session
        if (!currentUser.getNumeroClient().equals(numeroClient)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur d'authentification.");
            return "redirect:/client/livres";
        }

        try {
            // For now, just show a success message
            // In a real implementation, you would:
            // 1. Check if the book has available exemplaires
            // 2. Create a Pret (loan) record
            // 3. Update exemplaire status
            
            var livre = livreService.getLivreById(livreId);
            if (livre.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Demande d'emprunt pour '" + livre.get().getTitre() + "' enregistrée avec succès ! " +
                    "Veuillez vous présenter à l'accueil pour finaliser l'emprunt.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Livre non trouvé.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la demande d'emprunt: " + e.getMessage());
        }

        return "redirect:/client/livres";
    }

    /**
     * Show client dashboard/profile (future feature)
     */
    @GetMapping("/profile")
    public String showClientProfile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("contentPage", "client-profile");
        model.addAttribute("pageTitle", "Mon Profil");
        return "layout";
    }
}
