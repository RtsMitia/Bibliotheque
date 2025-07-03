package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;

@Entity
@Table(name = "les_contraint")
public class LesContraints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_contrainte", nullable = false, length = 50)
    private String typeContrainte;

    // Default constructor
    public LesContraints() {}

    // Constructor with parameters
    public LesContraints(String typeContrainte) {
        this.typeContrainte = typeContrainte;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeContrainte() {
        return typeContrainte;
    }

    public void setTypeContrainte(String typeContrainte) {
        this.typeContrainte = typeContrainte;
    }

    @Override
    public String toString() {
        return "LesContraints{" +
                "id=" + id +
                ", typeContrainte='" + typeContrainte + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LesContraints that = (LesContraints) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
