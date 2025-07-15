package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statut_reservation")
public class StatutReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String statut;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;

    // Enum for status types
    public enum StatutType {
        ATTENTE("en attente"),
        CONFIRMEE("confirmee"),
        ANNULEE("annulee"),
        EXPIREE("expiree");

        private final String libelle;

        StatutType(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Default constructor
    public StatutReservation() {}

    // Constructor with parameters
    public StatutReservation(String statut, LocalDateTime dateChangement, Reservation reservation) {
        this.statut = statut;
        this.dateChangement = dateChangement;
        this.reservation = reservation;
    }

    // Constructor with enum
    public StatutReservation(StatutType statutType, LocalDateTime dateChangement, Reservation reservation) {
        this.statut = statutType.getLibelle();
        this.dateChangement = dateChangement;
        this.reservation = reservation;
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

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "StatutReservation{" +
                "id=" + id +
                ", statut='" + statut + '\'' +
                ", dateChangement=" + dateChangement +
                ", reservation=" + (reservation != null ? reservation.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatutReservation that = (StatutReservation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
