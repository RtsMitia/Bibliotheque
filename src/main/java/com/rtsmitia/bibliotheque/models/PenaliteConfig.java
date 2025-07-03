package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "penalite_config")
public class PenaliteConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_jour", nullable = false)
    private Integer nombreJour; // nombre de jour de penalite, can't borrow any books
    
    @Column(name = "date_changement")
    private LocalDate dateChangement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_adherent", nullable = false)
    private TypeAdherent typeAdherent;
    
    // Constructeurs
    public PenaliteConfig() {}
    
    public PenaliteConfig(Integer nombreJour, LocalDate dateChangement, TypeAdherent typeAdherent) {
        this.nombreJour = nombreJour;
        this.dateChangement = dateChangement;
        this.typeAdherent = typeAdherent;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getNombreJour() {
        return nombreJour;
    }
    
    public void setNombreJour(Integer nombreJour) {
        this.nombreJour = nombreJour;
    }
    
    public LocalDate getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDate dateChangement) {
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
        return "PenaliteConfig{" +
                "id=" + id +
                ", nombreJour=" + nombreJour +
                ", dateChangement=" + dateChangement +
                ", typeAdherent=" + (typeAdherent != null ? typeAdherent.getLibelle() : "null") +
                '}';
    }
}
