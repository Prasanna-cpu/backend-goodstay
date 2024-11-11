package com.spring.backend.Mapper;

import com.spring.backend.DTO.BookingDTO;
import com.spring.backend.DTO.RoomDTO;
import com.spring.backend.Entity.Booking;

import java.awt.print.Book;
import java.util.List;

public class BookingMapper {


    public static BookingDTO mapToBookingDTO(Booking booking){
        BookingDTO bookingDTO = new BookingDTO();

        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumberOfAdults(booking.getNumberOfAdults());
        bookingDTO.setNumberOfChildren(booking.getNumberOfChildren());
        bookingDTO.setTotalNumberOfGuests(booking.getTotalNumberOfGuests());
        bookingDTO.setBookingConfirmation(booking.getBookingConfirmation());

        return bookingDTO;
    }

    public static Booking mapToBooking(BookingDTO bookingDTO){
        Booking booking = new Booking();

        booking.setId(bookingDTO.getId());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setNumberOfAdults(bookingDTO.getNumberOfAdults());
        booking.setNumberOfChildren(bookingDTO.getNumberOfChildren());
        booking.setTotalNumberOfGuests(bookingDTO.getTotalNumberOfGuests());
        booking.setBookingConfirmation(bookingDTO.getBookingConfirmation());

        return booking;
    }

    public static BookingDTO mapToBookingDTOPlusBookedRooms(Booking booking,boolean mapUser){
        BookingDTO bookingDTO=new BookingDTO();


        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumberOfChildren(booking.getNumberOfChildren());
        bookingDTO.setNumberOfAdults(booking.getNumberOfAdults());
        bookingDTO.setTotalNumberOfGuests(booking.getTotalNumberOfGuests());
        bookingDTO.setBookingConfirmation(booking.getBookingConfirmation());

        if(mapUser){
            bookingDTO.setUser(UserMapper.mapToUserDTO(booking.getUser()));
        }
        if(booking.getRoom()!=null){
            RoomDTO roomDTO=new RoomDTO();

            roomDTO.setId(booking.getRoom().getId());
            roomDTO.setRoomDescription(booking.getRoom().getRoomDescription());
            roomDTO.setRoomPrice(booking.getRoom().getRoomPrice());
            roomDTO.setRoomType(booking.getRoom().getRoomType());
            roomDTO.setRoomPhotoUrl(booking.getRoom().getRoomPhotoUrl());
            bookingDTO.setRoom(roomDTO);
        }
        return bookingDTO;
    }

    public static List<BookingDTO> mapBookingListToBookingDTOList(List<Booking> bookingDTO){
        return bookingDTO.stream()
               .map(BookingMapper::mapToBookingDTO)
               .toList();
    }

}
