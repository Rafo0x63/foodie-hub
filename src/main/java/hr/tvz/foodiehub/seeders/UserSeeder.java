package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.RoleRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class UserSeeder {
    @Bean
    @Order(3)
    CommandLineRunner seedUsers(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            if (userRepository.findByDeletedAtIsNull().isEmpty()){
                Role chefRole = roleRepository.findByRoleName("ROLE_CHEF").orElse(null);
                Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN").orElse(null);
                Role superAdminRole = roleRepository.findByRoleName("ROLE_SUPER_ADMIN").orElse(null);

                if(chefRole == null || adminRole == null || superAdminRole == null){
                    return;
                }

                User user1 = new User();
                user1.setName("Ivan Horvat");
                user1.setEmail("ivan@gmail.com");
                user1.setDeletedAt(null);
                user1.setRoles(List.of(chefRole));

                User user2 = new User();
                user2.setName("Ana Marić");
                user2.setEmail("ana@gmail.com");
                user2.setDeletedAt(null);
                user2.setRoles(List.of(adminRole, chefRole));

                User user3 = new User();
                user3.setName("Marko Kovač");
                user3.setEmail("marko@gmail.com");
                user3.setDeletedAt(null);
                user3.setRoles(List.of(adminRole));

                User user4 = new User();
                user4.setName("Petra Novak");
                user4.setEmail("petra@gmail.com");
                user4.setDeletedAt(null);
                user4.setRoles(List.of(superAdminRole, chefRole, adminRole));

                userRepository.saveAll(List.of(user1, user2, user3, user4));

            }
        };
    }
}
