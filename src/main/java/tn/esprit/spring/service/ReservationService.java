package tn.esprit.spring.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.domain.Reservation;
import tn.esprit.spring.repository.ReservationRepository;

import java.util.List;

@Service
public class ReservationService implements IReservation {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation creer(Reservation reservation) {
        reservation.setIdReservation(null);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation obtenirParId(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation introuvable avec l'identifiant " + id));
    }

    @Override
    public List<Reservation> listerTous() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation modifier(long id, Reservation reservation) {
        Reservation existante = obtenirParId(id);
        existante.setDateDebut(reservation.getDateDebut());
        existante.setDateFin(reservation.getDateFin());
        existante.setStatut(reservation.getStatut());
        return reservationRepository.save(existante);
    }

    @Override
    public void supprimer(long id) {
        obtenirParId(id);
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Reservation ajouterReservationContrat(Reservation r) {
        if (r == null || r.getContrat() == null) {
            throw new IllegalArgumentException("La réservation doit contenir un nouveau contrat.");
        }

        r.setIdReservation(null);
        r.getContrat().setIdContrat(null);
        return reservationRepository.save(r);
    }
}
