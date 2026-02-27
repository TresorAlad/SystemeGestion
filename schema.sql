PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS salle_equipements;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS demandes;
DROP TABLE IF EXISTS equipements;
DROP TABLE IF EXISTS salles;
DROP TABLE IF EXISTS utilisateurs;

CREATE TABLE utilisateurs (
    id_utilisateur   INTEGER PRIMARY KEY AUTOINCREMENT,
    nom              TEXT NOT NULL,
    email            TEXT NOT NULL UNIQUE,
    mot_de_passe     TEXT NOT NULL,
    role             TEXT NOT NULL CHECK (role IN ('UTILISATEUR','GESTIONNAIRE'))
);

CREATE TABLE salles (
    id_salle    INTEGER PRIMARY KEY AUTOINCREMENT,
    nom         TEXT NOT NULL,
    type        TEXT NOT NULL,
    capacite    INTEGER NOT NULL,
    disponible  INTEGER NOT NULL DEFAULT 1,
    photo       TEXT
);

CREATE TABLE equipements (
    id_equipement INTEGER PRIMARY KEY AUTOINCREMENT,
    nom           TEXT NOT NULL,
    quantite      INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE salle_equipements (
    id_salle      INTEGER NOT NULL,
    id_equipement INTEGER NOT NULL,
    quantite      INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (id_salle, id_equipement),
    FOREIGN KEY (id_salle) REFERENCES salles(id_salle) ON DELETE CASCADE,
    FOREIGN KEY (id_equipement) REFERENCES equipements(id_equipement) ON DELETE CASCADE
);

CREATE TABLE reservations (
    id_reservation INTEGER PRIMARY KEY AUTOINCREMENT,
    id_utilisateur INTEGER NOT NULL,
    id_salle       INTEGER NOT NULL,
    date           TEXT NOT NULL,
    heure_debut    TEXT NOT NULL,
    heure_fin      TEXT NOT NULL,
    statut         TEXT NOT NULL CHECK (statut IN ('EN_ATTENTE','VALIDEE','REJETEE','ANNULEE')),
    nom_reservataire TEXT,
    telephone      TEXT,
    objet          TEXT,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur) ON DELETE CASCADE,
    FOREIGN KEY (id_salle)       REFERENCES salles(id_salle)       ON DELETE CASCADE
);

CREATE TABLE demandes (
    id_demande     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_reservation INTEGER NOT NULL,
    type_demande   TEXT NOT NULL,
    date_demande   TEXT NOT NULL,
    statut         TEXT NOT NULL CHECK (statut IN ('EN_ATTENTE','VALIDEE','REJETEE')),
    FOREIGN KEY (id_reservation) REFERENCES reservations(id_reservation) ON DELETE CASCADE
);

INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES
('Utilisateur Test', 'user@esgis.org', 'password', 'UTILISATEUR'),
('Gestionnaire ', 'admin@esgis.org', 'admin', 'GESTIONNAIRE');

INSERT INTO salles (nom, type, capacite, disponible, photo) VALUES
('Salle 0-1', 'Cours', 10, 1, 'jav.jpg'),
('Salle 0-2', 'Cours', 30, 1, 'jav.jpg'),
('Salle 1-0', 'Conférence', 100, 1, 'jav.jpg'),
('Salle 0-4', 'Réunion', 10, 1, 'jav.jpg'),
('Salle 2-1', 'Cours', 40, 1, 'jav.jpg'),
('Salle 2-3', 'Conférence', 120, 1, 'jav.jpg');

INSERT INTO equipements (nom, quantite) VALUES
('Projecteur', 1),
('WiFi', 2),
('Climatiseur', 5),
('Tableau', 3),
('Ecran', 3);

INSERT INTO salle_equipements (id_salle, id_equipement, quantite) VALUES
(1, 1, 2), (1, 2, 3), (2, 2, 1), (2, 3, 2), (3, 1, 3), (3, 2, 1), (3, 3, 2);
