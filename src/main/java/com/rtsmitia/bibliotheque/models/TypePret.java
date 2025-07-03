package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "type_pret")
public class TypePret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String libelle;

    @OneToMany(mappedBy = "typePret", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pret> prets;

    // Default constructor
    public TypePret() {}

    // Constructor with parameters
    public TypePret(String libelle) {
        this.libelle = libelle;
    }

    // Getters and setters
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

    public List<Pret> getPrets() {
        return prets;
    }

    public void setPrets(List<Pret> prets) {
        this.prets = prets;
    }

    @Override
    public String toString() {
        return "TypePret{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePret typePret = (TypePret) o;
        return id != null && id.equals(typePret.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
