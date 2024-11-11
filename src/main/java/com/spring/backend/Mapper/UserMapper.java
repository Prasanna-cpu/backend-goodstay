package com.spring.backend.Mapper;

import com.spring.backend.DTO.UserDTO;
import com.spring.backend.Entity.User;

import java.util.List;

public class UserMapper {


    public static UserDTO mapToUserDTO(User user){

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());

        return userDTO;

    }

    public static User mapToUser(UserDTO userDTO){

        User user = new User();

        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());

        return user;
    }

    public static UserDTO mapToUserDTOPlusBookingsAndRoom(User user){
        UserDTO userDTO = mapToUserDTO(user);

        if(!user.getBookings().isEmpty()){
            userDTO.setBookings(
                    user.getBookings()
                            .stream()
                            .map(booking -> BookingMapper.mapToBookingDTOPlusBookedRooms(booking,false))
                            .toList()
            );
        }
        return userDTO;
    }

    public static List<UserDTO> mapUserListToUserListDTO(List<User> userList){
        return userList.stream().map(UserMapper::mapToUserDTO).toList();
    }


}
