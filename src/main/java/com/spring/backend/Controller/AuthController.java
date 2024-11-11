package com.spring.backend.Controller;


import com.spring.backend.DTO.LoginRequestDTO;
import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.Entity.User;
import com.spring.backend.Service.Signature.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody User user){
        ResponseDTO responseDTO=userService.register(user);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        ResponseDTO responseDTO=userService.login(loginRequestDTO);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }



}
