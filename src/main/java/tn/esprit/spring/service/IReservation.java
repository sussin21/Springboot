package tn.esprit.spring.service;

import tn.esprit.spring.domain.Reservation;

import java.util.List;

public interface IReservation {

    Reservation creer(Reservation reservation);

    Reservation obtenirParId(long id);

    List<Reservation> listerTous();

    Reservation modifier(long id, Reservation reservation);

    void supprimer(long id);
}
