package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.User;
import com.rtsmitia.bibliotheque.models.Livre;
import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Pret;
import com.rtsmitia.bibliotheque.services.LivreService;
import com.rtsmitia.bibliotheque.services.TypePretService;
import com.rtsmitia.bibliotheque.services.ExemplaireService;
import com.rtsmitia.bibliotheque.services.PretService;
import com.rtsmitia.bibliotheque.services.ProlongementService;
import com.rtsmitia.bibliotheque.services.QuotaPretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final LivreService livreService;
    private final TypePretService typePretService;
    private final ExemplaireService exemplaireService;
    private final PretService pretService;
    private final ProlongementService prolongementService;
    private final QuotaPretService quotaPretService;

    @Autowired
    public ClientController(LivreService livreService, TypePretService typePretService, ExemplaireService exemplaireService, PretService pretService, ProlongementService prolongementService, QuotaPretService quotaPretService) {
        this.livreService = livreService;
        this.typePretService = typePretService;
        this.exemplaireService = exemplaireService;
        this.pretService = pretService;
        this.prolongementService = prolongementService;
        this.quotaPretService = quotaPretService;
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
        
        // Get all books with their available exemplaires
        List<Livre> livres = livreService.getAllLivres();
        livres.forEach(livre -> {
            // For each book, set only available exemplaires
            List<Exemplaire> availableExemplaires = exemplaireService.getAvailableExemplairesByLivreId(livre.getId());
            livre.setExemplaires(availableExemplaires);
        });
        
        model.addAttribute("livres", livres);
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentDate", new java.util.Date());
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
        
        // Get search results with their available exemplaires
        List<Livre> livres = livreService.searchByTitleOrAuthor(searchTerm);
        livres.forEach(livre -> {
            // For each book, set only available exemplaires
            List<Exemplaire> availableExemplaires = exemplaireService.getAvailableExemplairesByLivreId(livre.getId());
            livre.setExemplaires(availableExemplaires);
        });
        
        model.addAttribute("livres", livres);
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentDate", new java.util.Date());
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

    /**
     * Show client's borrowed books for return
     */
    @GetMapping("/mes-emprunts")
    public String showMyLoans(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        Adherent sessionAdherent = (Adherent) session.getAttribute("currentAdherent");
        
        // Get adherent's active loans (validated and not returned)
        List<Pret> activeLoans = pretService.getActiveLoansForAdherent(sessionAdherent);
        
        // Check for pending extension requests for each loan
        Map<Long, Boolean> pendingExtensions = new HashMap<>();
        for (Pret pret : activeLoans) {
            boolean hasPending = prolongementService.hasPendingExtensionRequest(pret);
            pendingExtensions.put(pret.getId(), hasPending);
        }
        
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("pendingExtensions", pendingExtensions);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentDate", new java.util.Date());
        model.addAttribute("contentPage", "client-emprunts");
        model.addAttribute("pageTitle", "Mes Emprunts");
        return "layout";
    }

    /**
     * Handle book return with custom date (for simulation)
     */
    @PostMapping("/retour")
    public String returnBook(@RequestParam Long pretId,
                           @RequestParam String dateRetour,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        try {
            Adherent sessionAdherent = (Adherent) session.getAttribute("currentAdherent");
            
            // Parse the return date
            java.time.LocalDate returnDate = java.time.LocalDate.parse(dateRetour);
            java.time.LocalDateTime returnDateTime = returnDate.atStartOfDay();
            
            // Process the return with penalty check
            PretService.BookReturnResult result = pretService.returnBookWithDate(pretId, returnDateTime, sessionAdherent);
            
            if (result.isSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result.getMessage());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du retour: " + e.getMessage());
        }
        
        return "redirect:/client/mes-emprunts";
    }

    /**
     * Request loan extension for a specific loan
     */
    @PostMapping("/prolongement")
    public String requestExtension(@RequestParam Long pretId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        try {
            Adherent sessionAdherent = (Adherent) session.getAttribute("currentAdherent");
            
            // Get the loan
            Optional<Pret> pretOpt = pretService.findById(pretId);
            if (pretOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Prêt introuvable.");
                return "redirect:/client/mes-emprunts";
            }
            
            Pret pret = pretOpt.get();
            
            // Check if the loan belongs to the adherent
            if (!pret.getAdherent().getId().equals(sessionAdherent.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ce prêt ne vous appartient pas.");
                return "redirect:/client/mes-emprunts";
            }
            
            // Check if loan is active and not returned
            if (pret.getDateRetour() != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ce livre a déjà été retourné.");
                return "redirect:/client/mes-emprunts";
            }
            
            // Check if there's already a pending extension request
            boolean hasPendingExtension = prolongementService.hasPendingExtensionRequest(pret);
            if (hasPendingExtension) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Une demande de prolongement est déjà en cours pour ce prêt.");
                return "redirect:/client/mes-emprunts";
            }
            
            // Create extension request with automatic duration based on adherent type
            java.time.LocalDateTime requestDate = java.time.LocalDateTime.now();
            
            prolongementService.createProlongement(pret, requestDate);
            
            // Get the extension duration for display
            Integer extensionDays = quotaPretService.getLoanDurationForType(sessionAdherent.getTypeAdherent());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Demande de prolongement de %d jour(s) envoyée avec succès! Elle sera examinée par un administrateur.", extensionDays));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la demande de prolongement: " + e.getMessage());
        }
        
        return "redirect:/client/mes-emprunts";
    }
}
