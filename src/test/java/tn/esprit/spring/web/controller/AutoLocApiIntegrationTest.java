package tn.esprit.spring.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:autoloc-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "server.servlet.context-path="
})
class AutoLocApiIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void swaggerAndCrudEndpointsAreAvailable() throws Exception {
        HttpResponse<String> swaggerUi = send("GET", "/swagger-ui/index.html", null);
        assertThat(swaggerUi.statusCode()).isEqualTo(200);

        HttpResponse<String> openApi = send("GET", "/v3/api-docs", null);
        assertThat(openApi.statusCode()).isEqualTo(200);
        assertThat(openApi.body()).contains("/employes", "/vehicules", "/reservations");

        assertThat(send("GET", "/", null).statusCode()).isEqualTo(200);
        assertThat(send("GET", "/employes", null).statusCode()).isEqualTo(200);
        assertThat(send("GET", "/employes/test", null).statusCode()).isEqualTo(200);

        for (String path : new String[]{
                "/agences/listall", "/clients/listall", "/contrats/listall",
                "/employes/listall", "/equipements/listall", "/maintenances/listall",
                "/paiements/listall", "/reservations/listall", "/vehicules/listall"
        }) {
            HttpResponse<String> response = send("GET", path, null);
            assertThat(response.statusCode()).as(path).isEqualTo(200);
            assertThat(response.body()).as(path).isEqualTo("[]");
        }

        HttpResponse<String> created = send("POST", "/employes",
                "{\"idEmploye\":0,\"nom\":\"Hamza\",\"prenom\":\"Fhaiel\",\"role\":\"AGENT\"}");
        assertThat(created.statusCode()).isEqualTo(201);
        Matcher idMatcher = Pattern.compile("\"idEmploye\"\\s*:\\s*(\\d+)").matcher(created.body());
        assertThat(idMatcher.find()).isTrue();
        String id = idMatcher.group(1);

        HttpResponse<String> updated = send("PUT", "/employes/" + id,
                "{\"nom\":\"Hamza\",\"prenom\":\"Updated\",\"role\":\"MANAGER\"}");
        assertThat(updated.statusCode()).isEqualTo(200);
        assertThat(updated.body()).contains("Updated", "MANAGER");

        HttpResponse<String> deleted = send("DELETE", "/employes/" + id, null);
        assertThat(deleted.statusCode()).isEqualTo(204);

