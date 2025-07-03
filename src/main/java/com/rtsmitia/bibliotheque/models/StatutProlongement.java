package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statut_prolongement")
public class StatutProlongement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String statut;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "id_prolongement", nullable = false)
    private Prolongement prolongement;

    // Enum for status types
    public enum StatutType {
        DEMANDE("demandé"),
        APPROUVE("approuvé"),
        REFUSE("refusé"),
        ANNULE("annulé");

        private final String libelle;

        StatutType(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Default constructor
    public StatutProlongement() {}

    // Constructor with parameters
    public StatutProlongement(String statut, LocalDateTime dateChangement, Prolongement prolongement) {
        this.statut = statut;
        this.dateChangement = dateChangement;
        this.prolongement = prolongement;
    }

    // Constructor with enum
    public StatutProlongement(StatutType statutType, LocalDateTime dateChangement, Prolongement prolongement) {
        this.statut = statutType.getLibelle();
        this.dateChangement = dateChangement;
        this.prolongement = prolongement;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public Prolongement getProlongement() {
        return prolongement;
    }

    public void setProlongement(Prolongement prolongement) {
        this.prolongement = prolongement;
    }

    @Override
    public String toString() {
        return "StatutProlongement{" +
                "id=" + id +
                ", statut='" + statut + '\'' +
                ", dateChangement=" + dateChangement +
                ", prolongement=" + (prolongement != null ? prolongement.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatutProlongement that = (StatutProlongement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
