---Type Adherent
INSERT INTO type_adherent (libelle) VALUES 
('etudiant'),
('professionel'),
('enseignant');

---Les Contraintes
INSERT INTO les_contraint (type_contrainte) VALUES 
('mineur'),
('handicap moteur');

---Genres
INSERT INTO genre (libelle) VALUES 
('Fantastique'),
('Philosophie'),
('Jeunesse'),
('Literature Classique');

---Auteurs
INSERT INTO auteur (nom) VALUES 
('Victor Hugo'),
('Albert Camus'),
('J.K. Rowling');

---Type de Prêt
INSERT INTO type_pret (libelle) VALUES 
('a emporter'),
('lire sur place');

---Quota Prêt
INSERT INTO quota_pret (nombre_livre, nombre_jour_pret, date_changement, id_type_adherent) VALUES 
(2, 7, CURRENT_DATE, 1), -- etudiant: 2 livres, 7 jours
(3, 9, CURRENT_DATE, 3),  -- professionel: 3 livres, 9 jours
(4, 12, CURRENT_DATE, 2); 

CREATE TABLE quota_reservation(
   id SERIAL,
   nombre_livre INT,
   date_changement DATE,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

INSERT INTO quota_reservation (nombre_livre, date_changement, id_type_adherent) VALUES 
(1, CURRENT_DATE, 1),
(2, CURRENT_DATE, 3),
(3, CURRENT_DATE, 2);

---Pénalité Configuration
INSERT INTO penalite_config (id, nombre_jour, date_changement, id_type_adherent) VALUES 
(1, 10, CURRENT_DATE, 1),  -- etudiant: 7 jours de pénalité
(2, 8, CURRENT_DATE, 2),  -- professionel: 3 jours de pénalité
(3, 9, CURRENT_DATE, 3); 


---Jours Fériés
INSERT INTO jour_ferie (date_debut, date_fin, date_creation) VALUES 
-- Single day holidays
('2025-07-13', NULL, CURRENT_DATE),
('2025-07-20', NULL, CURRENT_DATE),
('2025-07-27', NULL, CURRENT_DATE),
('2025-08-03', NULL, CURRENT_DATE),
('2025-08-10', NULL, CURRENT_DATE),
('2025-08-17', NULL, CURRENT_DATE),
('2025-07-26', NULL, CURRENT_DATE),
('2025-07-19', NULL, CURRENT_DATE);

---Adhérents
INSERT INTO adherents (prenom, nom, email, telephone, adresse, date_inscription, id_type_adherent) VALUES 
('Amine', 'Bensaïd', 'amine.bensaid@email.com', '0601234567', '123 Rue A', '2025-01-15', 1), -- Etudiant
('Sarah', 'El Khattabi', 'sarah.elkhattabi@email.com', '0601234568', '124 Rue B', '2025-01-15', 1), -- Etudiant
('Youssef', 'Moujahid', 'youssef.moujahid@email.com', '0601234569', '125 Rue C', '2025-03-15', 1), -- Etudiant
('Nadia', 'Benali', 'nadia.benali@email.com', '0601234570', '126 Rue D', '2025-06-15', 3), -- Enseignant
('Karim', 'Haddadi', 'karim.haddadi@email.com', '0601234571', '127 Rue E', '2025-07-15', 3), -- Enseignant
('Salima', 'Touhami', 'salima.touhami@email.com', '0601234572', '128 Rue F', '2025-06-15', 3), -- Enseignant
('Rachid', 'El Mansouri', 'rachid.elmansouri@email.com', '0601234573', '129 Rue G', '2025-05-15', 2), -- Professionnel
('Amina', 'Zerouali', 'amina.zerouali@email.com', '0601234574', '130 Rue H', '2024-09-15', 2); -- Professionnel

---Abonnements
INSERT INTO adherent_abonnement (debut_abonnement, fin_abonnement, date_changement, numero_adherent) VALUES 
('2025-02-01', '2025-07-24', '2025-02-01', 1), -- ETU001 - OK
('2025-02-01', '2025-07-01', '2025-02-01', 2), -- ETU002 - KO
('2025-04-01', '2025-12-01', '2025-04-01', 3), -- ETU003 - OK
('2025-07-01', '2026-07-01', '2025-07-01', 4), -- ENS001 - OK
('2025-08-01', '2026-05-01', '2025-08-01', 5), -- ENS002 - KO
('2025-07-01', '2026-06-01', '2025-07-01', 6), -- ENS003 - OK
('2025-06-01', '2025-12-01', '2025-06-01', 7), -- PROF001 - OK
('2024-10-01', '2025-06-01', '2024-10-01', 8); -- PROF002 - KO

---Historique Statut Abonnement
-- Tous ont d'abord un statut "demande"
INSERT INTO historique_statut_abonnement (statut, date_changement, numero_adherent) VALUES 
('demande', '2025-02-01', 1),
('demande', '2025-02-01', 2),
('demande', '2025-04-01', 3),
('demande', '2025-07-01', 4),
('demande', '2025-08-01', 5),
('demande', '2025-07-01', 6),
('demande', '2025-06-01', 7),
('demande', '2024-10-01', 8);

-- Ceux avec un abonnement valide (OK) ont aussi un statut "valide"
INSERT INTO historique_statut_abonnement (statut, date_changement, numero_adherent) VALUES 
('valide', '2025-02-02', 1), -- ETU001 - OK
('valide', '2025-04-02', 3), -- ETU003 - OK
('valide', '2025-07-02', 4), -- ENS001 - OK
('valide', '2025-07-02', 6), -- ENS003 - OK
('valide', '2025-06-02', 7); -- PROF001 - OK

---Livres
INSERT INTO livre (titre, resume, date_Sortie, id_auteur) VALUES 
('Les Misérables', 'Roman de Victor Hugo publié en 1862, une fresque sociale de la France du XIXe siècle', '1862-01-01', 1), -- Victor Hugo
('Letranger', 'Roman dAlbert Camus publié en 1942, exploration de labsurdité de la condition humaine', '1942-01-01', 2), -- Albert Camus
('Harry Potter a lécole des sorciers', 'Premier tome de la saga Harry Potter, lhistoire d''un jeune sorcier', '1997-01-01', 3); -- J.K. Rowling

---Genres des Livres
INSERT INTO livre_genre (id_livre, id_genre) VALUES 
(1, 4), -- Les Misérables - Literature Classique
(2, 2), -- L'etranger - Philosophie
(3, 1), -- Harry Potter - Fantastique
(3, 3); -- Harry Potter - Jeunesse

---Exemplaires
INSERT INTO exemplaire (date_arrivee, id_livre) VALUES 
-- Les Misérables (3 exemplaires)
('2025-01-10', 1), -- MIS001
('2025-01-10', 1), -- MIS002
('2025-01-10', 1), -- MIS003
-- L'etranger (2 exemplaires)
('2025-01-15', 2), -- ETR001
('2025-01-15', 2), -- ETR002
-- Harry Potter (1 exemplaire)
('2025-01-20', 3); -- HAR001



