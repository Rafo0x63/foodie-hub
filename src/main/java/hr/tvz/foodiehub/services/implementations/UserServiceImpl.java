package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateUserRequest;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){ this.userRepository = userRepository; }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findByDeletedAtIsNull()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO getUserByID(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        return mapToDTO(user);
    }

    @Override
    public void deleteUserById(Long id) {
       User user = userRepository.findByIdAndDeletedAtIsNull(id)
               .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

       user.setDeletedAt(LocalDateTime.now());
       userRepository.save(user);
    }

    @Override
    public UserDTO createNewUser(CreateUserRequest createUserRequest) {
        User newUser = new User();
        newUser.setName(createUserRequest.name());
        newUser.setEmail(createUserRequest.email());
        newUser.setDeletedAt(null);

        User user = userRepository.save(newUser);
        return mapToDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, CreateUserRequest createUserRequest) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(createUserRequest.name());
        user.setEmail(createUserRequest.email());

        userRepository.save(user);
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().get(0).getRoleName()
        );
    }
}
