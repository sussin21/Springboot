package tn.esprit.spring.service;

import tn.esprit.spring.domain.Maintenance;

import java.util.List;

public interface IMaintenance {

    Maintenance creer(Maintenance maintenance);

    Maintenance obtenirParId(long id);

    List<Maintenance> listerTous();

    Maintenance modifier(long id, Maintenance maintenance);

    void supprimer(long id);

    Maintenance affecterMaintenanceVehicule(long idM, long idV);
}
