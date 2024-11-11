package com.spring.backend.Service.Implementation;

import com.spring.backend.DTO.LoginRequestDTO;
import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.DTO.UserDTO;
import com.spring.backend.Entity.User;
import com.spring.backend.Exception.EmailConflictException;
import com.spring.backend.Exception.EmailResourceNotFoundException;
import com.spring.backend.Exception.UserNotFoundException;
import com.spring.backend.Mapper.UserMapper;
import com.spring.backend.Repository.BookingRepository;
import com.spring.backend.Repository.UserRepository;
import com.spring.backend.Service.Signature.UserService;
import com.spring.backend.Utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;

    @Override
    public ResponseDTO register(User user) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            if(user.getRole()==null || user.getRole().isBlank()){
                user.setRole("USER");
            }
            if(userRepository.existsByEmail(user.getEmail())){
                throw new EmailConflictException("User Email Already Exists :"+user.getEmail());
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser=userRepository.save(user);
            UserDTO userDTO= UserMapper.mapToUserDTO(savedUser);
            responseDTO.setStatusCode(HttpStatus.CREATED.value());
            responseDTO.setMessage("User Registered Successfully");
            responseDTO.setUser(userDTO);
        }
        catch(EmailConflictException e){
            responseDTO.setStatusCode(HttpStatus.CONFLICT.value());
            responseDTO.setMessage(e.getMessage());

        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during user registration:"+e.getMessage());
        }


        return responseDTO;
    }

    @Override
    public ResponseDTO login(LoginRequestDTO loginRequest) {

        ResponseDTO responseDTO = new ResponseDTO();

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            User user=userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                    ()->new EmailResourceNotFoundException("Could not find email: "+loginRequest.getEmail())
            );
            var jwt=jwtUtils.generateToken(user);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setToken(jwt);
            responseDTO.setRole(user.getRole());
            responseDTO.setExpirationTime("7 days");
            responseDTO.setMessage("Login successful");
        }
        catch(EmailResourceNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during user login:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getAllUsers() {
        ResponseDTO responseDTO = new ResponseDTO();

        try{
            List<User> users=userRepository.findAll();
            List<UserDTO> userDTOList=UserMapper.mapUserListToUserListDTO(users);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUserList(userDTOList);
            responseDTO.setMessage("Users retrieved successfully");
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during users retrieval:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getUserBookingHistory(Long userId) {
        ResponseDTO responseDTO = new ResponseDTO();

        try{
            User user=userRepository.findById(userId).orElseThrow(
                    ()-> new UserNotFoundException("User not found")
            );
            UserDTO userDTO=UserMapper.mapToUserDTOPlusBookingsAndRoom(user);

            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDTO);
            responseDTO.setMessage("Booking History retrieved successfully");

        }
        catch(UserNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred in history retrieval:"+e.getMessage());
        }


        return responseDTO;
    }

    @Override
    public ResponseDTO deleteUser(String userId) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            userRepository.findById(Long.valueOf(userId)).ifPresentOrElse(userRepository::delete,()->{
                throw new UserNotFoundException("User not found with id: "+userId);
            });
            responseDTO.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseDTO.setMessage("User deleted successfully");
        }
        catch(UserNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during user deletion:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getUserById(String userId) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            User user=userRepository.findById(Long.valueOf(userId)).orElseThrow(
                    ()-> new UserNotFoundException("User not found with id: "+userId)
            );
            UserDTO userDTO=UserMapper.mapToUserDTO(user);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDTO);
            responseDTO.setMessage("User retrieved successfully");

        }
        catch(UserNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during user retrieval:"+e.getMessage());
        }


        return responseDTO;
    }

    @Override
    public ResponseDTO getMyInfo(String email) {

        ResponseDTO responseDTO=new ResponseDTO();

        try{
            User user=userRepository.findByEmail(email).orElseThrow(
                    ()->new UserNotFoundException("User not found")
            );
            UserDTO userDTO=UserMapper.mapToUserDTO(user);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setUser(userDTO);
            responseDTO.setMessage("User retrieved successfully");
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during user retrieval:"+e.getMessage());
        }

        return responseDTO;
    }
}
