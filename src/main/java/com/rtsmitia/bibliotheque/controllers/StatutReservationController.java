package com.rtsmitia.bibliotheque.controllers;

import com.rtsmitia.bibliotheque.models.Reservation;
import com.rtsmitia.bibliotheque.models.StatutReservation;
import com.rtsmitia.bibliotheque.services.StatutReservationService;
import com.rtsmitia.bibliotheque.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/statut-reservations")
public class StatutReservationController {

    private final StatutReservationService statutReservationService;
    private final ReservationService reservationService;

    @Autowired
    public StatutReservationController(StatutReservationService statutReservationService,
                                     ReservationService reservationService) {
        this.statutReservationService = statutReservationService;
        this.reservationService = reservationService;
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
     * Display all status changes
     */
    @GetMapping
    public String listStatutReservations(Model model) {
        try {
            List<StatutReservation> statutReservations = statutReservationService.getAllStatutReservations();
            model.addAttribute("statutReservations", statutReservations);
            model.addAttribute("pageTitle", "Historique des Statuts de Réservation");
            return "statut-reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des statuts: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show status change creation form
     */
    @GetMapping("/nouveau")
    public String showStatutReservationForm(@RequestParam(required = false) Long reservationId, Model model) {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            List<StatutReservation.StatutType> statutTypes = Arrays.asList(StatutReservation.StatutType.values());
            
            model.addAttribute("statutReservation", new StatutReservation());
            model.addAttribute("reservations", reservations);
            model.addAttribute("statutTypes", statutTypes);
            model.addAttribute("selectedReservationId", reservationId);
            model.addAttribute("pageTitle", "Nouveau Changement de Statut");
            model.addAttribute("isEdit", false);
            return "statut-reservation-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Create a new status change
     */
    @PostMapping
    public String createStatutReservation(@RequestParam("reservationId") Long reservationId,
                                        @RequestParam("statut") String statut,
                                        @RequestParam(value = "dateChangement", required = false) 
                                        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateChangement,
                                        RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (reservationId == null) {
                redirectAttributes.addFlashAttribute("error", "La réservation est requise");
                return "redirect:/statut-reservations/nouveau";
            }

            if (statut == null || statut.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le statut est requis");
                return "redirect:/statut-reservations/nouveau";
            }

            StatutReservation statutReservation;
            if (dateChangement != null) {
                statutReservation = statutReservationService.createStatutReservation(reservationId, statut, dateChangement);
            } else {
                statutReservation = statutReservationService.createStatutReservation(reservationId, statut);
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Changement de statut créé avec succès! ID: " + statutReservation.getId());
            return "redirect:/statut-reservations/" + statutReservation.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/statut-reservations/nouveau";
        }
    }

    /**
     * Show status change details
     */
    @GetMapping("/{id}")
    public String showStatutReservationDetails(@PathVariable Long id, Model model) {
        try {
            Optional<StatutReservation> statutReservationOpt = statutReservationService.getStatutReservationById(id);
            if (!statutReservationOpt.isPresent()) {
                model.addAttribute("error", "Changement de statut non trouvé avec l'ID: " + id);
                return "error";
            }

            StatutReservation statutReservation = statutReservationOpt.get();
            
            // Get all status changes for this reservation to show context
            List<StatutReservation> relatedStatuts = statutReservationService.getStatutsByReservation(statutReservation.getReservation());

            model.addAttribute("statutReservation", statutReservation);
            model.addAttribute("relatedStatuts", relatedStatuts);
            model.addAttribute("pageTitle", "Détails du Changement de Statut #" + id);
            
            return "statut-reservation-details";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des détails: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show status change edit form
     */
    @GetMapping("/{id}/modifier")
    public String showEditStatutReservationForm(@PathVariable Long id, Model model) {
        try {
            Optional<StatutReservation> statutReservationOpt = statutReservationService.getStatutReservationById(id);
            if (!statutReservationOpt.isPresent()) {
                model.addAttribute("error", "Changement de statut non trouvé avec l'ID: " + id);
                return "error";
            }

            List<Reservation> reservations = reservationService.getAllReservations();
            List<StatutReservation.StatutType> statutTypes = Arrays.asList(StatutReservation.StatutType.values());

            model.addAttribute("statutReservation", statutReservationOpt.get());
            model.addAttribute("reservations", reservations);
            model.addAttribute("statutTypes", statutTypes);
            model.addAttribute("pageTitle", "Modifier le Changement de Statut #" + id);
            model.addAttribute("isEdit", true);
            
            return "statut-reservation-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire d'édition: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Update a status change
     */
    @PostMapping("/{id}")
    public String updateStatutReservation(@PathVariable Long id,
                                        @RequestParam("statut") String statut,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (statut == null || statut.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le statut est requis");
                return "redirect:/statut-reservations/" + id + "/modifier";
            }

            statutReservationService.updateStatutReservation(id, statut);
            redirectAttributes.addFlashAttribute("success", "Changement de statut mis à jour avec succès!");
            return "redirect:/statut-reservations/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/statut-reservations/" + id + "/modifier";
        }
    }

    /**
     * Delete a status change
     */
    @PostMapping("/{id}/supprimer")
    public String deleteStatutReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            statutReservationService.deleteStatutReservation(id);
            redirectAttributes.addFlashAttribute("success", "Changement de statut supprimé avec succès!");
            return "redirect:/statut-reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/statut-reservations/" + id;
        }
    }

    /**
     * Get status changes by reservation (AJAX endpoint)
     */
    @GetMapping("/reservation/{reservationId}")
    @ResponseBody
    public List<StatutReservation> getStatutsByReservation(@PathVariable Long reservationId) {
        try {
            return statutReservationService.getStatutsByReservationId(reservationId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des statuts: " + e.getMessage());
        }
    }

    /**
     * Get current status for a reservation (AJAX endpoint)
     */
    @GetMapping("/reservation/{reservationId}/current")
    @ResponseBody
    public StatutReservation getCurrentStatusByReservation(@PathVariable Long reservationId) {
        try {
            Optional<StatutReservation> currentStatus = statutReservationService.getLatestStatusByReservationId(reservationId);
            return currentStatus.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du statut actuel: " + e.getMessage());
        }
    }

    /**
     * Get status changes by status type
     */
    @GetMapping("/statut/{statut}")
    public String getStatutsByStatus(@PathVariable String statut, Model model) {
        try {
            List<StatutReservation> statutReservations = statutReservationService.getStatutsByStatus(statut);
            model.addAttribute("statutReservations", statutReservations);
            model.addAttribute("pageTitle", "Changements de Statut: " + statut);
            model.addAttribute("filteredStatut", statut);
            
            return "statut-reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des statuts: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Search status changes by date range
     */
    @GetMapping("/recherche")
    public String searchStatutReservations(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
                                         @RequestParam(required = false) String statut,
                                         Model model) {
        try {
            List<StatutReservation> statutReservations;

            if (startDate != null && endDate != null && statut != null && !statut.trim().isEmpty()) {
                // Search by date range and status
                List<StatutReservation> byDateRange = statutReservationService.getStatutsByDateRange(startDate, endDate);
                statutReservations = byDateRange.stream()
                    .filter(sr -> sr.getStatut().equals(statut))
                    .toList();
            } else if (startDate != null && endDate != null) {
                statutReservations = statutReservationService.getStatutsByDateRange(startDate, endDate);
            } else if (statut != null && !statut.trim().isEmpty()) {
                statutReservations = statutReservationService.getStatutsByStatus(statut);
            } else {
                statutReservations = statutReservationService.getAllStatutReservations();
            }

            model.addAttribute("statutReservations", statutReservations);
            model.addAttribute("searchStartDate", startDate);
            model.addAttribute("searchEndDate", endDate);
            model.addAttribute("searchStatut", statut);
            model.addAttribute("pageTitle", "Recherche de Changements de Statut");
            
            return "statut-reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Get reservations with current status
     */
    @GetMapping("/actuel/{statut}")
    public String getReservationsWithCurrentStatus(@PathVariable String statut, Model model) {
        try {
            List<StatutReservation> currentStatuts = statutReservationService.getReservationsWithCurrentStatus(statut);
            model.addAttribute("statutReservations", currentStatuts);
            model.addAttribute("pageTitle", "Réservations avec Statut Actuel: " + statut);
            model.addAttribute("isCurrentStatusView", true);
            model.addAttribute("currentStatusFilter", statut);
            
            return "statut-reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des statuts actuels: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Quick status change for a reservation (AJAX endpoint)
     */
    @PostMapping("/reservation/{reservationId}/changer-statut")
    @ResponseBody
    public String quickStatusChange(@PathVariable Long reservationId,
                                  @RequestParam("statut") String statut) {
        try {
            statutReservationService.createStatutReservation(reservationId, statut);
            return "Statut changé avec succès!";
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }

    /**
     * Get status history for a reservation
     */
    @GetMapping("/reservation/{reservationId}/historique")
    public String getStatusHistory(@PathVariable Long reservationId, Model model) {
        try {
            Optional<Reservation> reservationOpt = reservationService.getReservationById(reservationId);
            if (!reservationOpt.isPresent()) {
                model.addAttribute("error", "Réservation non trouvée avec l'ID: " + reservationId);
                return "error";
            }

            Reservation reservation = reservationOpt.get();
            List<StatutReservation> statusHistory = statutReservationService.getStatusHistoryByReservation(reservation);

            model.addAttribute("reservation", reservation);
            model.addAttribute("statutReservations", statusHistory);
            model.addAttribute("pageTitle", "Historique des Statuts - Réservation #" + reservationId);
            model.addAttribute("isHistoryView", true);
            
            return "statut-reservation-list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération de l'historique: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Check reservation status (AJAX endpoint)
     */
    @GetMapping("/reservation/{reservationId}/check/{statusType}")
    @ResponseBody
    public boolean checkReservationStatus(@PathVariable Long reservationId, @PathVariable String statusType) {
        try {
            switch (statusType.toLowerCase()) {
                case "pending":
                    return statutReservationService.isReservationPending(reservationId);
                case "confirmed":
                    return statutReservationService.isReservationConfirmed(reservationId);
                case "cancelled":
                    return statutReservationService.isReservationCancelled(reservationId);
                case "expired":
                    return statutReservationService.isReservationExpired(reservationId);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
