package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.services.interfaces.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {this.userService = userService;}

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        System.out.println("hey hey hey");
        return userService.getAllUsers();
    }
}
