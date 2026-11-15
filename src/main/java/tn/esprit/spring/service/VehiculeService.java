package tn.esprit.spring.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.domain.Agence;
import tn.esprit.spring.domain.Equipement;
import tn.esprit.spring.domain.StatutVehicule;
import tn.esprit.spring.domain.Vehicule;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.EquipementRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VehiculeService implements IVehicule {

    private final VehiculeRepository vehiculeRepository;
    private final EquipementRepository equipementRepository;
    private final AgenceRepository agenceRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository, EquipementRepository equipementRepository,
                           AgenceRepository agenceRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.equipementRepository = equipementRepository;
        this.agenceRepository = agenceRepository;
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

    // 1. Véhicules d'une marque
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarque(String marque) {
        return vehiculeRepository.findByMarque(marque);
    }

    // 2. Marque contenant un mot
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueContenant(String mot) {
        return vehiculeRepository.findByMarqueContaining(mot);
    }

    // 3. Marque commençant par une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueCommencantPar(String valeur) {
        return vehiculeRepository.findByMarqueStartingWith(valeur);
    }

    // 4. Marque se terminant par une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueSeTerminantPar(String valeur) {
        return vehiculeRepository.findByMarqueEndingWith(valeur);
    }

    // 5. Marque sans tenir compte de la casse
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueSansCasse(String marque) {
        return vehiculeRepository.findByMarqueIgnoreCase(marque);
    }

    // 6. Véhicules par modèle
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParModele(String modele) {
        return vehiculeRepository.findByModele(modele);
    }

    // 7. Existence d'une immatriculation
    @Override
    @Transactional(readOnly = true)
    public boolean existeParImmatriculation(String immatriculation) {
        return vehiculeRepository.existsByImmatriculation(immatriculation);
    }

    // 8. Compter les véhicules d'une marque
    @Override
    @Transactional(readOnly = true)
    public long compterParMarque(String marque) {
        return vehiculeRepository.countByMarque(marque);
    }

    // 9. Tarif supérieur à une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParTarifSuperieur(BigDecimal valeur) {
        return vehiculeRepository.findByTarifJournalierGreaterThan(valeur);
    }

    // 10. Tarif inférieur à une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParTarifInferieur(BigDecimal valeur) {
        return vehiculeRepository.findByTarifJournalierLessThan(valeur);
    }

    // 11. Tarif supérieur ou égal à une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParTarifSuperieurOuEgal(BigDecimal valeur) {
        return vehiculeRepository.findByTarifJournalierGreaterThanEqual(valeur);
    }

    // 12. Tarif entre deux bornes incluses
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParTarifEntre(BigDecimal min, BigDecimal max) {
        return vehiculeRepository.findByTarifJournalierBetween(min, max);
    }

    // 13. Tri par tarif croissant
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> trierParTarifCroissant() {
        return vehiculeRepository.findAllByOrderByTarifJournalierAsc();
    }

    // 14. Tri par tarif décroissant
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> trierParTarifDecroissant() {
        return vehiculeRepository.findAllByOrderByTarifJournalierDesc();
    }

    // 15. Véhicules d'une marque par tarif croissant
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueEtTrierParTarifCroissant(String marque) {
        return vehiculeRepository.findByMarqueOrderByTarifJournalierAsc(marque);
    }

    // 16. Marque et tarif supérieur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueEtTarifSuperieur(String marque, BigDecimal tarif) {
        return vehiculeRepository.findByMarqueAndTarifJournalierGreaterThan(marque, tarif);
    }

    // 17. Marque ou modèle
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarqueOuModele(String marque, String modele) {
        return vehiculeRepository.findByMarqueOrModele(marque, modele);
    }

    // 18. Marque dans une liste
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParMarques(List<String> marques) {
        return vehiculeRepository.findByMarqueIn(marques);
    }

    // 19. Marque hors d'une liste
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherHorsMarques(List<String> marques) {
        return vehiculeRepository.findByMarqueNotIn(marques);
    }

    // 20. Véhicules par statut
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParStatut(StatutVehicule statut) {
        return vehiculeRepository.findByStatut(statut);
    }

    // 21. Véhicules disponibles par tarif croissant
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherDisponiblesParTarifCroissant() {
        return vehiculeRepository.findByStatutOrderByTarifJournalierAsc(StatutVehicule.DISPONIBLE);
    }

    // 22. Véhicules d'une agence
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParAgence(long idAgence) {
        return vehiculeRepository.findByAgence_IdAgence(idAgence);
    }

    // 23. Véhicules d'une agence dans une ville
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParVilleAgence(String ville) {
        return vehiculeRepository.findByAgence_Ville(ville);
    }

    // 24. Nom d'agence contenant une valeur
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParNomAgenceContenant(String mot) {
        return vehiculeRepository.findByAgence_NomContaining(mot);
    }

    // 25. Véhicules d'une agence et d'une marque
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherParAgenceEtMarque(long idAgence, String marque) {
        return vehiculeRepository.findByAgence_IdAgenceAndMarque(idAgence, marque);
    }

    // 26. Véhicules sans agence
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherSansAgence() {
        return vehiculeRepository.findByAgenceIsNull();
    }

    // 27. Véhicules affectés à une agence
    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> rechercherAvecAgence() {
        return vehiculeRepository.findByAgenceIsNotNull();
    }

    // 28. Compter les véhicules d'une agence
    @Override
    @Transactional(readOnly = true)
    public long compterParAgence(long idAgence) {
        return vehiculeRepository.countByAgence_IdAgence(idAgence);
    }

    // 29. Vérifier si une agence possède des véhicules
    @Override
    @Transactional(readOnly = true)
    public boolean agencePossedeVehicules(long idAgence) {
        return vehiculeRepository.existsByAgence_IdAgence(idAgence);
    }

    @Override
    @Transactional
    public Vehicule affecterAgenceVehicule(long idV, long idAgence) {
        Vehicule vehicule = vehiculeRepository.findById(idV).orElse(null);
        Agence agence = agenceRepository.findById(idAgence).orElse(null);
        if (vehicule == null || agence == null) {
            throw new EntityNotFoundException("Véhicule ou agence introuvable");
        }
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

}
