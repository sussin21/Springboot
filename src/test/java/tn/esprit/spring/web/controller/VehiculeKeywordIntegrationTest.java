package tn.esprit.spring.web.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tn.esprit.spring.domain.Agence;
import tn.esprit.spring.domain.CategorieVehicule;
import tn.esprit.spring.domain.StatutVehicule;
import tn.esprit.spring.domain.Vehicule;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:keyword-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop", "server.servlet.context-path="
})
class VehiculeKeywordIntegrationTest {
    @LocalServerPort private int port;
    @Autowired private VehiculeRepository vehiculeRepository;
    @Autowired private AgenceRepository agenceRepository;
    private final HttpClient http = HttpClient.newHttpClient();
    private Long a1, a2, emptyAgency;
    private Long v1, v2, v3, v4, v5;

    @BeforeAll
    void createFixtures() {
        Agence centre = agenceRepository.save(new Agence(null, "Agence Centre", "Tunis", "A", "1"));
        Agence nord = agenceRepository.save(new Agence(null, "Agence Nord", "Sousse", "B", "2"));
        Agence empty = agenceRepository.save(new Agence(null, "Agence Vide", "Tunis", "C", "3"));
        a1 = centre.getIdAgence(); a2 = nord.getIdAgence(); emptyAgency = empty.getIdAgence();
        v1 = vehicle("KW-1", "Renault", "Clio", "50", StatutVehicule.DISPONIBLE, centre);
        v2 = vehicle("KW-2", "Renault", "Megane", "100", StatutVehicule.LOUE, centre);
        v3 = vehicle("KW-3", "Peugeot", "208", "75", StatutVehicule.DISPONIBLE, nord);
        v4 = vehicle("KW-4", "Toyota", "Yaris", "100", StatutVehicule.MAINTENANCE, null);
        v5 = vehicle("KW-5", "renault", "Captur", "150", StatutVehicule.DISPONIBLE, nord);
    }

    private Long vehicle(String plate, String brand, String model, String price, StatutVehicule status, Agence agency) {
        Vehicule vehicle = new Vehicule(null, plate, brand, model, CategorieVehicule.CITADINE,
                new BigDecimal(price), status);
        vehicle.setAgence(agency);
        return vehiculeRepository.save(vehicle).getIdVehicule();
    }

    private record Query(String path, Long[] ids, String scalar, int order) {}
    private Query list(String path, Long... ids) { return new Query(path, ids, null, 0); }
    private Query sorted(String path, int order, Long... ids) { return new Query(path, ids, null, order); }
    private Query scalar(String path, String value) { return new Query(path, null, value, 0); }

    @TestFactory
    Stream<DynamicTest> all29KeywordQueries() {
        List<Query> cases = List.of(
                list("/recherche/marque?marque=Renault", v1, v2),
                list("/recherche/marque/contient?mot=nau", v1, v2, v5),
                list("/recherche/marque/commence-par?valeur=Ren", v1, v2),
                list("/recherche/marque/termine-par?valeur=ault", v1, v2, v5),
                list("/recherche/marque/ignore-casse?marque=RENAULT", v1, v2, v5),
                list("/recherche/modele?modele=Clio", v1),
                scalar("/recherche/immatriculation/existe?immatriculation=KW-1", "true"),
                scalar("/recherche/marque/compter?marque=Renault", "2"),
                list("/recherche/tarif/superieur?valeur=100", v5),
                list("/recherche/tarif/inferieur?valeur=100", v1, v3),
                list("/recherche/tarif/superieur-ou-egal?valeur=100", v2, v4, v5),
                list("/recherche/tarif/entre?min=75&max=100", v3, v2, v4),
                sorted("/recherche/trier/tarif-croissant", 1, v1, v2, v3, v4, v5),
                sorted("/recherche/trier/tarif-decroissant", -1, v1, v2, v3, v4, v5),
                sorted("/recherche/marque/trier-tarif-croissant?marque=Renault", 1, v1, v2),
                list("/recherche/marque/tarif-superieur?marque=Renault&tarif=50", v2),
                list("/recherche/marque-ou-modele?marque=Renault&modele=Yaris", v1, v2, v4),
                list("/recherche/marque/dans-liste?marques=Renault,Toyota", v1, v2, v4),
                list("/recherche/marque/hors-liste?marques=Renault,Toyota", v3, v5),
                list("/recherche/statut?statut=LOUE", v2),
                sorted("/recherche/statut/disponibles/tarif-croissant", 1, v1, v3, v5),
                list("/recherche/agence/" + a1, v1, v2),
                list("/recherche/agence/ville?ville=Tunis", v1, v2),
                list("/recherche/agence/nom-contient?mot=Nord", v3, v5),
                list("/recherche/agence/" + a2 + "/marque?marque=Peugeot", v3),
                list("/recherche/sans-agence", v4),
                list("/recherche/avec-agence", v1, v2, v3, v5),
                scalar("/recherche/agence/" + a1 + "/compter", "2"),
                scalar("/recherche/agence/" + a1 + "/possede-vehicules", "true")
        );
        return java.util.stream.IntStream.range(0, cases.size()).mapToObj(index -> {
            Query query = cases.get(index);
            return DynamicTest.dynamicTest("Keyword " + (index + 1) + " " + query.path(), () -> verify(query));
        });
    }

