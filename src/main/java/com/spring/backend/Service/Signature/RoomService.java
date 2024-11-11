package com.spring.backend.Service.Signature;

import com.spring.backend.DTO.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RoomService {

    ResponseDTO addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice,String description);

    ResponseDTO getAllRoomTypes();

    ResponseDTO getAllRooms();

    ResponseDTO deleteRoom(Long roomId);

    ResponseDTO updateRoom(Long roomId, String description, String roomType,BigDecimal roomPrice,MultipartFile photo);

    ResponseDTO getRoomById(Long roomId);

    ResponseDTO getAllAvailableRooms();

    ResponseDTO getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);





}
