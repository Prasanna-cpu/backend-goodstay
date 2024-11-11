package com.spring.backend.Service.Signature;

import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Entity.Booking;

public interface BookingService {
    ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest);

    ResponseDTO findBookingByConfirmationCode(String confirmationCode);

    ResponseDTO getAllBookings();

    ResponseDTO cancelBooking(Long bookingId);
}
