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
            if(roleRepository.findAll().isEmpty()){
                Role role1 = new Role();
                Role role2 = new Role();
                Role role3 = new Role();

                role1.setRoleName("ROLE_CHEF");
                role2.setRoleName("ROLE_ADMIN");
                role3.setRoleName("ROLE_SUPER_ADMIN");

                roleRepository.saveAll(List.of(role1, role2, role3));
            }
        };
    }
}
