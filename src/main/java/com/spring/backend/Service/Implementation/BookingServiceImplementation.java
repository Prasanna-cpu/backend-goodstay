package com.spring.backend.Service.Implementation;

import com.spring.backend.DTO.BookingDTO;
import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Entity.Booking;
import com.spring.backend.Entity.Room;
import com.spring.backend.Entity.User;
import com.spring.backend.Exception.*;
import com.spring.backend.Mapper.BookingMapper;
import com.spring.backend.Repository.BookingRepository;
import com.spring.backend.Repository.RoomRepository;
import com.spring.backend.Repository.UserRepository;
import com.spring.backend.Service.Signature.BookingService;
import com.spring.backend.Service.Signature.RoomService;
import com.spring.backend.Utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BookingServiceImplementation implements BookingService {


    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = {RoomResourceNotFoundException.class, UserNotFoundException.class, MisappropriateDatesException.class, RoomNotAvailableException.class})
    public ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        ResponseDTO responseDTO=new ResponseDTO();
        log.info("Received booking: {}", bookingRequest);


        try{
            if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
                throw new MisappropriateDatesException("Invalid checkout date: " + bookingRequest.getCheckOutDate());
            }
            Room room=roomRepository.findById(roomId).orElseThrow(
                    ()->new RoomResourceNotFoundException("Room " + roomId + " not found")
            );

            User user=userRepository.findById(userId).orElseThrow(
                    ()->new UserNotFoundException("User " + userId + " not found")
            );

            List<Booking> existingBookings=room.getBookings();

            if(!roomIsAvailable(bookingRequest,existingBookings)){
                throw new RoomNotAvailableException("Room not available for selected dates");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode= Utils.generateConfirmationCode(10);
            bookingRequest.setBookingConfirmation(bookingConfirmationCode);
            Booking newBooking=bookingRepository.save(bookingRequest);
            BookingDTO bookingDTO=BookingMapper.mapToBookingDTO(newBooking);
            responseDTO.setStatusCode(HttpStatus.CREATED.value());
            responseDTO.setMessage("Booked Successfully");
            responseDTO.setBooking(bookingDTO);
            responseDTO.setBookingConfirmation(bookingConfirmationCode);

        }
        catch(RoomResourceNotFoundException | UserNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
            throw e;
        }
        catch(MisappropriateDatesException e){
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage(e.getMessage());
            throw e;
        }
        catch(RoomNotAvailableException e){
            responseDTO.setStatusCode(HttpStatus.CONFLICT.value());
            responseDTO.setMessage(e.getMessage());
            throw e;
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during booking creation:"+e.getMessage());
            throw e;
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO findBookingByConfirmationCode(String confirmationCode) {
        ResponseDTO responseDTO=new ResponseDTO();
        try{
            Booking booking=bookingRepository.findByBookingConfirmation(confirmationCode)
                    .orElseThrow(
                            ()->new BookingResourceNotFoundException("Booking with confirmation code " + confirmationCode + " not found")
                    );
            BookingDTO bookingDTO= BookingMapper.mapToBookingDTOPlusBookedRooms(booking,true);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Booking found successfully");
            responseDTO.setBooking(bookingDTO);

        }
        catch(BookingResourceNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during booking retrieval:"+e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getAllBookings() {
        ResponseDTO responseDTO=new ResponseDTO();

        try {
            List<Booking> bookingList=bookingRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<BookingDTO> bookingDTOList=BookingMapper.mapBookingListToBookingDTOList(bookingList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Bookings found successfully");
            responseDTO.setBookingList(bookingDTOList);
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during bookings retrieval:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO cancelBooking(Long bookingId) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            bookingRepository.findById(bookingId).ifPresentOrElse(bookingRepository::delete,()->{
                throw new BookingResourceNotFoundException("Booking with id " + bookingId + " not found");
            });

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Booking cancelled successfully");

        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during booking cancellation:"+e.getMessage());
        }

        return responseDTO;
    }
}
