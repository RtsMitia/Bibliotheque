\c postgres
drop database bibliotheque;
CREATE DATABASE bibliotheque;
\c bibliotheque;

-- TABLE : TypeAdherent
CREATE TABLE type_adherent (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- Creation de la sequence pour l'ID des adherents (commence à 1)
CREATE SEQUENCE sequence_adherents START 1;

-- Table adherents
CREATE TABLE adherents (
    id INT PRIMARY KEY DEFAULT nextval('sequence_adherents'),
    numero_adherent VARCHAR(10) UNIQUE NOT NULL, -- Exemple : MBR001
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,  
    email VARCHAR(255) UNIQUE NOT NULL,
    id_type_adherent INT NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    date_inscription TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_adherents_type_adherent
        FOREIGN KEY (id_type_adherent)
        REFERENCES type_adherent(id)
        ON DELETE CASCADE
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


-- TABLE : Cotisation
CREATE TABLE cotisation (
    id SERIAL PRIMARY KEY,
    id_type_adherent INT NOT NULL,
    montant NUMERIC(8,2) NOT NULL CHECK (montant > 0),
    CONSTRAINT fk_cotisation_type_adherent
        FOREIGN KEY (id_type_adherent)
        REFERENCES type_adherent(id)
        ON DELETE CASCADE
);

-- TABLE : Adherent_Abonnement
CREATE TABLE adherent_abonnement (
    id SERIAL PRIMARY KEY,
    id_adherent INT NOT NULL,
    debut_abonnement DATE NOT NULL,
    fin_abonnement DATE NOT NULL,
    date_changement TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_abonnement_adherent
        FOREIGN KEY (id_adherent)
        REFERENCES adherents(id)
        ON DELETE CASCADE
);
