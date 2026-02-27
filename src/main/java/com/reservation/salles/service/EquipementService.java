package com.reservation.salles.service;

import com.reservation.salles.dao.EquipementDAO;
import com.reservation.salles.model.Equipement;

import java.util.List;

/**
 * Service pour la gestion du catalogue d'équipements.
 */
public class EquipementService {

    private final EquipementDAO equipementDAO = new EquipementDAO();

    /**
     * Liste tous les équipements disponibles pour l'affectation aux salles.
     */
    public List<Equipement> listerEquipements() {
        return equipementDAO.findAll();
    }

    /**
     * Enregistre une liste d'équipements dans la base de données.
     */
    public void enregistrerEquipements(List<Equipement> equipements) {
        for (Equipement e : equipements) {
            equipementDAO.save(e);
        }
    }
}
