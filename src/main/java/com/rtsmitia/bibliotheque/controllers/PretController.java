package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.*;
import com.rtsmitia.bibliotheque.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prets")
public class PretController {
    
    @Autowired
    private PretService pretService;
    
    @Autowired
    private ExemplaireService exemplaireService;
    
    @Autowired
    private TypePretService typePretService;
    
    @Autowired
    private AdherentService adherentService;
    
    @Autowired
    private ProlongementService prolongementService;
    
    /**
     * Client requests to borrow a book
     */
    @PostMapping("/request")
    public String requestLoan(@RequestParam Long exemplaireId,
                             @RequestParam Long typePretId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            Adherent sessionAdherent = (Adherent) session.getAttribute("currentAdherent");
            
            // Reload adherent with contraintes from database
            Optional<Adherent> adherentOpt = adherentService.getAdherentWithContraintes(sessionAdherent.getId());
            if (adherentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Adhérent introuvable.");
                return "redirect:/client/livres";
            }
            
            Adherent adherent = adherentOpt.get();
            
            Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(exemplaireId);
            Optional<TypePret> typePretOpt = typePretService.findById(typePretId);
            
            if (exemplaireOpt.isEmpty() || typePretOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Exemplaire ou type de prêt introuvable.");
                return "redirect:/client/livres";
            }
            
            // Check basic eligibility before creating request
            PretService.LoanEligibilityResult eligibility = pretService.checkLoanEligibility(adherent, exemplaireOpt.get());
            if (!eligibility.isEligible()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Impossible de demander ce prêt: " + eligibility.getReason());
                return "redirect:/client/livres";
            }
            
            // Create loan request
            pretService.requestLoan(adherent, exemplaireOpt.get(), typePretOpt.get());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Demande de prêt envoyée avec succès! Elle sera examinée par un administrateur.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la demande de prêt: " + e.getMessage());
        }
        
        return "redirect:/client/livres";
    }
    
    /**
     * Admin page to view pending loan requests
     */
    @GetMapping("/demandes")
    public String viewLoanRequests(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            List<Pret> pendingRequests = pretService.getPendingLoanRequests();
            List<Pret> onHoldRequests = pretService.getLoansOnHold();
            
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("onHoldRequests", onHoldRequests);
            model.addAttribute("contentPage", "demandes-pret");
            model.addAttribute("pageTitle", "Demandes de Prêt");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du chargement des demandes: " + e.getMessage());
        }
        
        return "layout";
    }
    
    /**
     * Admin approves a loan request
     */
    @PostMapping("/approve/{pretId}")
    public String approveLoan(@PathVariable Long pretId, 
                             HttpSession session, 
                             RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            PretService.LoanApprovalResult result = pretService.approveLoan(pretId);
            
            if (result.isSuccessful()) {
                redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result.getMessage());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'approbation: " + e.getMessage());
        }
        
        return "redirect:/prets/demandes";
    }
    
    /**
     * Admin rejects a loan request
     */
    @PostMapping("/reject/{pretId}")
    public String rejectLoan(@PathVariable Long pretId, 
                            HttpSession session, 
                            RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            boolean success = pretService.rejectLoan(pretId, "Rejeté par l'administrateur");
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Demande de prêt rejetée.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du rejet de la demande.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du rejet: " + e.getMessage());
        }
        
        return "redirect:/prets/demandes";
    }
    
    /**
     * Admin puts loan request on hold
     */
    @PostMapping("/hold/{pretId}")
    public String putLoanOnHold(@PathVariable Long pretId, 
                               HttpSession session, 
                               RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            boolean success = pretService.putLoanOnHold(pretId);
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Demande mise en attente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise en attente.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de la mise en attente: " + e.getMessage());
        }
        
        return "redirect:/prets/demandes";
    }
    
    /**
     * View active loans (admin)
     */
    @GetMapping("/actifs")
    public String viewActiveLoans(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            List<Pret> activeLoans = pretService.getApprovedLoans();
            
            model.addAttribute("activeLoans", activeLoans);
            model.addAttribute("contentPage", "prets-actifs");
            model.addAttribute("pageTitle", "Prêts Actifs");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du chargement des prêts actifs: " + e.getMessage());
        }
        
        return "layout";
    }
    
    /**
     * Return a book (admin)
     */
    @PostMapping("/return/{pretId}")
    public String returnBook(@PathVariable Long pretId, 
                            HttpSession session, 
                            RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            boolean success = pretService.returnBook(pretId);
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Livre retourné avec succès.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du retour du livre.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du retour: " + e.getMessage());
        }
        
        return "redirect:/prets/actifs";
    }
    
    /**
     * Client views their loan history
     */
    @GetMapping("/mes-prets")
    public String viewMyLoans(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            Adherent adherent = (Adherent) session.getAttribute("currentAdherent");
            List<Pret> myLoans = pretService.getLoansForAdherent(adherent);
            List<Pret> activeLoans = pretService.getActiveLoansForAdherent(adherent);
            
            model.addAttribute("myLoans", myLoans);
            model.addAttribute("activeLoans", activeLoans);
            model.addAttribute("contentPage", "client-prets");
            model.addAttribute("pageTitle", "Mes Prêts");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du chargement de vos prêts: " + e.getMessage());
        }
        
        return "layout";
    }

    /**
     * View pending prolongement requests (admin)
     */
    @GetMapping("/prolongements")
    public String viewProlongementRequests(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            List<Prolongement> pendingRequests = prolongementService.getPendingProlongementRequests();
            
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("contentPage", "prolongement-requests");
            model.addAttribute("pageTitle", "Demandes de Prolongement");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du chargement des demandes: " + e.getMessage());
        }
        
        return "layout";
    }

    /**
     * Approve a prolongement request (admin)
     */
    @PostMapping("/prolongements/{id}/approve")
    public String approveProlongement(@PathVariable Long id, 
                                    HttpSession session, 
                                    RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            prolongementService.approveProlongement(id, java.time.LocalDateTime.now());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Demande de prolongement approuvée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'approbation: " + e.getMessage());
        }
        
        return "redirect:/prets/prolongements";
    }

    /**
     * Reject a prolongement request (admin)
     */
    @PostMapping("/prolongements/{id}/reject")
    public String rejectProlongement(@PathVariable Long id, 
                                   HttpSession session, 
                                   RedirectAttributes redirectAttributes) {
        
        // Check admin access
        if (!LoginController.checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }
        
        try {
            prolongementService.rejectProlongement(id, java.time.LocalDateTime.now());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Demande de prolongement rejetée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du rejet: " + e.getMessage());
        }
        
        return "redirect:/prets/prolongements";
    }
}
