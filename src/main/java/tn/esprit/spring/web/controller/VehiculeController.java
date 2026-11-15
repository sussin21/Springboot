package tn.esprit.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.domain.StatutVehicule;
import tn.esprit.spring.domain.Vehicule;
import tn.esprit.spring.service.IVehicule;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
@Tag(name = "Véhicules", description = "Gestion de la flotte de véhicules")
public class VehiculeController {

    private final IVehicule vehiculeService;

    public VehiculeController(IVehicule vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    @Operation(summary = "Créer un véhicule")
    public ResponseEntity<Vehicule> creer(@RequestBody Vehicule vehicule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.creer(vehicule));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chercher un véhicule par identifiant")
    public Vehicule obtenirParId(@PathVariable long id) {
        return vehiculeService.obtenirParId(id);
    }

    @GetMapping("/listall")
    @Operation(summary = "Lister les véhicules")
    public List<Vehicule> listerTous() {
        return vehiculeService.listerTous();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule")
    public ResponseEntity<Vehicule> modifier(@PathVariable long id, @RequestBody Vehicule vehicule) {
        return ResponseEntity.ok(vehiculeService.modifier(id, vehicule));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule")
    public ResponseEntity<Void> supprimer(@PathVariable long id) {
        vehiculeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idV}/equipements/{idEq}")
    @Operation(summary = "Affecter un équipement à un véhicule",
            description = "Ajoute un équipement existant au véhicule. Une affectation répétée ne crée pas de doublon. Aucun corps JSON requis.")
    public Vehicule affecterEquipementVehicule(@PathVariable long idV, @PathVariable long idEq) {
        return vehiculeService.affecterEquipementVehicule(idV, idEq);
    }

    @GetMapping("/recherche/marque")
    @Operation(summary = "1. Véhicules d'une marque")
    public List<Vehicule> rechercherParMarque(@RequestParam("marque") String marque) {
        return vehiculeService.rechercherParMarque(marque);
    }

    @GetMapping("/recherche/marque/contient")
    @Operation(summary = "2. Marque contenant un mot")
    public List<Vehicule> rechercherParMarqueContenant(@RequestParam("mot") String mot) {
        return vehiculeService.rechercherParMarqueContenant(mot);
    }

    @GetMapping("/recherche/marque/commence-par")
    @Operation(summary = "3. Marque commençant par une valeur")
    public List<Vehicule> rechercherParMarqueCommencantPar(@RequestParam("valeur") String valeur) {
        return vehiculeService.rechercherParMarqueCommencantPar(valeur);
    }

    @GetMapping("/recherche/marque/termine-par")
    @Operation(summary = "4. Marque se terminant par une valeur")
    public List<Vehicule> rechercherParMarqueSeTerminantPar(@RequestParam("valeur") String valeur) {
        return vehiculeService.rechercherParMarqueSeTerminantPar(valeur);
    }

    @GetMapping("/recherche/marque/ignore-casse")
    @Operation(summary = "5. Marque sans tenir compte de la casse")
    public List<Vehicule> rechercherParMarqueSansCasse(@RequestParam("marque") String marque) {
        return vehiculeService.rechercherParMarqueSansCasse(marque);
    }

    @GetMapping("/recherche/modele")
    @Operation(summary = "6. Véhicules par modèle")
    public List<Vehicule> rechercherParModele(@RequestParam("modele") String modele) {
        return vehiculeService.rechercherParModele(modele);
    }

    @GetMapping("/recherche/immatriculation/existe")
    @Operation(summary = "7. Existence d'une immatriculation")
    public boolean existeParImmatriculation(@RequestParam("immatriculation") String immatriculation) {
        return vehiculeService.existeParImmatriculation(immatriculation);
    }

    @GetMapping("/recherche/marque/compter")
    @Operation(summary = "8. Compter les véhicules d'une marque")
    public long compterParMarque(@RequestParam("marque") String marque) {
        return vehiculeService.compterParMarque(marque);
    }

    @GetMapping("/recherche/tarif/superieur")
    @Operation(summary = "9. Tarif supérieur à une valeur")
    public List<Vehicule> rechercherParTarifSuperieur(@RequestParam("valeur") BigDecimal valeur) {
        return vehiculeService.rechercherParTarifSuperieur(valeur);
    }

    @GetMapping("/recherche/tarif/inferieur")
    @Operation(summary = "10. Tarif inférieur à une valeur")
    public List<Vehicule> rechercherParTarifInferieur(@RequestParam("valeur") BigDecimal valeur) {
        return vehiculeService.rechercherParTarifInferieur(valeur);
    }

    @GetMapping("/recherche/tarif/superieur-ou-egal")
    @Operation(summary = "11. Tarif supérieur ou égal à une valeur")
    public List<Vehicule> rechercherParTarifSuperieurOuEgal(@RequestParam("valeur") BigDecimal valeur) {
        return vehiculeService.rechercherParTarifSuperieurOuEgal(valeur);
    }

    @GetMapping("/recherche/tarif/entre")
    @Operation(summary = "12. Tarif entre deux bornes incluses")
    public List<Vehicule> rechercherParTarifEntre(@RequestParam("min") BigDecimal min, @RequestParam("max") BigDecimal max) {
        return vehiculeService.rechercherParTarifEntre(min, max);
    }

    @GetMapping("/recherche/trier/tarif-croissant")
    @Operation(summary = "13. Tri par tarif croissant")
    public List<Vehicule> trierParTarifCroissant() {
        return vehiculeService.trierParTarifCroissant();
    }

    @GetMapping("/recherche/trier/tarif-decroissant")
    @Operation(summary = "14. Tri par tarif décroissant")
    public List<Vehicule> trierParTarifDecroissant() {
        return vehiculeService.trierParTarifDecroissant();
    }

    @GetMapping("/recherche/marque/trier-tarif-croissant")
    @Operation(summary = "15. Véhicules d'une marque par tarif croissant")
    public List<Vehicule> rechercherParMarqueEtTrierParTarifCroissant(@RequestParam("marque") String marque) {
        return vehiculeService.rechercherParMarqueEtTrierParTarifCroissant(marque);
    }

    @GetMapping("/recherche/marque/tarif-superieur")
    @Operation(summary = "16. Marque et tarif supérieur")
    public List<Vehicule> rechercherParMarqueEtTarifSuperieur(@RequestParam("marque") String marque, @RequestParam("tarif") BigDecimal tarif) {
        return vehiculeService.rechercherParMarqueEtTarifSuperieur(marque, tarif);
    }

    @GetMapping("/recherche/marque-ou-modele")
    @Operation(summary = "17. Marque ou modèle")
    public List<Vehicule> rechercherParMarqueOuModele(@RequestParam("marque") String marque, @RequestParam("modele") String modele) {
        return vehiculeService.rechercherParMarqueOuModele(marque, modele);
    }

    @GetMapping("/recherche/marque/dans-liste")
    @Operation(summary = "18. Marque dans une liste")
    public List<Vehicule> rechercherParMarques(@RequestParam("marques") List<String> marques) {
        return vehiculeService.rechercherParMarques(marques);
    }

    @GetMapping("/recherche/marque/hors-liste")
    @Operation(summary = "19. Marque hors d'une liste")
    public List<Vehicule> rechercherHorsMarques(@RequestParam("marques") List<String> marques) {
        return vehiculeService.rechercherHorsMarques(marques);
    }

    @GetMapping("/recherche/statut")
    @Operation(summary = "20. Véhicules par statut")
    public List<Vehicule> rechercherParStatut(@RequestParam("statut") StatutVehicule statut) {
        return vehiculeService.rechercherParStatut(statut);
    }

    @GetMapping("/recherche/statut/disponibles/tarif-croissant")
    @Operation(summary = "21. Véhicules disponibles par tarif croissant")
    public List<Vehicule> rechercherDisponiblesParTarifCroissant() {
        return vehiculeService.rechercherDisponiblesParTarifCroissant();
    }

    @GetMapping("/recherche/agence/{idAgence}")
    @Operation(summary = "22. Véhicules d'une agence")
    public List<Vehicule> rechercherParAgence(@PathVariable("idAgence") long idAgence) {
        return vehiculeService.rechercherParAgence(idAgence);
    }

    @GetMapping("/recherche/agence/ville")
    @Operation(summary = "23. Véhicules d'une agence dans une ville")
    public List<Vehicule> rechercherParVilleAgence(@RequestParam("ville") String ville) {
        return vehiculeService.rechercherParVilleAgence(ville);
    }

    @GetMapping("/recherche/agence/nom-contient")
    @Operation(summary = "24. Nom d'agence contenant une valeur")
    public List<Vehicule> rechercherParNomAgenceContenant(@RequestParam("mot") String mot) {
        return vehiculeService.rechercherParNomAgenceContenant(mot);
    }

    @GetMapping("/recherche/agence/{idAgence}/marque")
    @Operation(summary = "25. Véhicules d'une agence et d'une marque")
    public List<Vehicule> rechercherParAgenceEtMarque(@PathVariable("idAgence") long idAgence, @RequestParam("marque") String marque) {
        return vehiculeService.rechercherParAgenceEtMarque(idAgence, marque);
    }

    @GetMapping("/recherche/sans-agence")
    @Operation(summary = "26. Véhicules sans agence")
    public List<Vehicule> rechercherSansAgence() {
        return vehiculeService.rechercherSansAgence();
    }

    @GetMapping("/recherche/avec-agence")
    @Operation(summary = "27. Véhicules affectés à une agence")
    public List<Vehicule> rechercherAvecAgence() {
        return vehiculeService.rechercherAvecAgence();
    }

    @GetMapping("/recherche/agence/{idAgence}/compter")
    @Operation(summary = "28. Compter les véhicules d'une agence")
    public long compterParAgence(@PathVariable("idAgence") long idAgence) {
        return vehiculeService.compterParAgence(idAgence);
    }

    @GetMapping("/recherche/agence/{idAgence}/possede-vehicules")
    @Operation(summary = "29. Vérifier si une agence possède des véhicules")
    public boolean agencePossedeVehicules(@PathVariable("idAgence") long idAgence) {
        return vehiculeService.agencePossedeVehicules(idAgence);
    }

    @PutMapping("/{idV}/agence/{idAgence}")
    @Operation(summary = "Affecter un véhicule à une agence")
    public Vehicule affecterAgenceVehicule(@PathVariable("idV") long idV,
                                          @PathVariable("idAgence") long idAgence) {
        return vehiculeService.affecterAgenceVehicule(idV, idAgence);
    }

}
