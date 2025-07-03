package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historique_statut_abonnement")
public class HistoriqueStatutAbonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutAbonnement statut;

    @Column(name = "date_changement", nullable = false)
    private LocalDate dateChangement;

    @ManyToOne
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;

    // Enum for status values
    public enum StatutAbonnement {
        demande,
        en_attente("en attente"),
        valide,
        refuse;

        private final String displayName;

        StatutAbonnement() {
            this.displayName = name();
        }

        StatutAbonnement(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor
    public HistoriqueStatutAbonnement() {}

    public HistoriqueStatutAbonnement(StatutAbonnement statut, LocalDate dateChangement, Adherent adherent) {
        this.statut = statut;
        this.dateChangement = dateChangement;
        this.adherent = adherent;
    }

    // === Getters and Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatutAbonnement getStatut() {
        return statut;
    }

    public void setStatut(StatutAbonnement statut) {
        this.statut = statut;
    }

    public LocalDate getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDate dateChangement) {
        this.dateChangement = dateChangement;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }
}
