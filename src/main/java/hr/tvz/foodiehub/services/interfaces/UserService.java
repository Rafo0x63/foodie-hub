package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateUserRequest;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserByID(Long id);
    void deleteUserById(Long id);
    UserDTO createNewUser(CreateUserRequest createUserRequests);
    UserDTO updateUser(Long id, CreateUserRequest createUserRequests);
}
