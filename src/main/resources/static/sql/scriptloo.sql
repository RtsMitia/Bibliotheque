CREATE TABLE type_adherent(
   id SERIAL,
   libelle VARCHAR(100),
   PRIMARY KEY(id)
);

CREATE TABLE cotisation(
   id SERIAL,
   montant DECIMAL(15,2),
   date_ DATE,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

CREATE TABLE auteur(
   id SERIAL,
   nom VARCHAR(255),
   PRIMARY KEY(id)
);

CREATE TABLE genre(
   id SERIAL,
   libelle VARCHAR(255),
   PRIMARY KEY(id)
);

CREATE TABLE type_pret(
   id SERIAL,
   libelle VARCHAR(50),
   PRIMARY KEY(id)
);

CREATE TABLE quota_pret(
   id SERIAL,
   nombre_livre INT,
   nombre_jour_pret INT NOT NULL,
   date_changement DATE,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

CREATE TABLE quota_reservation(
   id SERIAL,
   nombre_livre INT,
   date_changement DATE,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

CREATE TABLE penalite_config(
   id SERIAL,
   nombre_jour INT NOT NULL,
   date_changement DATE,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

CREATE SEQUENCE sequence_adherents START 1;
CREATE TABLE adherents(
   id INT DEFAULT nextval('sequence_adherents'),
   numero_adherent VARCHAR(50) UNIQUE NOT NULL,
   prenom VARCHAR(255) NOT NULL,
   nom VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL ,
   telephone VARCHAR(50),
   adresse VARCHAR(50),
   date_inscription DATE NOT NULL,
   id_type_adherent INT NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(email),
   FOREIGN KEY(id_type_adherent) REFERENCES type_adherent(id)
);

-- Fonction trigger pour generer le numero d'adherent
CREATE OR REPLACE FUNCTION generer_numero_adherent()
RETURNS TRIGGER AS $$
BEGIN
    -- Si le numero d'adherent n'est pas renseigne, on le genère
    IF NEW.numero_adherent IS NULL OR NEW.numero_adherent = '' THEN
        NEW.numero_adherent := 'MBR' || LPAD(NEW.id::text, 3, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger avant insertion qui appelle la fonction
CREATE TRIGGER trg_generer_numero_adherent
BEFORE INSERT ON adherents
FOR EACH ROW
EXECUTE FUNCTION generer_numero_adherent();

CREATE TABLE adherent_abonnement(
   id SERIAL,
   debut_abonnement DATE NOT NULL,
   fin_abonnement DATE NOT NULL,
   date_changement DATE NOT NULL,
   numero_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id)
);

CREATE TABLE historique_statut_abonnement(
   id SERIAL,
   statut VARCHAR(20) CHECK (statut IN ('demande', 'en attente', 'valide', 'refuse')) NOT NULL,
   date_changement DATE NOT NULL,
   numero_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id)
);

CREATE TABLE livre(
   id SERIAL,
   titre VARCHAR(255),
   resume VARCHAR(255),
   date_Sortie DATE,
   id_auteur INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_auteur) REFERENCES auteur(id)
);

CREATE TABLE exemplaire(
   id SERIAL,
   date_arrivee DATE,
   id_livre INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_livre) REFERENCES livre(id)
);

CREATE TABLE reservation(
   id SERIAL,
   date_reservation DATE,
   date_debut_pret DATE NOT NULL,
   id_exemplaire INT NOT NULL,
   numero_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_exemplaire) REFERENCES exemplaire(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id)
);

CREATE TABLE pret(
   id SERIAL,
   date_debut DATE,
   date_fin DATE,
   date_retour DATE,
   numero_adherent INT NOT NULL,
   id_type_pret INT NOT NULL,
   id_exemplaire INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id),
   FOREIGN KEY(id_type_pret) REFERENCES type_pret(id),
   FOREIGN KEY(id_exemplaire) REFERENCES exemplaire(id)
);

CREATE TABLE prolongement(
   id SERIAL,
   date_plg DATE,
   nouvelle_date_retour DATE,
   id_pret INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_pret) REFERENCES pret(id)
);

CREATE TABLE paiement_cotisation(
   id SERIAL,
   montant_paye DECIMAL(15,2),
   date_paiement DATE,
   numero_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id)
);

CREATE TABLE penalite(
   id SERIAL,
   date_debut DATE NOT NULL,
   date_fin DATE NOT NULL,
   numero_adherent INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id)
);

CREATE TABLE statut_reservation(
   id SERIAL,
   statut VARCHAR(50) NOT NULL,
   date_changement DATE NOT NULL,
   id_reservation INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_reservation) REFERENCES reservation(id)
);

CREATE TABLE statut_prolongement(
   id SERIAL,
   statut VARCHAR(50) NOT NULL CHECK (statut IN ('demande', 'en attente', 'valide', 'refuse')),
   date_changement DATE NOT NULL,
   id_prolongement INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_prolongement) REFERENCES prolongement(id)
);

CREATE TABLE les_contraint(
   id SERIAL,
   type_contrainte VARCHAR(50) NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE livre_genre(
   id SERIAL,
   id_livre INT,
   id_genre INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_livre) REFERENCES livre(id),
   FOREIGN KEY(id_genre) REFERENCES genre(id)
);

CREATE TABLE jour_ferie(
   id SERIAL,
   date_debut DATE NOT NULL,
   date_fin DATE,
   date_creation DATE,
   PRIMARY KEY(id)
);

CREATE TABLE livre_contraint(
   id_livre SERIAL,
   id_contraint int,
   PRIMARY KEY(id_livre, id_contraint),
   FOREIGN KEY(id_livre) REFERENCES livre(id),
   FOREIGN KEY(id_contraint) REFERENCES les_contraint(id)
);

CREATE TABLE contraint_adherant(
   id SERIAL,
   numero_adherent int,
   id_contraint int,
   PRIMARY KEY(id),
   FOREIGN KEY(numero_adherent) REFERENCES adherents(id),
   FOREIGN KEY(id_contraint) REFERENCES les_contraint(id)
);
