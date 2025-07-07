
package com.example.moviereservation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;
    private String username;
    private String movieTitle;
    private String showtime;
    private List<Integer> seatNumbers;
    private boolean isConfirmed;

    // Getters and Setters
}
