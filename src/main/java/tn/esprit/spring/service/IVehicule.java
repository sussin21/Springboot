package tn.esprit.spring.service;

import tn.esprit.spring.domain.Vehicule;

import java.util.List;

public interface IVehicule {

    Vehicule creer(Vehicule vehicule);

    Vehicule obtenirParId(long id);

    List<Vehicule> listerTous();

    Vehicule modifier(long id, Vehicule vehicule);

    void supprimer(long id);

    Vehicule affecterEquipementVehicule(long idV, long idEq);
}
