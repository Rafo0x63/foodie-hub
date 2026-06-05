package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.UserDTO;

import java.util.List;

public interface UserQueryService {
    List<UserDTO> getAllUsers();
    UserDTO getUserByID(Long id);
}
