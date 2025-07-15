package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_reservation")
    private LocalDateTime dateReservation;

    @Column(name = "date_debut_pret", nullable = false)
    private LocalDateTime dateDebutPret;

    @ManyToOne
    @JoinColumn(name = "id_exemplaire", nullable = false)
    private Exemplaire exemplaire;

    @ManyToOne
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatutReservation> statutReservations;

    // Default constructor
    public Reservation() {}

    // Constructor with parameters
    public Reservation(LocalDateTime dateReservation, LocalDateTime dateDebutPret, Exemplaire exemplaire, Adherent adherent) {
        this.dateReservation = dateReservation;
        this.dateDebutPret = dateDebutPret;
        this.exemplaire = exemplaire;
        this.adherent = adherent;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalDateTime getDateDebutPret() {
        return dateDebutPret;
    }

    public void setDateDebutPret(LocalDateTime dateDebutPret) {
        this.dateDebutPret = dateDebutPret;
    }

    public Exemplaire getExemplaire() {
        return exemplaire;
    }

    public void setExemplaire(Exemplaire exemplaire) {
        this.exemplaire = exemplaire;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public List<StatutReservation> getStatutReservations() {
        return statutReservations;
    }

    public void setStatutReservations(List<StatutReservation> statutReservations) {
        this.statutReservations = statutReservations;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dateReservation=" + dateReservation +
                ", dateDebutPret=" + dateDebutPret +
                ", exemplaire=" + (exemplaire != null ? exemplaire.getId() : null) +
                ", adherent=" + (adherent != null ? adherent.getNumeroAdherent() : null) +
                '}';
    }

    // Helper methods for JSP date formatting
    public Date getDateReservationAsDate() {
        if (this.dateReservation == null) {
            return null;
        }
        return Date.from(this.dateReservation.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getDateDebutPretAsDate() {
        if (this.dateDebutPret == null) {
            return null;
        }
        return Date.from(this.dateDebutPret.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
