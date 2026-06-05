package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateUserRequest;

public interface UserCommandService {
    void deleteUserById(Long id);
    UserDTO createNewUser(CreateUserRequest createUserRequests);
    UserDTO updateUser(Long id, CreateUserRequest createUserRequests);
}
