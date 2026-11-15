package tn.esprit.spring.service;

import tn.esprit.spring.domain.StatutVehicule;
import tn.esprit.spring.domain.Vehicule;

import java.math.BigDecimal;
import java.util.List;

public interface IVehicule {

    Vehicule creer(Vehicule vehicule);

    Vehicule obtenirParId(long id);

    List<Vehicule> listerTous();

    Vehicule modifier(long id, Vehicule vehicule);

    void supprimer(long id);

    Vehicule affecterEquipementVehicule(long idV, long idEq);

    // 1. Véhicules d'une marque
    List<Vehicule> rechercherParMarque(String marque);

    // 2. Marque contenant un mot
    List<Vehicule> rechercherParMarqueContenant(String mot);

    // 3. Marque commençant par une valeur
    List<Vehicule> rechercherParMarqueCommencantPar(String valeur);

    // 4. Marque se terminant par une valeur
    List<Vehicule> rechercherParMarqueSeTerminantPar(String valeur);

    // 5. Marque sans tenir compte de la casse
    List<Vehicule> rechercherParMarqueSansCasse(String marque);

    // 6. Véhicules par modèle
    List<Vehicule> rechercherParModele(String modele);

    // 7. Existence d'une immatriculation
    boolean existeParImmatriculation(String immatriculation);

    // 8. Compter les véhicules d'une marque
    long compterParMarque(String marque);

    // 9. Tarif supérieur à une valeur
    List<Vehicule> rechercherParTarifSuperieur(BigDecimal valeur);

    // 10. Tarif inférieur à une valeur
    List<Vehicule> rechercherParTarifInferieur(BigDecimal valeur);

    // 11. Tarif supérieur ou égal à une valeur
    List<Vehicule> rechercherParTarifSuperieurOuEgal(BigDecimal valeur);

    // 12. Tarif entre deux bornes incluses
    List<Vehicule> rechercherParTarifEntre(BigDecimal min, BigDecimal max);

    // 13. Tri par tarif croissant
    List<Vehicule> trierParTarifCroissant();

    // 14. Tri par tarif décroissant
    List<Vehicule> trierParTarifDecroissant();

    // 15. Véhicules d'une marque par tarif croissant
    List<Vehicule> rechercherParMarqueEtTrierParTarifCroissant(String marque);

    // 16. Marque et tarif supérieur
    List<Vehicule> rechercherParMarqueEtTarifSuperieur(String marque, BigDecimal tarif);

    // 17. Marque ou modèle
    List<Vehicule> rechercherParMarqueOuModele(String marque, String modele);

    // 18. Marque dans une liste
    List<Vehicule> rechercherParMarques(List<String> marques);

    // 19. Marque hors d'une liste
    List<Vehicule> rechercherHorsMarques(List<String> marques);

    // 20. Véhicules par statut
    List<Vehicule> rechercherParStatut(StatutVehicule statut);

    // 21. Véhicules disponibles par tarif croissant
    List<Vehicule> rechercherDisponiblesParTarifCroissant();

    // 22. Véhicules d'une agence
    List<Vehicule> rechercherParAgence(long idAgence);

    // 23. Véhicules d'une agence dans une ville
    List<Vehicule> rechercherParVilleAgence(String ville);

    // 24. Nom d'agence contenant une valeur
    List<Vehicule> rechercherParNomAgenceContenant(String mot);

    // 25. Véhicules d'une agence et d'une marque
    List<Vehicule> rechercherParAgenceEtMarque(long idAgence, String marque);

    // 26. Véhicules sans agence
    List<Vehicule> rechercherSansAgence();

    // 27. Véhicules affectés à une agence
    List<Vehicule> rechercherAvecAgence();

    // 28. Compter les véhicules d'une agence
    long compterParAgence(long idAgence);

    // 29. Vérifier si une agence possède des véhicules
    boolean agencePossedeVehicules(long idAgence);

    Vehicule affecterAgenceVehicule(long idV, long idAgence);

}
