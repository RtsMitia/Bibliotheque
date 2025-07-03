package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "livre")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String titre;

    @Column(length = 255)
    private String resume;

    @Column(name = "date_sortie")
    private LocalDateTime dateSortie;

    @ManyToOne
    @JoinColumn(name = "id_auteur", nullable = false)
    private Auteur auteur;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exemplaire> exemplaires;

    @ManyToMany
    @JoinTable(
        name = "livre_genre",
        joinColumns = @JoinColumn(name = "id_livre"),
        inverseJoinColumns = @JoinColumn(name = "id_genre")
    )
    private List<Genre> genres;

    @ManyToMany
    @JoinTable(
        name = "livre_contraint",
        joinColumns = @JoinColumn(name = "id_livre"),
        inverseJoinColumns = @JoinColumn(name = "id_contraint")
    )
    private List<LesContraints> contraintes;

    // Default constructor
    public Livre() {}

    // Constructor with parameters
    public Livre(String titre, String resume, LocalDateTime dateSortie, Auteur auteur) {
        this.titre = titre;
        this.resume = resume;
        this.dateSortie = dateSortie;
        this.auteur = auteur;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public LocalDateTime getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDateTime dateSortie) {
        this.dateSortie = dateSortie;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public List<Exemplaire> getExemplaires() {
        return exemplaires;
    }

    public void setExemplaires(List<Exemplaire> exemplaires) {
        this.exemplaires = exemplaires;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<LesContraints> getContraintes() {
        return contraintes;
    }

    public void setContraintes(List<LesContraints> contraintes) {
        this.contraintes = contraintes;
    }

    public Date getDateSortieAsDate() {
        if (this.dateSortie == null) {
            return null;
        }
        return Date.from(this.dateSortie.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "Livre{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", resume='" + resume + '\'' +
                ", dateSortie=" + dateSortie +
                ", auteur=" + (auteur != null ? auteur.getNom() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livre livre = (Livre) o;
        return id != null && id.equals(livre.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
