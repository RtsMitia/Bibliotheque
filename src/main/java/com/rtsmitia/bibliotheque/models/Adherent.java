package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adherents")
public class Adherent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_adherent", nullable = false, unique = true, length = 10)
    private String numeroAdherent;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_type_adherent", nullable = false)
    private TypeAdherent typeAdherent;

    @Column(length = 20)
    private String telephone;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    // === Constructor to auto-set date_inscription ===
    @PrePersist
    protected void onCreate() {`
        this.dateInscription = LocalDateTime.now();
    }

    // === Getters and Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroAdherent() {
        return numeroAdherent;
    }

    public void setNumeroAdherent(String numeroAdherent) {
        this.numeroAdherent = numeroAdherent;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TypeAdherent getTypeAdherent() {
        return typeAdherent;
    }

    public void setTypeAdherent(TypeAdherent typeAdherent) {
        this.typeAdherent = typeAdherent;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }
}
