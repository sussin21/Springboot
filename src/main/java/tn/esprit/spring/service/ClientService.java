package tn.esprit.spring.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.domain.Client;
import tn.esprit.spring.domain.Reservation;
import tn.esprit.spring.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService implements IClient {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client creer(Client client) {
        client.setIdClient(null);
        return clientRepository.save(client);
    }

    @Override
    public Client obtenirParId(long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'identifiant " + id));
    }

    @Override
    public List<Client> listerTous() {
        return clientRepository.findAll();
    }

    @Override
    public Client modifier(long id, Client client) {
        Client existant = obtenirParId(id);
        existant.setNom(client.getNom());
        existant.setPrenom(client.getPrenom());
        existant.setEmail(client.getEmail());
        existant.setTelephone(client.getTelephone());
        existant.setNumPermis(client.getNumPermis());
        existant.setDateInscription(client.getDateInscription());
        return clientRepository.save(existant);
    }

    @Override
    public void supprimer(long id) {
        obtenirParId(id);
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Client ajouterReservationClient(Client c) {
        if (c == null || c.getReservations() == null || c.getReservations().isEmpty()
                || c.getReservations().contains(null)) {
            throw new IllegalArgumentException("Le client doit contenir au moins une réservation non nulle.");
        }

        c.setIdClient(null);
        for (Reservation reservation : c.getReservations()) {
            reservation.setIdReservation(null);
            if (reservation.getContrat() != null) {
                reservation.getContrat().setIdContrat(null);
            }
        }
        return clientRepository.save(c);
    }
}
