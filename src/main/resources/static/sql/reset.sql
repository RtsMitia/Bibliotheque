\c postgres
drop database bibliotheque;
CREATE DATABASE bibliotheque;
\c bibliotheque;

-- Drop tables in reverse order of dependency (child tables first, parent tables last)
DROP TABLE IF EXISTS livre_genre CASCADE;
DROP TABLE IF EXISTS contraint CASCADE;
DROP TABLE IF EXISTS statut_prolongement CASCADE;
DROP TABLE IF EXISTS statut_reservation CASCADE;
DROP TABLE IF EXISTS penalite CASCADE;
DROP TABLE IF EXISTS paiement_cotisation CASCADE;
DROP TABLE IF EXISTS prolongement CASCADE;
DROP TABLE IF EXISTS pret CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS adherent_abonnement CASCADE;
DROP TABLE IF EXISTS exemplaire CASCADE;
DROP TABLE IF EXISTS livre CASCADE;
DROP TABLE IF EXISTS quota_reservation CASCADE;
DROP TABLE IF EXISTS quota_pret CASCADE;
DROP TABLE IF EXISTS penalite_config CASCADE;
DROP TABLE IF EXISTS cotisation CASCADE;
DROP TABLE IF EXISTS adherents CASCADE;
DROP TABLE IF EXISTS type_pret CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS auteur CASCADE;
DROP TABLE IF EXISTS type_adherent CASCADE;

-- Drop the sequence if it exists
DROP SEQUENCE IF EXISTS sequence_adherents CASCADE;

-- Drop the trigger function if it exists
DROP FUNCTION IF EXISTS generer_numero_adherent() CASCADE;