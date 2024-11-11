package com.spring.backend.Controller;


import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Service.Signature.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;



    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUsers(){
        ResponseDTO responseDTO=userService.getAllUsers();
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }


    @GetMapping("/get/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("userId") String userId){
        ResponseDTO responseDTO=userService.getUserById(userId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("userId")String userId){
        ResponseDTO responseDTO=userService.deleteUser(userId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/logged-in-user")
    public ResponseEntity<ResponseDTO> getLoggedInUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        ResponseDTO responseDTO=userService.getMyInfo(email);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/get-user-bookings/{userId}")
    public ResponseEntity<ResponseDTO> getUserBookingHistory(@PathVariable("userId") Long userId) {
        ResponseDTO response = userService.getUserBookingHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }




}
