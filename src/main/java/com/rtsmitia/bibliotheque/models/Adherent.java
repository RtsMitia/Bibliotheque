package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "adherents")
public class Adherent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_adherent", insertable = false, updatable = false)
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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateInscription;

    @OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueStatutAbonnement> historiqueStatuts;

    @OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Penalite> penalites;

    @OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdherentAbonnement> abonnements;

    @ManyToMany
    @JoinTable(
        name = "contraint_adherant",
        joinColumns = @JoinColumn(name = "numero_adherent"),
        inverseJoinColumns = @JoinColumn(name = "id_contraint")
    )
    private List<LesContraints> contraintes;

    // Default constructor
    public Adherent() {
        this.contraintes = new ArrayList<>();
        this.penalites = new ArrayList<>();
    }

    // === Constructor to auto-set date_inscription ===
    @PrePersist
    protected void onCreate() {
        // Only set date, number is handled by database trigger
        if (this.dateInscription == null) {
            this.dateInscription = LocalDateTime.now();
        }
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

    public List<HistoriqueStatutAbonnement> getHistoriqueStatuts() {
        return historiqueStatuts;
    }

    public void setHistoriqueStatuts(List<HistoriqueStatutAbonnement> historiqueStatuts) {
        this.historiqueStatuts = historiqueStatuts;
    }

    public List<Penalite> getPenalites() {
        return penalites;
    }

    public void setPenalites(List<Penalite> penalites) {
        this.penalites = penalites;
    }

    public List<AdherentAbonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<AdherentAbonnement> abonnements) {
        this.abonnements = abonnements;
    }

    public List<LesContraints> getContraintes() {
        return contraintes;
    }

    public void setContraintes(List<LesContraints> contraintes) {
        this.contraintes = contraintes;
    }
    
    public String getFormattedDateInscription() {
        if (this.dateInscription == null) {
            return "";
        }
        return String.format("%02d/%02d/%d %02d:%02d", 
                this.dateInscription.getDayOfMonth(),
                this.dateInscription.getMonthValue(),
                this.dateInscription.getYear(),
                this.dateInscription.getHour(),
                this.dateInscription.getMinute());
    }
    // Add this for JSP compatibility
    public Date getDateInscriptionAsDate() {
        return Date.from(dateInscription.atZone(ZoneId.systemDefault()).toInstant());
    }
}
