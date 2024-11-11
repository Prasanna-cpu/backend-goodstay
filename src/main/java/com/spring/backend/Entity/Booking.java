package com.spring.backend.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="bookings")
public class Booking extends BaseEntity implements Serializable {

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numberOfAdults=" + numberOfAdults +
                ", numberOfChildren=" + numberOfChildren +
                ", totalNumberOfGuests=" + totalNumberOfGuests +
                ", bookingConfirmation='" + bookingConfirmation + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in_date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "check in date is required and must not be null")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "check out date is required and must be in future")
    private LocalDate checkOutDate;

    @Column(name = "number_of_adults")
    @Min(value=1,message = "number of adults must not be less than 1")
    private int numberOfAdults;

    @Column(name = "number_of_children")
    @Min(value = 0, message = "Number of children must not be less that 0")
    private int numberOfChildren;

    @Column(name = "total_number_of_guests")
    private int totalNumberOfGuests;

    @Column(name = "booking_confirmation")
    private String bookingConfirmation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateTotalNumberOfGuests(){
        this.totalNumberOfGuests=this.numberOfAdults+this.numberOfChildren;
    }

    public void setNumberOfAdults(int numberOfAdults){
        this.numberOfAdults=numberOfAdults;
        calculateTotalNumberOfGuests();
    }

    public void setNumberOfChildren(int numberOfChildren){
        this.numberOfChildren=numberOfChildren;
        calculateTotalNumberOfGuests();
    }



}
