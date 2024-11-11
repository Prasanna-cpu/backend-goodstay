package com.spring.backend.Mapper;

import com.spring.backend.DTO.BookingDTO;
import com.spring.backend.DTO.RoomDTO;
import com.spring.backend.Entity.Room;

import java.util.List;

public class RoomMapper {
    public static RoomDTO mapToRoomDTO(Room room){
        RoomDTO roomDTO=new RoomDTO();

        roomDTO.setId(room.getId());
        roomDTO.setRoomDescription(room.getRoomDescription());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPhotoUrl(room.getRoomPhotoUrl());

        return roomDTO;

    }

    public static Room mapToRoom(RoomDTO roomDTO){
        Room room=new Room();

        room.setId(roomDTO.getId());
        room.setRoomDescription(roomDTO.getRoomDescription());
        room.setRoomPrice(roomDTO.getRoomPrice());
        room.setRoomType(roomDTO.getRoomType());
        room.setRoomPhotoUrl(roomDTO.getRoomPhotoUrl());

        return room;
    }

    public static List<RoomDTO> mapRoomListToRoomDTOList(List<Room> rooms){
        return rooms.stream()
                .map(RoomMapper::mapToRoomDTO)
                .toList();
    }

    public static RoomDTO mapToRoomDTOPlusBookings(Room room){
        RoomDTO roomDTO=new RoomDTO();

        roomDTO.setId(room.getId());
        roomDTO.setRoomDescription(room.getRoomDescription());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPhotoUrl(room.getRoomPhotoUrl());

        if(room.getBookings()!=null){
            roomDTO.setBookings(
                    room.getBookings().stream()
                            .map(BookingMapper::mapToBookingDTO).toList()
            );
        }

        return roomDTO;
    }

}
