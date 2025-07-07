
package com.example.moviereservation.controller;

import com.example.moviereservation.model.Reservation;
import com.example.moviereservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final int TOTAL_SEATS = 100;

    @PostMapping("/reserve")
    public String reserveSeat(@RequestBody Reservation reservation) {
        List<Integer> bookedSeats = getBookedSeats(reservation.getMovieTitle(), reservation.getShowtime());
        for (Integer seat : reservation.getSeatNumbers()) {
            if (bookedSeats.contains(seat)) {
                return "Seat " + seat + " is already booked.";
            }
        }

        reservation.setConfirmed(true);
        Reservation saved = reservationRepository.save(reservation);

        messagingTemplate.convertAndSend("/topic/seats", "New reservation made");

        return "Reservation successful with ID: " + saved.getId();
    }

    @PutMapping("/update-seat")
    public String updateSeat(@RequestParam String reservationId, @RequestBody List<Integer> newSeats) {
        Optional<Reservation> optional = reservationRepository.findById(reservationId);
        if (!optional.isPresent()) return "Reservation not found.";

        Reservation reservation = optional.get();
        List<Integer> booked = getBookedSeats(reservation.getMovieTitle(), reservation.getShowtime());
        for (Integer seat : newSeats) {
            if (booked.contains(seat) && !reservation.getSeatNumbers().contains(seat)) {
                return "Seat " + seat + " is already booked.";
            }
        }

        reservation.setSeatNumbers(newSeats);
        reservationRepository.save(reservation);

        messagingTemplate.convertAndSend("/topic/seats", "Seat updated");

        return "Seats updated successfully!";
    }

    @GetMapping("/available-seats")
    public List<Integer> getAvailableSeats(@RequestParam String movie, @RequestParam String showtime) {
        List<Integer> booked = getBookedSeats(movie, showtime);
        List<Integer> available = new ArrayList<>();
        for (int i = 1; i <= TOTAL_SEATS; i++) {
            if (!booked.contains(i)) available.add(i);
        }
        return available;
    }

    private List<Integer> getBookedSeats(String movie, String showtime) {
        return reservationRepository.findByMovieTitleAndShowtime(movie, showtime).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toList());
    }
}
