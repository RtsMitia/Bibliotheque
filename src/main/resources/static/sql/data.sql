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
INSERT INTO quota_pret (nombre_livre, nombre_jour_pret, date_changement, id_type_adherant) VALUES 
(5, 10, CURRENT_DATE, 1), -- etudiant: 5 livres, 10 jours
(7, 5, CURRENT_DATE, 2);  -- professionel: 7 livres, 5 jours

---Pénalité Configuration
INSERT INTO penalite_config (nombre_jour, date_changement, id_type_adherent) VALUES 
(7, CURRENT_DATE, 1),  -- etudiant: 7 jours de pénalité
(3, CURRENT_DATE, 2);  -- professionel: 3 jours de pénalité
