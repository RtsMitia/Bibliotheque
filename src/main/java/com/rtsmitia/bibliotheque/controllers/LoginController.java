package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.User;
import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.services.AdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private AdherentService adherentService;

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        // If already logged in, redirect to appropriate page
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.isLoggedIn()) {
            if (currentUser.isAdmin()) {
                return "redirect:/livres/list";
            } else {
                return "redirect:/client/livres";
            }
        }

        model.addAttribute("contentPage", "login");
        model.addAttribute("pageTitle", "Connexion");
        return "layout";
    }

    /**
     * Handle client login
     */
    @RequestMapping(value = "/login/client", method = RequestMethod.POST)
    public String loginClient(@RequestParam String numeroClient, 
                             HttpSession session, 
                             RedirectAttributes redirectAttributes) {
        try {
            // Simple validation - just check if numero is not empty
            if (numeroClient == null || numeroClient.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Veuillez saisir votre numéro de client.");
                return "redirect:/login";
            }

            // Validate that the client number exists in the database
            Optional<Adherent> adherentOpt = adherentService.validateClientNumber(numeroClient.trim());
            if (adherentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Numéro de client invalide. Veuillez vérifier votre numéro d'adhérent.");
                return "redirect:/login";
            }

            Adherent adherent = adherentOpt.get();

            // Check if the adherent has a valid subscription and can borrow
            /*if (!adherentService.hasValidSubscription(adherent)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Votre abonnement n'est pas actif. Veuillez contacter l'administration.");
                return "redirect:/login";
            }*/

            // Create client user with additional info
            User user = new User(numeroClient.trim(), "CLIENT");
            user.setAdherentInfo(adherent.getPrenom() + " " + adherent.getNom());
            session.setAttribute("currentUser", user);
            session.setAttribute("currentAdherent", adherent);

            // Add borrowing status info
            String borrowingStatus = adherentService.getBorrowingStatus(adherent);
            String quotaInfo = adherentService.getQuotaInfo(adherent);
            
            String welcomeMessage = String.format("Connexion réussie ! Bienvenue, %s %s. Statut: %s. %s", 
                                                adherent.getPrenom(), adherent.getNom(), borrowingStatus, quotaInfo);
            
            redirectAttributes.addFlashAttribute("successMessage", welcomeMessage);
            return "redirect:/client/livres";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la connexion: " + e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * Handle admin login
     */
    @RequestMapping(value = "/login/admin", method = RequestMethod.POST)
    public String loginAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Create admin user
            User user = new User("admin", "ADMIN");
            session.setAttribute("currentUser", user);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Connexion administrateur réussie !");
            return "redirect:/livres/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la connexion administrateur: " + e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * Logout
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("currentUser");
        session.invalidate();
        
        redirectAttributes.addFlashAttribute("successMessage", "Déconnexion réussie !");
        return "redirect:/login";
    }

    /**
     * Default route - redirect to login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Check access for admin routes
     */
    public static boolean checkAdminAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !currentUser.isLoggedIn()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez vous connecter.");
            return false;
        }

        if (!currentUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès administrateur requis.");
            return false;
        }

        return true;
    }

    /**
     * Check access for client routes
     */
    public static boolean checkClientAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !currentUser.isLoggedIn()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez vous connecter.");
            return false;
        }

        return true;
    }
}
