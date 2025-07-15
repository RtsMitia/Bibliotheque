package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exemplaire")
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_arrivee")
    private LocalDateTime dateArrivee;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    private Livre livre;

    @OneToMany(mappedBy = "exemplaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "exemplaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pret> prets;

    // Default constructor
    public Exemplaire() {}

    // Constructor with parameters
    public Exemplaire(LocalDateTime dateArrivee, Livre livre) {
        this.dateArrivee = dateArrivee;
        this.livre = livre;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDateTime dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Pret> getPrets() {
        return prets;
    }

    public void setPrets(List<Pret> prets) {
        this.prets = prets;
    }

    /**
     * Get dateArrivee as java.util.Date for JSP formatting
     */
    public java.util.Date getDateArriveeAsDate() {
        if (dateArrivee == null) {
            return null;
        }
        return java.util.Date.from(dateArrivee.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    /**
     * Check if this exemplaire is available (not currently borrowed)
     */
    public boolean isAvailable() {
        if (prets == null || prets.isEmpty()) {
            return true;
        }
        
        // Check if there's any active loan (not returned)
        return prets.stream().noneMatch(pret -> pret.getDateRetour() == null);
    }

    @Override
    public String toString() {
        return "Exemplaire{" +
                "id=" + id +
                ", dateArrivee=" + dateArrivee +
                ", livre=" + (livre != null ? livre.getTitre() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemplaire that = (Exemplaire) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
