package tn.esprit.spring.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employe {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identifiant généré par la base de données.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    private String nom;

    private String prenom;

    @Enumerated(EnumType.STRING)
    private RoleEmploye role;

    @ManyToOne
    @JoinColumn(name = "id_agence")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY,
            description = "Agence affectée via PUT /employes/{idEmploye}/agence/{idAgence}.")
    private Agence agence;

    public Employe(Long idEmploye, String nom, String prenom, RoleEmploye role) {
        this.idEmploye = idEmploye;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }
}
