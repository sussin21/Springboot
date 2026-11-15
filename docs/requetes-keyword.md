# Requêtes keyword — guide de dépôt et de démonstration

Les trois fichiers demandés sont VehiculeController.java, VehiculeRepository.java et VehiculeService.java. Ils contiennent les 29 requêtes de l’énoncé, numérotées dans le code.

Pour intégrer le code dans le projet complet, IVehicule.java déclare les nouvelles méthodes et Vehicule.java contient l’association @ManyToOne vers Agence. Les fichiers IVehicule.java et Vehicule.java sont inclus dans le projet.

## Vérifier dans Swagger

Démarrer MySQL, lancer AutoLocationApplication, puis ouvrir http://localhost:8081/swagger-ui/index.html. Les recherches sont des requêtes GET : aucun corps JSON à envoyer. Les listes vides, 0 et false sont des résultats normaux si les données ne correspondent pas.

Créer une agence (POST /agences), puis un véhicule (POST /vehicules). Utiliser les IDs retournés pour PUT /vehicules/{idV}/agence/{idAgence} sans JSON. Cette affectation permet de tester les requêtes 22 à 29. Exemple : PUT /vehicules/3/agence/2.

## Correspondance des 29 requêtes

| N° | Besoin | Méthode keyword du repository | Exemple GET |
|---|---|---|---|
| 1 | Véhicules d'une marque | `findByMarque` | `/vehicules/recherche/marque?marque=Renault` |
| 2 | Marque contenant un mot | `findByMarqueContaining` | `/vehicules/recherche/marque/contient?mot=nau` |
| 3 | Marque commençant par une valeur | `findByMarqueStartingWith` | `/vehicules/recherche/marque/commence-par?valeur=Ren` |
| 4 | Marque se terminant par une valeur | `findByMarqueEndingWith` | `/vehicules/recherche/marque/termine-par?valeur=ault` |
| 5 | Marque sans tenir compte de la casse | `findByMarqueIgnoreCase` | `/vehicules/recherche/marque/ignore-casse?marque=RENAULT` |
| 6 | Véhicules par modèle | `findByModele` | `/vehicules/recherche/modele?modele=Clio` |
| 7 | Existence d'une immatriculation | `existsByImmatriculation` | `/vehicules/recherche/immatriculation/existe?immatriculation=123-TUN-456` |
| 8 | Compter les véhicules d'une marque | `countByMarque` | `/vehicules/recherche/marque/compter?marque=Renault` |
| 9 | Tarif supérieur à une valeur | `findByTarifJournalierGreaterThan` | `/vehicules/recherche/tarif/superieur?valeur=100` |
| 10 | Tarif inférieur à une valeur | `findByTarifJournalierLessThan` | `/vehicules/recherche/tarif/inferieur?valeur=100` |
| 11 | Tarif supérieur ou égal à une valeur | `findByTarifJournalierGreaterThanEqual` | `/vehicules/recherche/tarif/superieur-ou-egal?valeur=100` |
| 12 | Tarif entre deux bornes incluses | `findByTarifJournalierBetween` | `/vehicules/recherche/tarif/entre?min=50&max=150` |
| 13 | Tri par tarif croissant | `findAllByOrderByTarifJournalierAsc` | `/vehicules/recherche/trier/tarif-croissant` |
| 14 | Tri par tarif décroissant | `findAllByOrderByTarifJournalierDesc` | `/vehicules/recherche/trier/tarif-decroissant` |
| 15 | Véhicules d'une marque par tarif croissant | `findByMarqueOrderByTarifJournalierAsc` | `/vehicules/recherche/marque/trier-tarif-croissant?marque=Renault` |
| 16 | Marque et tarif supérieur | `findByMarqueAndTarifJournalierGreaterThan` | `/vehicules/recherche/marque/tarif-superieur?marque=Renault&tarif=100` |
| 17 | Marque ou modèle | `findByMarqueOrModele` | `/vehicules/recherche/marque-ou-modele?marque=Renault&modele=Yaris` |
| 18 | Marque dans une liste | `findByMarqueIn` | `/vehicules/recherche/marque/dans-liste?marques=Renault,Toyota` |
| 19 | Marque hors d'une liste | `findByMarqueNotIn` | `/vehicules/recherche/marque/hors-liste?marques=Renault,Toyota` |
| 20 | Véhicules par statut | `findByStatut` | `/vehicules/recherche/statut?statut=DISPONIBLE` |
| 21 | Véhicules disponibles par tarif croissant | `findByStatutOrderByTarifJournalierAsc` | `/vehicules/recherche/statut/disponibles/tarif-croissant` |
| 22 | Véhicules d'une agence | `findByAgence_IdAgence` | `/vehicules/recherche/agence/1` |
| 23 | Véhicules d'une agence dans une ville | `findByAgence_Ville` | `/vehicules/recherche/agence/ville?ville=Tunis` |
| 24 | Nom d'agence contenant une valeur | `findByAgence_NomContaining` | `/vehicules/recherche/agence/nom-contient?mot=Centre` |
| 25 | Véhicules d'une agence et d'une marque | `findByAgence_IdAgenceAndMarque` | `/vehicules/recherche/agence/1/marque?marque=Renault` |
| 26 | Véhicules sans agence | `findByAgenceIsNull` | `/vehicules/recherche/sans-agence` |
| 27 | Véhicules affectés à une agence | `findByAgenceIsNotNull` | `/vehicules/recherche/avec-agence` |
| 28 | Compter les véhicules d'une agence | `countByAgence_IdAgence` | `/vehicules/recherche/agence/1/compter` |
| 29 | Vérifier si une agence possède des véhicules | `existsByAgence_IdAgence` | `/vehicules/recherche/agence/1/possede-vehicules` |

Les exemples d’agence utilisent l’ID 1 : remplacer cet ID par celui réellement retourné par la création. Between inclut les deux bornes. GreaterThan et LessThan sont stricts. Les tarifs utilisent BigDecimal. IgnoreCase ignore explicitement la casse ; les autres égalités textuelles suivent la collation de la base MySQL.

Dans phpMyAdmin : autoloc_db → vehicule → colonne id_agence. La relation facultative permet de conserver les véhicules existants sans agence.

Architecture : Controller → IVehicule → VehiculeService → VehiculeRepository → base. Spring Data génère les requêtes à partir des noms : aucune annotation @Query et aucun SQL manuel ne sont nécessaires pour ces 29 recherches.

## Vérification

Le test VehiculeKeywordIntegrationTest vérifie les 29 recherches via HTTP sur cinq véhicules et trois agences (dont une vide), les tris, les bornes inclusives/exclusives, les résultats absents et les paramètres invalides. La suite existante vérifie les CRUD et les associations déjà développées. Exécuter .\mvnw.cmd verify sur Windows.
