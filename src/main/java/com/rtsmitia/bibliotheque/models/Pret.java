package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pret")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "date_retour")
    private LocalDateTime dateRetour;

    @ManyToOne
    @JoinColumn(name = "numero_adherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "id_type_pret", nullable = false)
    private TypePret typePret;

    @ManyToOne
    @JoinColumn(name = "id_exemplaire", nullable = false)
    private Exemplaire exemplaire;

    @OneToMany(mappedBy = "pret", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prolongement> prolongements;

    @OneToMany(mappedBy = "pret", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatutPret> statutsPret;

    // Default constructor
    public Pret() {
        this.prolongements = new java.util.ArrayList<>();
        this.statutsPret = new java.util.ArrayList<>();
    }

    // Constructor with parameters
    public Pret(LocalDateTime dateDebut, LocalDateTime dateFin, Adherent adherent, TypePret typePret, Exemplaire exemplaire) {
        this();
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.adherent = adherent;
        this.typePret = typePret;
        this.exemplaire = exemplaire;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDateTime getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDateTime dateRetour) {
        this.dateRetour = dateRetour;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public TypePret getTypePret() {
        return typePret;
    }

    public void setTypePret(TypePret typePret) {
        this.typePret = typePret;
    }

    public Exemplaire getExemplaire() {
        return exemplaire;
    }

    public void setExemplaire(Exemplaire exemplaire) {
        this.exemplaire = exemplaire;
    }

    public List<Prolongement> getProlongements() {
        return prolongements;
    }

    public void setProlongements(List<Prolongement> prolongements) {
        this.prolongements = prolongements;
    }

    public List<StatutPret> getStatutsPret() {
        return statutsPret;
    }

    public void setStatutsPret(List<StatutPret> statutsPret) {
        this.statutsPret = statutsPret;
    }

    // Helper method to check if book is returned
    public boolean isReturned() {
        return dateRetour != null;
    }

    // Helper method to check if book is overdue
    public boolean isOverdue() {
        return !isReturned() && dateFin != null && LocalDateTime.now().isAfter(dateFin);
    }
    
    // Helper method to get current loan status
    public StatutPret getCurrentStatus() {
        if (statutsPret == null || statutsPret.isEmpty()) {
            return null;
        }
        
        // Return the most recent status (assuming they are ordered by date)
        return statutsPret.stream()
                .max((s1, s2) -> {
                    if (s1.getDateChangement() == null && s2.getDateChangement() == null) return 0;
                    if (s1.getDateChangement() == null) return -1;
                    if (s2.getDateChangement() == null) return 1;
                    return s1.getDateChangement().compareTo(s2.getDateChangement());
                })
                .orElse(null);
    }
    
    // Helper method to check if loan is approved
    public boolean isApproved() {
        StatutPret currentStatus = getCurrentStatus();
        return currentStatus != null && currentStatus.getStatut() == StatutPret.StatutPretEnum.valide;
    }
    
    // Helper method to check if loan is pending
    public boolean isPending() {
        StatutPret currentStatus = getCurrentStatus();
        return currentStatus != null && currentStatus.isPending();
    }

    @Override
    public String toString() {
        return "Pret{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", dateRetour=" + dateRetour +
                ", adherent=" + (adherent != null ? adherent.getNumeroAdherent() : null) +
                ", typePret=" + (typePret != null ? typePret.getLibelle() : null) +
                ", exemplaire=" + (exemplaire != null ? exemplaire.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pret pret = (Pret) o;
        return id != null && id.equals(pret.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
