package tn.esprit.spring.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Maintenance {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identifiant généré par la base de données.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_vehicule")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY,
            description = "Véhicule affecté via PUT /maintenances/{idM}/vehicule/{idV}.")
    private Vehicule vehicule;

    public Maintenance(Long idMaintenance, LocalDate dateDebut, LocalDate dateFin, String description) {
        this.idMaintenance = idMaintenance;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
    }
}
