package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateUserRequest;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - unit tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setDeletedAt(null);
        user.setRoles(List.of(role));
    }

    @Test
    @DisplayName("getAllUsers returns all non-deleted users as DTOs")
    void getAllUsers_returnsUserDTOList() {
        when(userRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(user));

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getName()).isEqualTo("Test User");
        assertThat(result.getFirst().getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirst().getRole()).isEqualTo("ROLE_USER");

        verify(userRepository).findByDeletedAtIsNull();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getUserByID returns user when user exists")
    void getUserByID_existingUser_returnsUserDTO() {
        when(userRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByID(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");

        verify(userRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("getUserByID throws exception when user does not exist")
    void getUserByID_nonExistingUser_throwsException() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByID(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recipe not found with id: 99");

        verify(userRepository).findByIdAndDeletedAtIsNull(99L);
    }

    @Test
    @DisplayName("deleteUserById soft deletes existing user")
    void deleteUserById_existingUser_setsDeletedAtAndSaves() {
        when(userRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        assertThat(user.getDeletedAt()).isNotNull();

        verify(userRepository).findByIdAndDeletedAtIsNull(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deleteUserById throws exception when user does not exist")
    void deleteUserById_nonExistingUser_throwsException() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 99");

        verify(userRepository).findByIdAndDeletedAtIsNull(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createNewUser saves user and returns DTO")
    void createNewUser_validRequest_savesAndReturnsUserDTO() {
        CreateUserRequest request = new CreateUserRequest(
                "New User",
                "new@example.com"
        );

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_USER");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setName("New User");
        savedUser.setEmail("new@example.com");
        savedUser.setDeletedAt(null);
        savedUser.setRoles(List.of(role));

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserDTO result = userService.createNewUser(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getName()).isEqualTo("New User");
        assertThat(capturedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(capturedUser.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("updateUser updates name and email")
    void updateUser_existingUser_updatesAndReturnsUserDTO() {
        CreateUserRequest request = new CreateUserRequest(
                "Updated User",
                "updated@example.com"
        );

        when(userRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        UserDTO result = userService.updateUser(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated User");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");

        verify(userRepository).findByIdAndDeletedAtIsNull(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateUser throws exception when user does not exist")
    void updateUser_nonExistingUser_throwsException() {
        CreateUserRequest request = new CreateUserRequest(
                "Updated User",
                "updated@example.com"
        );

        when(userRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 99");

        verify(userRepository).findByIdAndDeletedAtIsNull(99L);
        verify(userRepository, never()).save(any(User.class));
    }
}
