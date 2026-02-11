package com.reservation.salles.service;

import com.reservation.salles.dao.EquipementDAO;
import com.reservation.salles.model.Equipement;

import java.util.List;

public class EquipementService {

    private final EquipementDAO equipementDAO = new EquipementDAO();

    public List<Equipement> listerEquipements() {
        return equipementDAO.findAll();
    }

    public void enregistrerEquipements(List<Equipement> equipements) {
        for (Equipement e : equipements) {
            equipementDAO.save(e);
        }
    }
}

