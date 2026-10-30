package tn.esprit.spring.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.domain.Equipement;
import tn.esprit.spring.domain.Vehicule;
import tn.esprit.spring.repository.VehiculeRepository;
import tn.esprit.spring.repository.EquipementRepository;

import java.util.List;

@Service
public class VehiculeService implements IVehicule {

    private final VehiculeRepository vehiculeRepository;
    private final EquipementRepository equipementRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository, EquipementRepository equipementRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.equipementRepository = equipementRepository;
    }

    @Override
    public Vehicule creer(Vehicule vehicule) {
        vehicule.setIdVehicule(null);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule obtenirParId(long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicule introuvable avec l'identifiant " + id));
    }

    @Override
    public List<Vehicule> listerTous() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule modifier(long id, Vehicule vehicule) {
        Vehicule existant = obtenirParId(id);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        return vehiculeRepository.save(existant);
    }

    @Override
    public void supprimer(long id) {
        obtenirParId(id);
        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Vehicule affecterEquipementVehicule(long idV, long idEq) {
        Vehicule vehicule = vehiculeRepository.findById(idV).orElse(null);
        Equipement equipement = equipementRepository.findById(idEq).orElse(null);

        if (vehicule == null || equipement == null) {
            throw new EntityNotFoundException("Véhicule ou équipement introuvable");
        }

        vehicule.getEquipements().add(equipement);
        return vehiculeRepository.save(vehicule);
    }
}
