package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
import com.rtsmitia.bibliotheque.services.ReservationService;
import com.rtsmitia.bibliotheque.services.StatutReservationService;
import com.rtsmitia.bibliotheque.services.AdherentService;
import com.rtsmitia.bibliotheque.services.ExemplaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final StatutReservationService statutReservationService;
    private final AdherentService adherentService;
    private final ExemplaireService exemplaireService;

    @Autowired
    public ReservationController(ReservationService reservationService,
                               StatutReservationService statutReservationService,
                               AdherentService adherentService,
                               ExemplaireService exemplaireService) {
        this.reservationService = reservationService;
        this.statutReservationService = statutReservationService;
        this.adherentService = adherentService;
        this.exemplaireService = exemplaireService;
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
                        setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e1) {
                        try {
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
     * Display all reservations
     */
    @GetMapping
    public String listReservations(Model model) {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            model.addAttribute("reservations", reservations);
            model.addAttribute("contentPage", "reservation-list");
            model.addAttribute("pageTitle", "Gestion des Réservations");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des réservations: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Display active reservations only
     */
    @GetMapping("/actives")
    public String listActiveReservations(Model model) {
        try {
            List<Reservation> activeReservations = reservationService.getActiveReservations();
            model.addAttribute("reservations", activeReservations);
            model.addAttribute("contentPage", "reservation-list");
            model.addAttribute("pageTitle", "Réservations Actives");
            model.addAttribute("isActiveView", true);
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des réservations actives: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show reservation creation form
     */
    @GetMapping("/nouvelle")
    public String showReservationForm(Model model) {
        try {
            List<Adherent> adherents = adherentService.getAllAdherents();
            List<Exemplaire> exemplaires = exemplaireService.getAllExemplaires();
            
            model.addAttribute("reservation", new Reservation());
            model.addAttribute("adherents", adherents);
            model.addAttribute("exemplaires", exemplaires);
            model.addAttribute("contentPage", "reservation-form");
            model.addAttribute("pageTitle", "Nouvelle Réservation");
            model.addAttribute("isEdit", false);
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Create a new reservation
     */
    @PostMapping
    public String createReservation(@RequestParam("numeroAdherent") String numeroAdherent,
                                  @RequestParam("exemplaireId") Long exemplaireId,
                                  @RequestParam("dateDebutPret") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateDebutPret,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            // Validate input
            if (numeroAdherent == null || numeroAdherent.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le numéro d'adhérent est requis");
                return "redirect:/reservations/nouvelle";
            }

            if (exemplaireId == null) {
                redirectAttributes.addFlashAttribute("error", "L'exemplaire est requis");
                return "redirect:/reservations/nouvelle";
            }

            if (dateDebutPret == null) {
                redirectAttributes.addFlashAttribute("error", "La date de début de prêt est requise");
                return "redirect:/reservations/nouvelle";
            }

            // Validate that the date is in the future
            if (dateDebutPret.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "La date de début de prêt doit être dans le futur");
                return "redirect:/reservations/nouvelle";
            }

            Reservation reservation = reservationService.createReservation(numeroAdherent, exemplaireId, dateDebutPret);
            
            redirectAttributes.addFlashAttribute("success", 
                "Réservation créée avec succès! ID: " + reservation.getId());
            return "redirect:/reservations/" + reservation.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/reservations/nouvelle";
        }
    }

    /**
     * Show reservation details
     */
    @GetMapping("/{id}")
    public String showReservationDetails(@PathVariable Long id, Model model) {
        try {
            Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
            if (!reservationOpt.isPresent()) {
                model.addAttribute("error", "Réservation non trouvée avec l'ID: " + id);
                return "error";
            }

            Reservation reservation = reservationOpt.get();
            List<StatutReservation> statusHistory = statutReservationService.getStatusHistoryByReservation(reservation);
            Optional<StatutReservation> currentStatus = statutReservationService.getLatestStatusByReservation(reservation);

            model.addAttribute("reservation", reservation);
            model.addAttribute("statusHistory", statusHistory);
            model.addAttribute("currentStatus", currentStatus.orElse(null));
            model.addAttribute("contentPage", "reservation-details");
            model.addAttribute("pageTitle", "Détails de la Réservation #" + id);
            
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des détails: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show reservation edit form
     */
    @GetMapping("/{id}/modifier")
    public String showEditReservationForm(@PathVariable Long id, Model model) {
        try {
            Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
            if (!reservationOpt.isPresent()) {
                model.addAttribute("error", "Réservation non trouvée avec l'ID: " + id);
                return "error";
            }

            List<Adherent> adherents = adherentService.getAllAdherents();
            List<Exemplaire> exemplaires = exemplaireService.getAllExemplaires();

            model.addAttribute("reservation", reservationOpt.get());
            model.addAttribute("adherents", adherents);
            model.addAttribute("exemplaires", exemplaires);
            model.addAttribute("contentPage", "reservation-form");
            model.addAttribute("pageTitle", "Modifier la Réservation #" + id);
            model.addAttribute("isEdit", true);
            
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire d'édition: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Update a reservation
     */
    @PostMapping("/{id}")
    public String updateReservation(@PathVariable Long id,
                                  @RequestParam("dateDebutPret") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateDebutPret,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (dateDebutPret == null) {
                redirectAttributes.addFlashAttribute("error", "La date de début de prêt est requise");
                return "redirect:/reservations/" + id + "/modifier";
            }

            if (dateDebutPret.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "La date de début de prêt doit être dans le futur");
                return "redirect:/reservations/" + id + "/modifier";
            }

            reservationService.updateReservation(id, dateDebutPret);
            redirectAttributes.addFlashAttribute("success", "Réservation mise à jour avec succès!");
            return "redirect:/reservations/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/reservations/" + id + "/modifier";
        }
    }

    /**
     * Confirm a reservation
     */
    @PostMapping("/{id}/confirmer")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation confirmée avec succès!");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la confirmation: " + e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    /**
     * Cancel a reservation
     */
    @PostMapping("/{id}/annuler")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès!");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation: " + e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    /**
     * Expire a reservation
     */
    @PostMapping("/{id}/expirer")
    public String expireReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.expireReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation expirée!");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'expiration: " + e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    /**
     * Delete a reservation
     */
    @PostMapping("/{id}/supprimer")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès!");
            return "redirect:/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    /**
     * Get reservations by adherent (AJAX endpoint)
     */
    @GetMapping("/adherent/{numeroAdherent}")
    @ResponseBody
    public List<Reservation> getReservationsByAdherent(@PathVariable String numeroAdherent) {
        try {
            return reservationService.getReservationsByAdherentNumero(numeroAdherent);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des réservations: " + e.getMessage());
        }
    }

    /**
     * Get reservations by exemplaire (AJAX endpoint)
     */
    @GetMapping("/exemplaire/{exemplaireId}")
    @ResponseBody
    public List<Reservation> getReservationsByExemplaire(@PathVariable Long exemplaireId) {
        try {
            Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(exemplaireId);
            if (!exemplaireOpt.isPresent()) {
                throw new RuntimeException("Exemplaire non trouvé");
            }
            return reservationService.getReservationsByExemplaire(exemplaireOpt.get());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des réservations: " + e.getMessage());
        }
    }

    /**
     * Search reservations by date range
     */
    @GetMapping("/recherche")
    public String searchReservations(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
                                   @RequestParam(required = false) String statut,
                                   Model model) {
        try {
            List<Reservation> reservations;

            if (startDate != null && endDate != null) {
                reservations = reservationService.getReservationsByDateRange(startDate, endDate);
            } else {
                reservations = reservationService.getAllReservations();
            }

            model.addAttribute("reservations", reservations);
            model.addAttribute("searchStartDate", startDate);
            model.addAttribute("searchEndDate", endDate);
            model.addAttribute("searchStatut", statut);
            model.addAttribute("contentPage", "reservation-list");
            model.addAttribute("pageTitle", "Recherche de Réservations");
            
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Get reservations expiring soon
     */
    @GetMapping("/expirent-bientot")
    public String getExpiringSoonReservations(@RequestParam(defaultValue = "7") int days, Model model) {
        try {
            List<Reservation> expiringReservations = reservationService.findReservationsExpiringSoon(days);
            model.addAttribute("reservations", expiringReservations);
            model.addAttribute("contentPage", "reservation-list");
            model.addAttribute("pageTitle", "Réservations Expirant dans " + days + " jours");
            model.addAttribute("isExpiringView", true);
            model.addAttribute("daysAhead", days);
            
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des réservations expirant bientôt: " + e.getMessage());
            return "error";
        }
    }

    /**
     * User-facing reservation page (public)
     * Note: numero_adherent is not needed as input - it comes from the session
     */
    @GetMapping("/faire-reservation")
    public String showUserReservationForm(Model model) {
        try {
            List<Exemplaire> availableExemplaires = exemplaireService.getAllExemplairesWithPrets();
            
            model.addAttribute("reservation", new Reservation());
            model.addAttribute("exemplaires", availableExemplaires);
            model.addAttribute("contentPage", "user-reservation-form");
            model.addAttribute("pageTitle", "Faire une Réservation");
            model.addAttribute("isUserForm", true);
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Create a reservation from user interface
     */
    @PostMapping("/faire-reservation")
    public String createUserReservation(@RequestParam("exemplaireId") Long exemplaireId,
                                      @RequestParam("dateDebutPret") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateDebutPret,
                                      @RequestParam(value = "dateReservation", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateReservation,
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
                redirectAttributes.addFlashAttribute("error", "Adhérent introuvable.");
                return "redirect:/client/livres";
            }
            
            Adherent adherent = adherentOpt.get();

            // Validate input
            if (exemplaireId == null) {
                redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un livre");
                return "redirect:/reservations/faire-reservation";
            }

            if (dateDebutPret == null) {
                redirectAttributes.addFlashAttribute("error", "La date de début de prêt est requise");
                return "redirect:/reservations/faire-reservation";
            }

            // Get exemplaire
            Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(exemplaireId);
            if (exemplaireOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Exemplaire introuvable.");
                return "redirect:/reservations/faire-reservation";
            }

            // Create reservation without business rule validation
            Reservation reservation = reservationService.createReservation(adherent.getNumeroAdherent(), exemplaireId, dateDebutPret, dateReservation);
            
            redirectAttributes.addFlashAttribute("success", 
                "Votre réservation a été créée avec succès! Elle est en attente d'approbation par l'administrateur. Numéro de réservation: " + reservation.getId());
            return "redirect:/reservations/mes-reservations";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de la réservation: " + e.getMessage());
            return "redirect:/reservations/faire-reservation";
        }
    }

    /**
     * User's personal reservations page
     */
    @GetMapping("/mes-reservations")
    public String showUserReservations(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check client access
        if (!LoginController.checkClientAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        try {
            Adherent sessionAdherent = (Adherent) session.getAttribute("currentAdherent");
            List<Reservation> userReservations = reservationService.getReservationsByAdherentNumero(sessionAdherent.getNumeroAdherent());
            
            model.addAttribute("reservations", userReservations);
            model.addAttribute("numeroAdherent", sessionAdherent.getNumeroAdherent());
            model.addAttribute("contentPage", "user-reservations");
            model.addAttribute("pageTitle", "Mes Réservations");
            model.addAttribute("isUserView", true);
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement de vos réservations: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Admin panel for managing reservations
     */
    @GetMapping("/admin/panel")
    public String showAdminPanel(Model model) {
        try {
            List<Reservation> pendingReservations = reservationService.getPendingReservationsForApproval();
            List<Reservation> confirmedReservations = reservationService.getReservationsByStatus("confirmee");
            
            model.addAttribute("pendingReservations", pendingReservations);
            model.addAttribute("confirmedReservations", confirmedReservations);
            model.addAttribute("contentPage", "admin-reservations-panel");
            model.addAttribute("pageTitle", "Gestion des Réservations - Admin");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du panneau admin: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Admin approves a reservation
     */
    @PostMapping("/admin/{id}/approuver")
    public String approveReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.approveReservation(id);
            redirectAttributes.addFlashAttribute("success", "Réservation approuvée avec succès!");
            return "redirect:/reservations/admin/panel";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'approbation: " + e.getMessage());
            return "redirect:/reservations/admin/panel";
        }
    }

    /**
     * Admin rejects a reservation
     */
    @PostMapping("/admin/{id}/rejeter")
    public String rejectReservation(@PathVariable Long id, 
                                  @RequestParam(value = "reason", defaultValue = "Rejeté par l'administrateur") String reason,
                                  RedirectAttributes redirectAttributes) {
        try {
            reservationService.rejectReservation(id, reason);
            redirectAttributes.addFlashAttribute("success", "Réservation rejetée!");
            return "redirect:/reservations/admin/panel";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du rejet: " + e.getMessage());
            return "redirect:/reservations/admin/panel";
        }
    }

    /**
     * Convert confirmed reservation to actual loan
     */
    @PostMapping("/admin/{id}/convertir-pret")
    public String convertToPret(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.convertReservationToPret(id);
            redirectAttributes.addFlashAttribute("success", "Réservation convertie en prêt avec succès!");
            return "redirect:/reservations/admin/panel";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la conversion: " + e.getMessage());
            return "redirect:/reservations/admin/panel";
        }
    }

    /**
     * Get reservations by status for admin (AJAX)
     */
    @GetMapping("/admin/by-status/{status}")
    @ResponseBody
    public List<Reservation> getReservationsByStatusAdmin(@PathVariable String status) {
        try {
            return reservationService.getReservationsByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des réservations: " + e.getMessage());
        }
    }

    // ...existing code...
}
