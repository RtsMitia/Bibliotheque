package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quota_reservation")
public class QuotaReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_livre")
    private Integer nombreLivre;

    @Column(name = "date_changement")
    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "id_type_adherent", nullable = false)
    private TypeAdherent typeAdherent;

    // Default constructor
    public QuotaReservation() {}

    // Constructor with parameters
    public QuotaReservation(Integer nombreLivre, LocalDateTime dateChangement, TypeAdherent typeAdherent) {
        this.nombreLivre = nombreLivre;
        this.dateChangement = dateChangement;
        this.typeAdherent = typeAdherent;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNombreLivre() {
        return nombreLivre;
    }

    public void setNombreLivre(Integer nombreLivre) {
        this.nombreLivre = nombreLivre;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public TypeAdherent getTypeAdherent() {
        return typeAdherent;
    }

    public void setTypeAdherent(TypeAdherent typeAdherent) {
        this.typeAdherent = typeAdherent;
    }

    @Override
    public String toString() {
        return "QuotaReservation{" +
                "id=" + id +
                ", nombreLivre=" + nombreLivre +
                ", dateChangement=" + dateChangement +
                ", typeAdherent=" + (typeAdherent != null ? typeAdherent.getLibelle() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuotaReservation that = (QuotaReservation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
