package com.rtsmitia.bibliotheque.dto;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Penalite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdherentDetailsDto {
    private Long id;
    private String numeroAdherent;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDateTime dateInscription;
    private String typeAdherent;
    private boolean isAbonne;
    private LocalDate dateFinAbonnement;
    private List<PenaliteDto> penalitesActives;
    private int nombrePenalitesActives;
    private boolean peutEmprunter;
    private String statutAbonnement;

    // Default constructor
    public AdherentDetailsDto() {}

    // Constructor from Adherent entity
    public AdherentDetailsDto(Adherent adherent, boolean isAbonne, LocalDate dateFinAbonnement, 
                             List<Penalite> penalitesActives, boolean peutEmprunter, String statutAbonnement) {
        this.id = adherent.getId();
        this.numeroAdherent = adherent.getNumeroAdherent();
        this.prenom = adherent.getPrenom();
        this.nom = adherent.getNom();
        this.email = adherent.getEmail();
        this.telephone = adherent.getTelephone();
        this.adresse = adherent.getAdresse();
        this.dateInscription = adherent.getDateInscription();
        
        // Type adherent
        if (adherent.getTypeAdherent() != null) {
            this.typeAdherent = adherent.getTypeAdherent().getLibelle();
        }
        
        // Subscription status
        this.isAbonne = isAbonne;
        this.dateFinAbonnement = dateFinAbonnement;
        this.peutEmprunter = peutEmprunter;
        this.statutAbonnement = statutAbonnement;
        
        // Active penalties
        if (penalitesActives != null) {
            this.penalitesActives = penalitesActives.stream()
                    .map(PenaliteDto::new)
                    .collect(Collectors.toList());
            this.nombrePenalitesActives = penalitesActives.size();
        } else {
            this.nombrePenalitesActives = 0;
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroAdherent() {
        return numeroAdherent;
    }

    public void setNumeroAdherent(String numeroAdherent) {
        this.numeroAdherent = numeroAdherent;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getTypeAdherent() {
        return typeAdherent;
    }

    public void setTypeAdherent(String typeAdherent) {
        this.typeAdherent = typeAdherent;
    }

    public boolean isAbonne() {
        return isAbonne;
    }

    public void setAbonne(boolean abonne) {
        isAbonne = abonne;
    }

    public LocalDate getDateFinAbonnement() {
        return dateFinAbonnement;
    }

    public void setDateFinAbonnement(LocalDate dateFinAbonnement) {
        this.dateFinAbonnement = dateFinAbonnement;
    }

    public List<PenaliteDto> getPenalitesActives() {
        return penalitesActives;
    }

    public void setPenalitesActives(List<PenaliteDto> penalitesActives) {
        this.penalitesActives = penalitesActives;
    }

    public int getNombrePenalitesActives() {
        return nombrePenalitesActives;
    }

    public void setNombrePenalitesActives(int nombrePenalitesActives) {
        this.nombrePenalitesActives = nombrePenalitesActives;
    }

    public boolean isPeutEmprunter() {
        return peutEmprunter;
    }

    public void setPeutEmprunter(boolean peutEmprunter) {
        this.peutEmprunter = peutEmprunter;
    }

    public String getStatutAbonnement() {
        return statutAbonnement;
    }

    public void setStatutAbonnement(String statutAbonnement) {
        this.statutAbonnement = statutAbonnement;
    }

    // Nested DTO for Penalite
    public static class PenaliteDto {
        private Long id;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private boolean active;

        public PenaliteDto() {}

        public PenaliteDto(Penalite penalite) {
            this.id = penalite.getId();
            this.dateDebut = penalite.getDateDebut();
            this.dateFin = penalite.getDateFin();
            this.active = penalite.isActive();
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LocalDate getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDate getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
