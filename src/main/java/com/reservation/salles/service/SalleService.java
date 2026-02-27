package com.reservation.salles.service;

import com.reservation.salles.dao.SalleDAO;
import com.reservation.salles.model.Salle;

import java.util.List;

/**
 * Service pour la gestion des salles et de leurs disponibilités.
 */
public class SalleService {

    private final SalleDAO salleDAO = new SalleDAO();

    /**
     * Récupère la liste de toutes les salles présentes en base.
     */
    public List<Salle> listerToutesLesSalles() {
        return salleDAO.findAll();
    }

    /**
     * Recherche une salle par son ID.
     */
    public Salle trouverParId(int id) {
        return salleDAO.findById(id);
    }

    /**
     * Ajoute une nouvelle salle au système.
     */
    public Salle ajouterSalle(Salle salle) {
        return salleDAO.save(salle);
    }

    /**
     * Modifie les informations d'une salle existante.
     */
    public boolean modifierSalle(Salle salle) {
        return salleDAO.update(salle);
    }

    /**
     * Supprime une salle par son ID.
     */
    public boolean supprimerSalle(int id) {
        return salleDAO.delete(id);
    }

    /**
     * Enregistre les équipements liés à une salle.
     */
    public void lierEquipementsASalle(int idSalle, List<com.reservation.salles.model.Equipement> equipements) {
        salleDAO.saveEquipements(idSalle, equipements);
    }

    /**
     * Calcule le nombre de salles actuellement disponibles (non réservées et en
     * statut disponible).
     */
    public int compterDisponibles() {
        int totalDispos = salleDAO.countDisponibles();
        int occupees = 0;
        for (Salle s : salleDAO.findAll()) {
            if (s.isDisponible() && estOccupeeMaintenant(s.getIdSalle())) {
                occupees++;
            }
        }
        return Math.max(0, totalDispos - occupees);
    }

    /**
     * Vérifie si une salle est occupée en temps réel.
     */
    public boolean estOccupeeMaintenant(int idSalle) {
        return new com.reservation.salles.dao.ReservationDAO().isSalleOccupeeMaintenant(idSalle);
    }
}
