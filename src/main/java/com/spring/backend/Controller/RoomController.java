package com.spring.backend.Controller;


import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Service.Signature.BookingService;
import com.spring.backend.Service.Signature.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final BookingService bookingService;

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addNewRoom(
            @RequestParam(value = "photo",required = false)MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam(value = "roomDescription",required = false) String roomDescription
            ){
        if(photo==null || photo.isEmpty() || roomType==null || roomType.isBlank() ||roomPrice==null){
            ResponseDTO responseDTO=new ResponseDTO();
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage("Please provide photo,room type,room price");
        }
        ResponseDTO responseDTO=roomService.addNewRoom(photo, roomType, roomPrice, roomDescription);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }


    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllRooms(){
        ResponseDTO responseDTO=roomService.getAllRooms();
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/types")
    public ResponseEntity<ResponseDTO> getRoomTypes(){
        ResponseDTO responseDTO=roomService.getAllRoomTypes();
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ResponseDTO> getRoomById(@PathVariable("roomId") Long roomId){
        ResponseDTO responseDTO=roomService.getRoomById(roomId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/available-rooms-by-date-and-type")
    public ResponseEntity<ResponseDTO> getAvailableRooms(
            @RequestParam(value="checkInDate",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate checkInDate,

            @RequestParam(value = "checkOutDate",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate checkOutDate,

            @RequestParam(value = "roomType",required = false) String roomType
    ) {

        if(checkInDate==null || checkOutDate==null || roomType==null || roomType.isBlank()){
            ResponseDTO responseDTO=new ResponseDTO();
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage("Please provide check-in date, check-out date, and room type");
            return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
        }
        ResponseDTO responseDTO=roomService.getAvailableRoomsByDataAndType(checkInDate,checkOutDate,roomType);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);

    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateRoom(
            @PathVariable Long id,
            @RequestParam(value = "photo",required = false)MultipartFile photo,
            @RequestParam(value = "roomType",required = false) String roomType,
            @RequestParam(value = "roomPrice",required = false) BigDecimal roomPrice,
            @RequestParam(value = "description",required = false) String description
    ){
        ResponseDTO responseDTO=roomService.updateRoom(id, description,roomType,roomPrice, photo);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteRoom(@PathVariable Long id){
        ResponseDTO responseDTO=roomService.deleteRoom(id);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/all-available")
    public ResponseEntity<ResponseDTO> getAllAvailableRooms(){
        ResponseDTO responseDTO=roomService.getAllAvailableRooms();
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

}
