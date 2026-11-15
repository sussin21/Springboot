package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.domain.StatutVehicule;
import tn.esprit.spring.domain.Vehicule;

import java.math.BigDecimal;
import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // Méthodes dérivées Spring Data JPA : aucune requête SQL/JPQL manuelle.

    // 1. Véhicules d'une marque
    List<Vehicule> findByMarque(String marque);

    // 2. Marque contenant un mot
    List<Vehicule> findByMarqueContaining(String mot);

    // 3. Marque commençant par une valeur
    List<Vehicule> findByMarqueStartingWith(String valeur);

    // 4. Marque se terminant par une valeur
    List<Vehicule> findByMarqueEndingWith(String valeur);

    // 5. Marque sans tenir compte de la casse
    List<Vehicule> findByMarqueIgnoreCase(String marque);

    // 6. Véhicules par modèle
    List<Vehicule> findByModele(String modele);

    // 7. Existence d'une immatriculation
    boolean existsByImmatriculation(String immatriculation);

    // 8. Compter les véhicules d'une marque
    long countByMarque(String marque);

    // 9. Tarif supérieur à une valeur
    List<Vehicule> findByTarifJournalierGreaterThan(BigDecimal valeur);

    // 10. Tarif inférieur à une valeur
    List<Vehicule> findByTarifJournalierLessThan(BigDecimal valeur);

    // 11. Tarif supérieur ou égal à une valeur
    List<Vehicule> findByTarifJournalierGreaterThanEqual(BigDecimal valeur);

    // 12. Tarif entre deux bornes incluses
    List<Vehicule> findByTarifJournalierBetween(BigDecimal min, BigDecimal max);

    // 13. Tri par tarif croissant
    List<Vehicule> findAllByOrderByTarifJournalierAsc();

    // 14. Tri par tarif décroissant
    List<Vehicule> findAllByOrderByTarifJournalierDesc();

    // 15. Véhicules d'une marque par tarif croissant
    List<Vehicule> findByMarqueOrderByTarifJournalierAsc(String marque);

    // 16. Marque et tarif supérieur
    List<Vehicule> findByMarqueAndTarifJournalierGreaterThan(String marque, BigDecimal tarif);

    // 17. Marque ou modèle
    List<Vehicule> findByMarqueOrModele(String marque, String modele);

    // 18. Marque dans une liste
    List<Vehicule> findByMarqueIn(List<String> marques);

    // 19. Marque hors d'une liste
    List<Vehicule> findByMarqueNotIn(List<String> marques);

    // 20. Véhicules par statut
    List<Vehicule> findByStatut(StatutVehicule statut);

    // 21. Véhicules disponibles par tarif croissant
    List<Vehicule> findByStatutOrderByTarifJournalierAsc(StatutVehicule statut);

    // 22. Véhicules d'une agence
    List<Vehicule> findByAgence_IdAgence(long idAgence);

    // 23. Véhicules d'une agence dans une ville
    List<Vehicule> findByAgence_Ville(String ville);

    // 24. Nom d'agence contenant une valeur
    List<Vehicule> findByAgence_NomContaining(String mot);

    // 25. Véhicules d'une agence et d'une marque
    List<Vehicule> findByAgence_IdAgenceAndMarque(long idAgence, String marque);

    // 26. Véhicules sans agence
    List<Vehicule> findByAgenceIsNull();

    // 27. Véhicules affectés à une agence
    List<Vehicule> findByAgenceIsNotNull();

    // 28. Compter les véhicules d'une agence
    long countByAgence_IdAgence(long idAgence);

    // 29. Vérifier si une agence possède des véhicules
    boolean existsByAgence_IdAgence(long idAgence);

}
