package com.rtsmitia.bibliotheque.models;

import jakarta.persistence.*;

@Entity
@Table(name = "statut_pret")
public class StatutPret {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Convert(converter = StatutPretEnumConverter.class)
    @Column(nullable = false, length = 20)
    private StatutPretEnum statut;
    
    @Column(name = "date_changement", length = 50)
    private String dateChangement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pret", nullable = false)
    private Pret pret;
    
    // Enum for statut values
    public enum StatutPretEnum {
        demande("demande"), 
        en_attente("en attente"), 
        valide("valide"), 
        refuse("refuse");
        
        private final String value;
        
        StatutPretEnum(String value) {
            this.value = value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
    
    // Constructeurs
    public StatutPret() {}
    
    public StatutPret(StatutPretEnum statut, String dateChangement, Pret pret) {
        this.statut = statut;
        this.dateChangement = dateChangement;
        this.pret = pret;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public StatutPretEnum getStatut() {
        return statut;
    }
    
    public void setStatut(StatutPretEnum statut) {
        this.statut = statut;
    }
    
    public String getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(String dateChangement) {
        this.dateChangement = dateChangement;
    }
    
    public Pret getPret() {
        return pret;
    }
    
    public void setPret(Pret pret) {
        this.pret = pret;
    }
    
    // Méthodes utilitaires
    public boolean isActive() {
        return statut == StatutPretEnum.valide;
    }
    
    public boolean isPending() {
        return statut == StatutPretEnum.demande || statut == StatutPretEnum.en_attente;
    }
    
    public boolean isRejected() {
        return statut == StatutPretEnum.refuse;
    }
    
    @Override
    public String toString() {
        return "StatutPret{" +
                "id=" + id +
                ", statut=" + statut +
                ", dateChangement='" + dateChangement + '\'' +
                ", pret=" + (pret != null ? pret.getId() : "null") +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatutPret that = (StatutPret) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
