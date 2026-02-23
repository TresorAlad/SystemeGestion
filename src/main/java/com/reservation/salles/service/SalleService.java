package com.reservation.salles.service;

import com.reservation.salles.dao.SalleDAO;
import com.reservation.salles.model.Salle;

import java.util.List;

public class SalleService {

    private final SalleDAO salleDAO = new SalleDAO();

    public List<Salle> listerToutesLesSalles() {
        return salleDAO.findAll();
    }

    public Salle trouverParId(int id) {
        return salleDAO.findById(id);
    }

    public Salle ajouterSalle(Salle salle) {
        return salleDAO.save(salle);
    }

    public boolean modifierSalle(Salle salle) {
        return salleDAO.update(salle);
    }

    public boolean supprimerSalle(int id) {
        return salleDAO.delete(id);
    }

    public int compterDisponibles() {
        return salleDAO.countDisponibles();
    }
}
