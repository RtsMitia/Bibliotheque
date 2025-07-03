package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "penalite")
public class Penalite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;
    
    // Constructeurs
    public Penalite() {}
    
    public Penalite(LocalDate dateDebut, LocalDate dateFin, Adherent adherent) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.adherent = adherent;
    }
    
    // Getters et Setters
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
    
    public Adherent getAdherent() {
        return adherent;
    }
    
    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }
    
    // Méthodes utilitaires
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return today.isAfter(dateDebut.minusDays(1)) && today.isBefore(dateFin.plusDays(1));
    }
    
    public boolean isActive(LocalDate date) {
        return date.isAfter(dateDebut.minusDays(1)) && date.isBefore(dateFin.plusDays(1));
    }
    
    @Override
    public String toString() {
        return "Penalite{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", adherent=" + (adherent != null ? adherent.getNumeroAdherent() : "null") +
                '}';
    }
}
