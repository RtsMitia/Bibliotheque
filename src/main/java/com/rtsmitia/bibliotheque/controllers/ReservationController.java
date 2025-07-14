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
            model.addAttribute("pageTitle", "Gestion des Réservations");
            return "reservation-list";
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
            model.addAttribute("pageTitle", "Réservations Actives");
            model.addAttribute("isActiveView", true);
            return "reservation-list";
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
            model.addAttribute("pageTitle", "Nouvelle Réservation");
            model.addAttribute("isEdit", false);
            return "reservation-form";
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
            model.addAttribute("pageTitle", "Détails de la Réservation #" + id);
            
            return "reservation-details";
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
            model.addAttribute("pageTitle", "Modifier la Réservation #" + id);
            model.addAttribute("isEdit", true);
            
            return "reservation-form";
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
            model.addAttribute("pageTitle", "Recherche de Réservations");
            
            return "reservation-list";
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
            model.addAttribute("pageTitle", "Réservations Expirant dans " + days + " jours");
            model.addAttribute("isExpiringView", true);
            model.addAttribute("daysAhead", days);
            
            return "reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des réservations expirant bientôt: " + e.getMessage());
            return "error";
        }
    }
}
