package com.spring.backend.Service.Signature;

import com.spring.backend.DTO.LoginRequestDTO;
import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Entity.User;

public interface UserService {


    ResponseDTO register(User user);

    ResponseDTO login(LoginRequestDTO loginRequest);

    ResponseDTO getAllUsers();

    ResponseDTO getUserBookingHistory(Long userId);

    ResponseDTO deleteUser(String userId);

    ResponseDTO getUserById(String userId);

    ResponseDTO getMyInfo(String email);


}
