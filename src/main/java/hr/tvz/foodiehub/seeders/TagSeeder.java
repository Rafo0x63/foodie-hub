package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Tag;
import hr.tvz.foodiehub.repositories.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TagSeeder {
    @Bean
    CommandLineRunner seedTags(TagRepository tagRepository) {
        return args -> {
          if (tagRepository.findAll().isEmpty()) {
              List<Tag> tags = List.of(
                      new Tag(null, "Dessert"),
                      new Tag(null, "Appetizer"),
                      new Tag(null, "Dinner"),
                      new Tag(null, "Salad"),
                      new Tag(null, "Asian"),
                      new Tag(null, "Vegan"),
                      new Tag(null, "Vegetarian")
              );
              tagRepository.saveAll(tags);
          }
        };
    }
}
