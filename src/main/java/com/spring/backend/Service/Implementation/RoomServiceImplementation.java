package com.spring.backend.Service.Implementation;

import com.spring.backend.DTO.ResponseDTO;
import com.spring.backend.DTO.RoomDTO;
import com.spring.backend.Entity.Room;
import com.spring.backend.Exception.RoomResourceNotFoundException;
import com.spring.backend.Mapper.RoomMapper;
import com.spring.backend.Repository.RoomRepository;
import com.spring.backend.Service.AwsS3Service;
import com.spring.backend.Service.Signature.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class RoomServiceImplementation implements RoomService {


    private final RoomRepository roomRepository;

//    private final BookingRepository bookingRepository;

    private final AwsS3Service awsS3Service;


    @Override
    public ResponseDTO addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room=new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomDescription(description);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);

            Room savedRoom=roomRepository.save(room);
            RoomDTO roomDTO= RoomMapper.mapToRoomDTO(savedRoom);
            responseDTO.setStatusCode(HttpStatus.CREATED.value());
            responseDTO.setMessage("Room created successfully");
            responseDTO.setRoom(roomDTO);

        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room creation:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getAllRoomTypes() {
        ResponseDTO responseDTO=new ResponseDTO();


        try{
            List<String> distinctRoomTypes=roomRepository.findDistinctByRoomType();
            responseDTO.setData(distinctRoomTypes);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setMessage("Room types retrieved successfully");
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room type retrieval:"+e.getMessage());
        }


        return responseDTO;
    }

    @Override
    public ResponseDTO getAllRooms() {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            List<Room> rooms=roomRepository.findAll();
            List<RoomDTO> roomDTOs=RoomMapper.mapRoomListToRoomDTOList(rooms);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setRoomList(roomDTOs);
            responseDTO.setMessage("Rooms retrieved successfully");
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room type retrieval:"+e.getMessage());
        }


        return responseDTO;

    }

    @Override
    public ResponseDTO deleteRoom(Long roomId) {

        ResponseDTO responseDTO=new ResponseDTO();

        try{
            roomRepository.findById(roomId).ifPresentOrElse(roomRepository::delete,()->{
                throw new RoomResourceNotFoundException("Room "+roomId+" does not exist");
            });

            responseDTO.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseDTO.setMessage("Room deleted successfully");
        }
        catch(RoomResourceNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room deletion:"+e.getMessage());
        }



        return responseDTO;
    }

    @Override
    public ResponseDTO updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            String imageUrl=null;
            if(photo!=null && !photo.isEmpty()){
                imageUrl = awsS3Service.saveImageToS3(photo);
            }

            Room room=roomRepository.findById(roomId).orElseThrow(
                    ()-> new RoomResourceNotFoundException("Room "+roomId+" does not exist")
            );

            if(roomType!=null){
                room.setRoomType(roomType);
            }

            if(roomPrice!=null){
                room.setRoomPrice(roomPrice);
            }

            if(description!=null){
                room.setRoomDescription(description);
            }

            if(imageUrl!=null){
                room.setRoomPhotoUrl(imageUrl);
            }

            RoomDTO roomDTO=RoomMapper.mapToRoomDTO(room);

            responseDTO.setStatusCode(HttpStatus.ACCEPTED.value());
            responseDTO.setMessage("Room updated successfully");
            responseDTO.setRoom(roomDTO);

        }
        catch (RoomResourceNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch(Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during updating:"+e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public ResponseDTO getRoomById(Long roomId) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            Room room=roomRepository.findById(roomId).orElseThrow(
                    ()->new RoomResourceNotFoundException("Room "+roomId+" does not exist")
            );

            RoomDTO roomDTO=RoomMapper.mapToRoomDTO(room);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setRoom(roomDTO);
            responseDTO.setMessage("Room retrieved successfully");
        }
        catch(RoomResourceNotFoundException e){
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room retrieval:"+e.getMessage());
        }



        return responseDTO;
    }

    @Override
    public ResponseDTO getAllAvailableRooms() {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            List<Room> roomList=roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOS=RoomMapper.mapRoomListToRoomDTOList(roomList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setRoomList(roomDTOS);
            responseDTO.setMessage("Available rooms retrieved successfully");
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room retrieval:"+e.getMessage());
        }


        return responseDTO;
    }

    @Override
    public ResponseDTO getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        ResponseDTO responseDTO=new ResponseDTO();

        try{
            List<Room> roomList=roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate,checkOutDate,roomType);
            List<RoomDTO> roomDTOS=RoomMapper.mapRoomListToRoomDTOList(roomList);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            responseDTO.setRoomList(roomDTOS);
            responseDTO.setMessage("Available rooms retrieved successfully");
        }
        catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setMessage("Error occurred during room retrieval:"+e.getMessage());
        }


        return responseDTO;
    }


}
