package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quota_pret")
public class QuotaPret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_livre")
    private Integer nombreLivre;

    @Column(name = "nombre_jour_pret", nullable = false)
    private Integer nombreJourPret;

    @Column(name = "date_changement")
    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "id_type_adherant", nullable = false)
    private TypeAdherent typeAdherent;

    // Default constructor
    public QuotaPret() {}

    // Constructor with parameters
    public QuotaPret(Integer nombreLivre, Integer nombreJourPret, LocalDateTime dateChangement, TypeAdherent typeAdherent) {
        this.nombreLivre = nombreLivre;
        this.nombreJourPret = nombreJourPret;
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

    public Integer getNombreJourPret() {
        return nombreJourPret;
    }

    public void setNombreJourPret(Integer nombreJourPret) {
        this.nombreJourPret = nombreJourPret;
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
        return "QuotaPret{" +
                "id=" + id +
                ", nombreLivre=" + nombreLivre +
                ", nombreJourPret=" + nombreJourPret +
                ", dateChangement=" + dateChangement +
                ", typeAdherent=" + (typeAdherent != null ? typeAdherent.getLibelle() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuotaPret quotaPret = (QuotaPret) o;
        return id != null && id.equals(quotaPret.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
