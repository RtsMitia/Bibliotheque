---Type Adherent
INSERT INTO type_adherent (libelle) VALUES 
('etudiant'),
('professionel');

---Les Contraintes
INSERT INTO les_contraint (type_contrainte) VALUES 
('mineur'),
('handicap moteur');

---Genres
INSERT INTO genre (libelle) VALUES 
('Fiction'),
('Science-Fiction'),
('Fantastique'),
('Romance'),
('Thriller'),
('Policier'),
('Mystère'),
('Horreur'),
('Aventure'),
('Historique'),
('Biographie'),
('Autobiographie'),
('Essai'),
('Philosophie'),
('Sciences'),
('Technologie'),
('Art'),
('Cuisine'),
('Voyage'),
('Jeunesse'),
('Bande Dessinée'),
('Manga'),
('Poésie'),
('Théâtre'),
('Développement Personnel');

---Auteurs
INSERT INTO auteur (nom) VALUES 
('Victor Hugo'),
('Alexandre Dumas'),
('Jules Verne'),
('Emile Zola'),
('Albert Camus'),
('Jean-Paul Sartre'),
('J.R.R. Tolkien'),
('J.K. Rowling'),
('Agatha Christie'),
('Charles Dickens'),
('Jane Austen'),
('George Orwell'),
('Gabriel Garcia Marquez'),
('Ernest Hemingway'),
('John Steinbeck'),
('F. Scott Fitzgerald'),
('Mark Twain'),
('Stephen King'),
('Isaac Asimov'),
('Frank Herbert'),
('Fiodor Dostoïevski'),
('Leon Tolstoi'),
('Franz Kafka'),
('Haruki Murakami'),
('Umberto Eco');

---Type de Prêt
INSERT INTO type_pret (libelle) VALUES 
('a emporter'),
('lire sur place');

---Quota Prêt
INSERT INTO quota_pret (nombre_livre, nombre_jour_pret, date_changement, id_type_adherent) VALUES 
(5, 10, CURRENT_DATE, 1), -- etudiant: 5 livres, 14 jours
(7, 5, CURRENT_DATE, 2);  -- professionel: 7 livres, 21 jours

---Pénalité Configuration
INSERT INTO penalite_config (id, nombre_jour, date_changement, id_type_adherent) VALUES 
(1, 7, CURRENT_DATE, 1),  -- etudiant: 7 jours de pénalité
(2, 3, CURRENT_DATE, 2);  -- professionel: 3 jours de pénalité

---Jours Fériés
INSERT INTO jour_ferie (date_debut, date_fin, date_creation) VALUES 
-- Christmas period (Dec 24-26, 2025)
('2025-12-24', '2025-12-26', CURRENT_DATE);
-- New Year period (Dec 31, 2025 - Jan 2, 2026)
('2025-12-31', '2026-01-02', CURRENT_DATE),
-- Easter period (April 20-21, 2026) 
('2026-04-20', '2026-04-21', CURRENT_DATE),
-- Single day holidays
('2025-07-14', NULL, CURRENT_DATE), -- Bastille Day
('2025-11-11', NULL, CURRENT_DATE), -- Armistice Day
('2025-05-01', NULL, CURRENT_DATE), -- Labor Day
('2025-05-08', NULL, CURRENT_DATE); -- Victory Day
