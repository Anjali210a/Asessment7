
package com.example.moviereservation.repository;

import com.example.moviereservation.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByMovieTitleAndShowtime(String movieTitle, String showtime);
}
