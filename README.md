# Système de réservation de salles

Application desktop JavaFX pour gérer la réservation de salles et équipements.

---

## Technologies

* **Java 17**
* **JavaFX 23**
* **Maven**
* **SQLite**

---

## Prérequis

* JDK 17+
* Maven 3.6+
* SQLite

---

## Installation et lancement

### 1. Cloner le projet

```bash
git clone https://github.com/tresor228/SystemeGestion.git
cd SystemeGestion
```

### 2. Initialiser la base de données

Avant de compiler le projet, créez et initialisez la base SQLite avec le schéma fourni :

```bash
sqlite3 reservation.db < schema.sql
```

> Cela créera le fichier `reservation.db` et toutes les tables nécessaires.

### 3. Compiler le projet

```bash
mvn clean compile
```

### 4. Lancer l'application

```bash
mvn javafx:run
```

---

## Base de données

* **SQLite** : fichier `reservation.db`
* Script d'initialisation : `schema.sql`

### Comptes par défaut

| Rôle         | Email                                     | Mot de passe |
| ------------ | ----------------------------------------- | ------------ |
| Utilisateur  | user@esgis.org  | password     |
| Gestionnaire | admin@esgis.org | admin        |

---

## Structure du projet

```
SystemeGestion/
├── src/main/java/com/reservation/salles/
│   ├── dao/          # Accès aux données
│   ├── model/        # Entités (Salle, Reservation, Utilisateur...)
│   ├── service/      # Logique métier
│   ├── ui/           # Interface JavaFX + contrôleurs
│   └── util/         # Utilitaires (DB, Notifications)
├── src/main/resources/
│   ├── fxml/         # Vues FXML
│   └── css/          # Styles
├── schema.sql        # Schéma base de données
├── reservation.db    # Base SQLite (générée)
└── pom.xml           # Configuration Maven
```

---

## Fonctionnalités principales

* **Utilisateur** :

  * Réserver des salles
  * Consulter ses réservations

* **Gestionnaire** :

  * Gérer les salles et équipements
  * Valider ou rejeter les demandes et réservations

---

## Réinitialiser la base de données

Pour réinitialiser complètement la base :

```bash
sqlite3 reservation.db < schema.sql
```