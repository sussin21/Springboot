package tn.esprit.spring.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.domain.Maintenance;
import tn.esprit.spring.domain.Vehicule;
import tn.esprit.spring.repository.MaintenanceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.util.List;

@Service
public class MaintenanceService implements IMaintenance {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, VehiculeRepository vehiculeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public Maintenance creer(Maintenance maintenance) {
        maintenance.setIdMaintenance(null);
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance obtenirParId(long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance introuvable avec l'identifiant " + id));
    }

    @Override
    public List<Maintenance> listerTous() {
        return maintenanceRepository.findAll();
    }

    @Override
    public Maintenance modifier(long id, Maintenance maintenance) {
        Maintenance existante = obtenirParId(id);
        existante.setDateDebut(maintenance.getDateDebut());
        existante.setDateFin(maintenance.getDateFin());
        existante.setDescription(maintenance.getDescription());
        return maintenanceRepository.save(existante);
    }

    @Override
    public void supprimer(long id) {
        obtenirParId(id);
        maintenanceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Maintenance affecterMaintenanceVehicule(long idM, long idV) {
        Maintenance maintenance = maintenanceRepository.findById(idM).orElse(null);
        Vehicule vehicule = vehiculeRepository.findById(idV).orElse(null);

        if (maintenance == null || vehicule == null) {
            throw new EntityNotFoundException("Maintenance ou véhicule introuvable");
        }

        maintenance.setVehicule(vehicule);
        return maintenanceRepository.save(maintenance);
    }
}
