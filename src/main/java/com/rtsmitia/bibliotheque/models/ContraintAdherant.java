package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contraint_adherant")
public class ContraintAdherant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "id_contraint", nullable = false)
    private LesContraints contrainte;

    // Default constructor
    public ContraintAdherant() {}

    // Constructor with parameters
    public ContraintAdherant(Adherent adherent, LesContraints contrainte) {
        this.adherent = adherent;
        this.contrainte = contrainte;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public LesContraints getContrainte() {
        return contrainte;
    }

    public void setContrainte(LesContraints contrainte) {
        this.contrainte = contrainte;
    }

    @Override
    public String toString() {
        return "ContraintAdherant{" +
                "id=" + id +
                ", adherent=" + (adherent != null ? adherent.getId() : null) +
                ", contrainte=" + (contrainte != null ? contrainte.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContraintAdherant that = (ContraintAdherant) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
