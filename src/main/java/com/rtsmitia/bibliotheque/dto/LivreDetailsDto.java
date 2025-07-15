package com.rtsmitia.bibliotheque.dto;

import com.rtsmitia.bibliotheque.models.Exemplaire;
import com.rtsmitia.bibliotheque.models.Livre;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LivreDetailsDto {
    private Long id;
    private String titre;
    private String resume;
    private LocalDateTime dateSortie;
    private String auteurNom;
    private List<String> genres;
    private List<ExemplaireDto> exemplaires;
    private int totalExemplaires;
    private int exemplairesDiponibles;

    // Default constructor
    public LivreDetailsDto() {}

    // Constructor from Livre entity
    public LivreDetailsDto(Livre livre) {
        this.id = livre.getId();
        this.titre = livre.getTitre();
        this.resume = livre.getResume();
        this.dateSortie = livre.getDateSortie();
        
        // Auteur information
        if (livre.getAuteur() != null) {
            this.auteurNom = livre.getAuteur().getNom();
            // Note: Auteur only has 'nom' field, no 'prenom'
        }
        
        // Genres
        if (livre.getGenres() != null) {
            this.genres = livre.getGenres().stream()
                    .map(genre -> genre.getLibelle())
                    .collect(Collectors.toList());
        }
        
        // Exemplaires
        if (livre.getExemplaires() != null) {
            this.exemplaires = livre.getExemplaires().stream()
                    .map(ExemplaireDto::new)
                    .collect(Collectors.toList());
            this.totalExemplaires = livre.getExemplaires().size();
            this.exemplairesDiponibles = (int) livre.getExemplaires().stream()
                    .filter(Exemplaire::isAvailable)
                    .count();
        } else {
            this.totalExemplaires = 0;
            this.exemplairesDiponibles = 0;
        }
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

    public String getAuteurNom() {
        return auteurNom;
    }

    public void setAuteurNom(String auteurNom) {
        this.auteurNom = auteurNom;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<ExemplaireDto> getExemplaires() {
        return exemplaires;
    }

    public void setExemplaires(List<ExemplaireDto> exemplaires) {
        this.exemplaires = exemplaires;
    }

    public int getTotalExemplaires() {
        return totalExemplaires;
    }

    public void setTotalExemplaires(int totalExemplaires) {
        this.totalExemplaires = totalExemplaires;
    }

    public int getExemplairesDiponibles() {
        return exemplairesDiponibles;
    }

    public void setExemplairesDiponibles(int exemplairesDiponibles) {
        this.exemplairesDiponibles = exemplairesDiponibles;
    }

    // Nested DTO for Exemplaire
    public static class ExemplaireDto {
        private Long id;
        private LocalDateTime dateArrivee;
        private boolean disponible;

        public ExemplaireDto() {}

        public ExemplaireDto(Exemplaire exemplaire) {
            this.id = exemplaire.getId();
            this.dateArrivee = exemplaire.getDateArrivee();
            this.disponible = exemplaire.isAvailable();
        }

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

        public boolean isDisponible() {
            return disponible;
        }

        public void setDisponible(boolean disponible) {
            this.disponible = disponible;
        }
    }
}
