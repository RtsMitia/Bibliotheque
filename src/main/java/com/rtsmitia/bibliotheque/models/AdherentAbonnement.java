package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adherent_abonnement")
public class AdherentAbonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "debut_abonnement", nullable = false)
    private LocalDate debutAbonnement;

    @Column(name = "fin_abonnement", nullable = false)
    private LocalDate finAbonnement;

    @Column(name = "date_changement", nullable = false)
    private LocalDate dateChangement;

    @ManyToOne
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;

    // Default constructor
    public AdherentAbonnement() {}

    // Constructor with parameters
    public AdherentAbonnement(LocalDate debutAbonnement, LocalDate finAbonnement, LocalDate dateChangement, Adherent adherent) {
        this.debutAbonnement = debutAbonnement;
        this.finAbonnement = finAbonnement;
        this.dateChangement = dateChangement;
        this.adherent = adherent;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDebutAbonnement() {
        return debutAbonnement;
    }

    public void setDebutAbonnement(LocalDate debutAbonnement) {
        this.debutAbonnement = debutAbonnement;
    }

    public LocalDate getFinAbonnement() {
        return finAbonnement;
    }

    public void setFinAbonnement(LocalDate finAbonnement) {
        this.finAbonnement = finAbonnement;
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

    @Override
    public String toString() {
        return "AdherentAbonnement{" +
                "id=" + id +
                ", debutAbonnement=" + debutAbonnement +
                ", finAbonnement=" + finAbonnement +
                ", dateChangement=" + dateChangement +
                ", adherent=" + (adherent != null ? adherent.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdherentAbonnement that = (AdherentAbonnement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