        HttpResponse<String> missing = send("GET", "/employes/" + id, null);
        assertThat(missing.statusCode()).isEqualTo(404);
    }

    @Test
    void remainingEntitiesSupportCompleteCrudLifecycle() throws Exception {
        String[][] cases = {
                {"agences", "idAgence", "{\"nom\":\"Agence Test\",\"ville\":\"Tunis\",\"adresse\":\"Rue Test\",\"telephone\":\"12345678\"}", "Tunis", "Sousse"},
                {"clients", "idClient", "{\"nom\":\"Client Test\",\"prenom\":\"Alice\",\"email\":\"alice@example.com\",\"telephone\":\"12345678\",\"numPermis\":\"TEST123\",\"dateInscription\":\"2026-10-01\"}", "Alice", "Bob"},
                {"contrats", "idContrat", "{\"dateSignature\":\"2026-10-01\",\"montantTotal\":100.00,\"valide\":false}", "100.00", "250.00"},
                {"equipements", "idEquipement", "{\"libelle\":\"GPS\"}", "GPS", "Siege enfant"},
                {"maintenances", "idMaintenance", "{\"dateDebut\":\"2026-10-01\",\"dateFin\":\"2026-10-02\",\"description\":\"Vidange\"}", "Vidange", "Pneus"},
                {"paiements", "idPaiement", "{\"montant\":100.00,\"datePaiement\":\"2026-10-01\",\"modePaiement\":\"CARTE\"}", "CARTE", "VIREMENT"},
                {"reservations", "idReservation", "{\"dateDebut\":\"2026-10-01\",\"dateFin\":\"2026-10-02\",\"statut\":\"EN_ATTENTE\"}", "EN_ATTENTE", "CONFIRMEE"},
                {"vehicules", "idVehicule", "{\"immatriculation\":\"TEST-123\",\"marque\":\"Renault\",\"modele\":\"Clio\",\"categorie\":\"CITADINE\",\"tarifJournalier\":50.00,\"statut\":\"DISPONIBLE\"}", "Clio", "Megane"}
        };

        for (String[] entry : cases) {
            String path = "/" + entry[0];
            String swaggerBody = "{\"" + entry[1] + "\":0," + entry[2].substring(1);
            HttpResponse<String> created = send("POST", path, swaggerBody);
            assertThat(created.statusCode()).as("create %s", path).isEqualTo(201);
            Matcher idMatcher = Pattern.compile("\"" + entry[1] + "\"\\s*:\\s*(\\d+)").matcher(created.body());
            assertThat(idMatcher.find()).as("generated ID for %s", path).isTrue();
            String itemPath = path + "/" + idMatcher.group(1);

            HttpResponse<String> fetched = send("GET", itemPath, null);
            assertThat(fetched.statusCode()).as("read %s", path).isEqualTo(200);
            assertThat(fetched.body()).contains(entry[3]);
            assertThat(send("GET", path + "/listall", null).body()).contains(entry[3]);

            HttpResponse<String> updated = send("PUT", itemPath, entry[2].replace(entry[3], entry[4]));
            assertThat(updated.statusCode()).as("update %s", path).isEqualTo(200);
            assertThat(updated.body()).contains(entry[4]);
            assertThat(send("GET", itemPath, null).body()).contains(entry[4]);

            assertThat(send("DELETE", itemPath, null).statusCode()).as("delete %s", path).isEqualTo(204);
            assertThat(send("GET", itemPath, null).statusCode()).isEqualTo(404);
            assertThat(send("PUT", itemPath, entry[2]).statusCode()).isEqualTo(404);
            assertThat(send("DELETE", itemPath, null).statusCode()).isEqualTo(404);
            assertThat(send("GET", path + "/listall", null).body()).isEqualTo("[]");
        }
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        HttpResponse<String> response = send("POST", "/employes", "{");
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("error");
    }

    @Test
    void generatedIdsAreReadOnlyInOpenApi() throws Exception {
        HttpResponse<String> response = send("GET", "/v3/api-docs", null);
        assertThat(response.statusCode()).isEqualTo(200);
        for (String entity : new String[]{"Agence", "Client", "Contrat", "Employe", "Equipement", "Maintenance", "Paiement", "Reservation", "Vehicule"}) {
            assertThat(response.body()).containsPattern("\"id" + entity + "\"\\s*:\\s*\\{[^}]*\"readOnly\"\\s*:\\s*true");
        }
    }

    @Test
    void employeesCanBeAssignedAndReassignedToExistingAgencies() throws Exception {
        String firstAgency = null;
        String secondAgency = null;
        String firstEmployee = null;
        String secondEmployee = null;
        try {
            firstAgency = createId("/agences", "idAgence", "{\"nom\":\"Assignment Agency One\"}");
            secondAgency = createId("/agences", "idAgence", "{\"nom\":\"Assignment Agency Two\"}");
            firstEmployee = createId("/employes", "idEmploye", "{\"nom\":\"Assignment Employee One\",\"role\":\"AGENT\"}");
            secondEmployee = createId("/employes", "idEmploye", "{\"nom\":\"Assignment Employee Two\",\"role\":\"AGENT\"}");

            for (String employee : new String[]{firstEmployee, secondEmployee}) {
                HttpResponse<String> assigned = send("PUT", "/employes/" + employee + "/agence/" + firstAgency, null);
                assertThat(assigned.statusCode()).isEqualTo(200);
                assertThat(assigned.body()).contains("Assignment Agency One");
                assertThat(send("GET", "/employes/" + employee, null).body()).contains("Assignment Agency One");
            }

            HttpResponse<String> reassigned = send("PUT", "/employes/" + firstEmployee + "/agence/" + secondAgency, null);
            assertThat(reassigned.statusCode()).isEqualTo(200);
            assertThat(send("GET", "/employes/" + firstEmployee, null).body()).contains("Assignment Agency Two");
            assertThat(send("GET", "/employes/" + secondEmployee, null).body()).contains("Assignment Agency One");

            assertThat(send("PUT", "/employes/" + firstEmployee + "/agence/9223372036854775807", null).statusCode()).isEqualTo(404);
            assertThat(send("PUT", "/employes/9223372036854775807/agence/" + firstAgency, null).statusCode()).isEqualTo(404);
            assertThat(send("GET", "/employes/" + firstEmployee, null).body()).contains("Assignment Agency Two");

            HttpResponse<String> edited = send("PUT", "/employes/" + firstEmployee,
                    "{\"nom\":\"Edited Employee\",\"role\":\"MANAGER\"}");
            assertThat(edited.statusCode()).isEqualTo(200);
            assertThat(edited.body()).contains("Edited Employee", "Assignment Agency Two");
            assertThat(send("GET", "/employes/listall", null).body()).contains("Assignment Agency One", "Assignment Agency Two");
            assertThat(send("GET", "/v3/api-docs", null).body()).contains("/employes/{idEmploye}/agence/{idAgence}");

            assertThat(send("DELETE", "/employes/" + firstEmployee, null).statusCode()).isEqualTo(204);
            firstEmployee = null;
            assertThat(send("GET", "/agences/" + secondAgency, null).statusCode()).isEqualTo(200);
        } finally {
            if (firstEmployee != null) send("DELETE", "/employes/" + firstEmployee, null);
            if (secondEmployee != null) send("DELETE", "/employes/" + secondEmployee, null);
            if (firstAgency != null) send("DELETE", "/agences/" + firstAgency, null);
            if (secondAgency != null) send("DELETE", "/agences/" + secondAgency, null);
        }
    }

    @Test
    void vehicleEquipmentAssignmentsPersistWithoutDuplicatesOrCascadeDeletes() throws Exception {
        String firstVehicle = null;
        String secondVehicle = null;
        String firstEquipment = null;
        String secondEquipment = null;
        try {
            firstVehicle = createVehicle("EQUIP-V-1");
            secondVehicle = createVehicle("EQUIP-V-2");
            firstEquipment = createId("/equipements", "idEquipement", "{\"libelle\":\"Assignment GPS\"}");
            secondEquipment = createId("/equipements", "idEquipement", "{\"libelle\":\"Assignment Seat\"}");

            String assignment = "/vehicules/" + firstVehicle + "/equipements/" + firstEquipment;
            assertThat(send("PUT", assignment, null).statusCode()).isEqualTo(200);
            assertThat(send("PUT", assignment, null).statusCode()).isEqualTo(200);
            assertThat(send("PUT", "/vehicules/" + firstVehicle + "/equipements/" + secondEquipment, null).statusCode()).isEqualTo(200);
            assertThat(send("PUT", "/vehicules/" + secondVehicle + "/equipements/" + firstEquipment, null).statusCode()).isEqualTo(200);

            HttpResponse<String> saved = send("GET", "/vehicules/" + firstVehicle, null);
            assertThat(saved.statusCode()).isEqualTo(200);
            assertThat(saved.body()).contains("Assignment GPS", "Assignment Seat");
            assertThat(Pattern.compile("\"idEquipement\"\\s*:\\s*" + firstEquipment + "\\b")
                    .matcher(saved.body()).results().count()).isEqualTo(1);
            assertThat(send("GET", "/vehicules/" + secondVehicle, null).body()).contains("Assignment GPS");

            assertThat(send("PUT", "/vehicules/" + firstVehicle + "/equipements/9223372036854775807", null).statusCode()).isEqualTo(404);
            assertThat(send("PUT", "/vehicules/9223372036854775807/equipements/" + firstEquipment, null).statusCode()).isEqualTo(404);
            HttpResponse<String> edited = send("PUT", "/vehicules/" + firstVehicle, vehicleBody("EQUIP-V-1"));
            assertThat(edited.statusCode()).isEqualTo(200);
            assertThat(edited.body()).contains("Assignment GPS", "Assignment Seat");
            assertThat(send("GET", "/vehicules/listall", null).body()).contains("Assignment GPS");
            assertThat(send("GET", "/v3/api-docs", null).body()).contains("/vehicules/{idV}/equipements/{idEq}");

            assertThat(send("DELETE", "/vehicules/" + firstVehicle, null).statusCode()).isEqualTo(204);
            firstVehicle = null;
            assertThat(send("GET", "/equipements/" + firstEquipment, null).statusCode()).isEqualTo(200);
            assertThat(send("GET", "/vehicules/" + secondVehicle, null).body()).contains("Assignment GPS");
        } finally {
            if (firstVehicle != null) send("DELETE", "/vehicules/" + firstVehicle, null);
            if (secondVehicle != null) send("DELETE", "/vehicules/" + secondVehicle, null);
            if (firstEquipment != null) send("DELETE", "/equipements/" + firstEquipment, null);
            if (secondEquipment != null) send("DELETE", "/equipements/" + secondEquipment, null);
        }
    }

    @Test
    void maintenanceCanBeAssignedAndReassignedWithoutChangingTheVehicle() throws Exception {
        String firstVehicle = null;
        String secondVehicle = null;
        String firstMaintenance = null;
        String secondMaintenance = null;
        try {
            firstVehicle = createVehicle("MAINT-V-1");
            secondVehicle = createVehicle("MAINT-V-2");
            firstMaintenance = createId("/maintenances", "idMaintenance", "{\"description\":\"Assignment Oil\"}");
            secondMaintenance = createId("/maintenances", "idMaintenance", "{\"description\":\"Assignment Tires\"}");

            for (String maintenance : new String[]{firstMaintenance, secondMaintenance}) {
                assertThat(send("PUT", "/maintenances/" + maintenance + "/vehicule/" + firstVehicle, null).statusCode()).isEqualTo(200);
                assertThat(send("GET", "/maintenances/" + maintenance, null).body()).contains("MAINT-V-1");
            }
            assertThat(send("PUT", "/maintenances/" + firstMaintenance + "/vehicule/" + secondVehicle, null).statusCode()).isEqualTo(200);
            assertThat(send("GET", "/maintenances/" + firstMaintenance, null).body()).contains("MAINT-V-2");
            assertThat(send("GET", "/maintenances/" + secondMaintenance, null).body()).contains("MAINT-V-1");

            assertThat(send("PUT", "/maintenances/" + firstMaintenance + "/vehicule/9223372036854775807", null).statusCode()).isEqualTo(404);
            assertThat(send("PUT", "/maintenances/9223372036854775807/vehicule/" + firstVehicle, null).statusCode()).isEqualTo(404);
            HttpResponse<String> edited = send("PUT", "/maintenances/" + firstMaintenance, "{\"description\":\"Updated Maintenance\"}");
            assertThat(edited.statusCode()).isEqualTo(200);
            assertThat(edited.body()).contains("Updated Maintenance", "MAINT-V-2");
            assertThat(send("GET", "/maintenances/listall", null).body()).contains("MAINT-V-1", "MAINT-V-2");
            assertThat(send("GET", "/v3/api-docs", null).body()).contains("/maintenances/{idM}/vehicule/{idV}");

            assertThat(send("DELETE", "/maintenances/" + firstMaintenance, null).statusCode()).isEqualTo(204);
            firstMaintenance = null;
            assertThat(send("GET", "/vehicules/" + secondVehicle, null).statusCode()).isEqualTo(200);
        } finally {
            if (firstMaintenance != null) send("DELETE", "/maintenances/" + firstMaintenance, null);
            if (secondMaintenance != null) send("DELETE", "/maintenances/" + secondMaintenance, null);
            if (firstVehicle != null) send("DELETE", "/vehicules/" + firstVehicle, null);
            if (secondVehicle != null) send("DELETE", "/vehicules/" + secondVehicle, null);
        }
    }

    private String vehicleBody(String registration) {
        return "{\"immatriculation\":\"" + registration + "\",\"marque\":\"Renault\",\"modele\":\"Clio\","
                + "\"categorie\":\"CITADINE\",\"tarifJournalier\":50.00,\"statut\":\"DISPONIBLE\"}";
    }

    private String createVehicle(String registration) throws Exception {
        return createId("/vehicules", "idVehicule", vehicleBody(registration));
    }

    private String createId(String path, String idField, String body) throws Exception {
        HttpResponse<String> response = send("POST", path, body);
        assertThat(response.statusCode()).isEqualTo(201);
        Matcher matcher = Pattern.compile("\"" + idField + "\"\\s*:\\s*(\\d+)").matcher(response.body());
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path));
        if (body == null) {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
