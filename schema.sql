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
('Gestionnaire Test', 'admin@esgis.org', 'admin', 'GESTIONNAIRE');

INSERT INTO salles (nom, type, capacite, disponible, photo) VALUES
('Salle A', 'Réunion', 10, 1, 'jav.jpg'),
('Salle B', 'Cours', 30, 1, 'jav.jpg'),
('Salle C', 'Conférence', 100, 1, 'jav.jpg');

INSERT INTO equipements (nom, quantite) VALUES
('Projecteur', 2),
('WiFi', 5),
('Tableau blanc', 3);

INSERT INTO salle_equipements (id_salle, id_equipement) VALUES
(1,1),(1,2),(2,2),(2,3),(3,1),(3,2),(3,3);

