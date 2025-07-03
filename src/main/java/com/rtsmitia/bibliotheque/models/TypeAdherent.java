package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "type_adherent")
public class TypeAdherent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL in PostgreSQL
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @OneToMany(mappedBy = "typeAdherent", cascade = CascadeType.ALL)
    private List<Adherent> adherents;

    @OneToMany(mappedBy = "typeAdherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuotaPret> quotaPrets;

    @OneToMany(mappedBy = "typeAdherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuotaReservation> quotaReservations;

    @OneToMany(mappedBy = "typeAdherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PenaliteConfig> penaliteConfigs;

    // === Getters and Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Adherent> getAdherents() {
        return adherents;
    }

    public void setAdherents(List<Adherent> adherents) {
        this.adherents = adherents;
    }

    public List<QuotaPret> getQuotaPrets() {
        return quotaPrets;
    }

    public void setQuotaPrets(List<QuotaPret> quotaPrets) {
        this.quotaPrets = quotaPrets;
    }

    public List<QuotaReservation> getQuotaReservations() {
        return quotaReservations;
    }

    public void setQuotaReservations(List<QuotaReservation> quotaReservations) {
        this.quotaReservations = quotaReservations;
    }

    public List<PenaliteConfig> getPenaliteConfigs() {
        return penaliteConfigs;
    }

    public void setPenaliteConfigs(List<PenaliteConfig> penaliteConfigs) {
        this.penaliteConfigs = penaliteConfigs;
    }
}
