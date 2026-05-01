package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class RoleSeeder {
    @Bean
    @Order(1)
    CommandLineRunner seedRoles(RoleRepository roleRepository){
        return args -> {
            for (String roleName : List.of("ROLE_USER", "ROLE_CHEF", "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
                if (roleRepository.findByRoleName(roleName).isEmpty()) {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    roleRepository.save(role);
                }
            }
        };
    }
}
