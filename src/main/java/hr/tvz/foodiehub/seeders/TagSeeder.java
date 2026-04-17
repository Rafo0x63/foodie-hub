package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Tag;
import hr.tvz.foodiehub.repositories.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class TagSeeder {
    @Bean
    @Order(2)
    CommandLineRunner seedTags(TagRepository tagRepository) {
        return args -> {
          if (tagRepository.findAll().isEmpty()) {
              List<Tag> tags = List.of(
                      new Tag(null, "Desert"),
                      new Tag(null, "Predjelo"),
                      new Tag(null, "Večera"),
                      new Tag(null, "Salata"),
                      new Tag(null, "Azijsko"),
                      new Tag(null, "Vegansko"),
                      new Tag(null, "Vegetarijansko")
              );
              tagRepository.saveAll(tags);
          }
        };
    }
}