    private void verify(Query query) throws Exception {
        HttpResponse<String> response = send("GET", "/vehicules" + query.path());
        assertThat(response.statusCode()).as(query.path()).isEqualTo(200);
        if (query.scalar() != null) {
            assertThat(response.body()).isEqualTo(query.scalar());
        } else {
            List<Long> ids = Pattern.compile("\"idVehicule\"\\s*:\\s*(\\d+)")
                    .matcher(response.body()).results().map(m -> Long.valueOf(m.group(1))).toList();
            assertThat(ids).as(query.path()).containsExactlyInAnyOrder(query.ids());
            if (query.order() != 0) {
                List<BigDecimal> prices = Pattern.compile("\"tarifJournalier\"\\s*:\\s*([0-9.]+)")
                        .matcher(response.body()).results().map(m -> new BigDecimal(m.group(1))).toList();
                assertThat(prices).hasSize(query.ids().length);
                assertThat(prices).isSortedAccordingTo(query.order() > 0
                        ? Comparator.naturalOrder() : Comparator.reverseOrder());
            }
        }
    }

    @Test
    void emptyResultsAndInvalidParametersAreHandled() throws Exception {
        verify(list("/recherche/marque?marque=Inconnue"));
        verify(list("/recherche/agence/" + emptyAgency));
        verify(scalar("/recherche/agence/" + emptyAgency + "/compter", "0"));
        verify(scalar("/recherche/agence/" + emptyAgency + "/possede-vehicules", "false"));
        verify(scalar("/recherche/immatriculation/existe?immatriculation=ABSENT", "false"));
        assertThat(send("GET", "/vehicules/recherche/statut?statut=INVALIDE").statusCode()).isEqualTo(400);
        assertThat(send("GET", "/vehicules/recherche/tarif/superieur?valeur=abc").statusCode()).isEqualTo(400);
        assertThat(send("GET", "/vehicules/recherche/marque").statusCode()).isEqualTo(400);
    }

    @Test
    void agencyAssignmentPersistsAndRejectsMissingRecords() throws Exception {
        Long id = vehicle("KW-TEMP", "Test", "Test", "10", StatutVehicule.DISPONIBLE, null);
        try {
            assertThat(send("PUT", "/vehicules/" + id + "/agence/" + emptyAgency).statusCode()).isEqualTo(200);
            assertThat(send("GET", "/vehicules/" + id).body()).contains("Agence Vide");
            assertThat(send("PUT", "/vehicules/" + id + "/agence/9223372036854775807").statusCode()).isEqualTo(404);
            assertThat(send("PUT", "/vehicules/9223372036854775807/agence/" + a1).statusCode()).isEqualTo(404);
        } finally {
            vehiculeRepository.deleteById(id);
        }
    }

    private HttpResponse<String> send(String method, String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .method(method, HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
    }

    @AfterAll
    void cleanup() {
        vehiculeRepository.deleteAll();
        agenceRepository.deleteAll();
    }
}
