package tn.esprit.spring.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identifiant généré par la base de données.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String numPermis;

    private LocalDate dateInscription;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_client")
    private List<Reservation> reservations = new ArrayList<>();

    public Client(Long idClient, String nom, String prenom, String email, String telephone,
                  String numPermis, LocalDate dateInscription) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.numPermis = numPermis;
        this.dateInscription = dateInscription;
    }
}
