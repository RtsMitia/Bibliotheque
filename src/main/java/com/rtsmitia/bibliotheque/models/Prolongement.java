package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prolongement")
public class Prolongement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_plg")
    private LocalDateTime dateProlongement;

    @Column(name = "nouvelle_date_retour")
    private LocalDateTime nouvelleDateRetour;

    @ManyToOne
    @JoinColumn(name = "id_pret", nullable = false)
    private Pret pret;

    @OneToMany(mappedBy = "prolongement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatutProlongement> statutsProlongement;

    // Default constructor
    public Prolongement() {}

    // Constructor with parameters
    public Prolongement(LocalDateTime dateProlongement, LocalDateTime nouvelleDateRetour, Pret pret) {
        this.dateProlongement = dateProlongement;
        this.nouvelleDateRetour = nouvelleDateRetour;
        this.pret = pret;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateProlongement() {
        return dateProlongement;
    }

    public void setDateProlongement(LocalDateTime dateProlongement) {
        this.dateProlongement = dateProlongement;
    }

    public LocalDateTime getNouvelleDateRetour() {
        return nouvelleDateRetour;
    }

    public void setNouvelleDateRetour(LocalDateTime nouvelleDateRetour) {
        this.nouvelleDateRetour = nouvelleDateRetour;
    }

    public Pret getPret() {
        return pret;
    }

    public void setPret(Pret pret) {
        this.pret = pret;
    }

    public List<StatutProlongement> getStatutsProlongement() {
        return statutsProlongement;
    }

    public void setStatutsProlongement(List<StatutProlongement> statutsProlongement) {
        this.statutsProlongement = statutsProlongement;
    }

    @Override
    public String toString() {
        return "Prolongement{" +
                "id=" + id +
                ", dateProlongement=" + dateProlongement +
                ", nouvelleDateRetour=" + nouvelleDateRetour +
                ", pret=" + (pret != null ? pret.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prolongement that = (Prolongement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
