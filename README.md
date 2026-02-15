# Système de réservation de salles

Application desktop JavaFX pour gérer la réservation de salles et équipements.

---

## Technologies

- **Java 17**
- **JavaFX 23**
- **Maven**
- **SQLite**

---

## Prérequis

- JDK 17+
- Maven 3.6+

---

## Installation et lancement

### 1. Cloner le projet

```bash
git clone https://github.com/tresor228/SystemeGestion.git
cd SystemeGestion
```

### 2. Compiler

```bash
mvn clean compile
```

### 3. Lancer l'application

```bash
mvn javafx:run
```

---

## Base de données

- **SQLite** : fichier `reservation.db` (créé automatiquement au premier lancement)
- Script d'initialisation : `schema.sql`

### Comptes par défaut

| Rôle       | Email           | Mot de passe |
|------------|-----------------|--------------|
| Utilisateur | user@example.com | password     |
| Gestionnaire | admin@example.com | admin        |

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

- **Utilisateur** : réserver des salles, consulter ses réservations
- **Gestionnaire** : gérer salles, équipements, valider/rejeter les demandes et réservations

---

## Réinitialiser la base de données

```bash
sqlite3 reservation.db < schema.sql
```
