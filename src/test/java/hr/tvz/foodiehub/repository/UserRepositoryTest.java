package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByDeletedAtIsNull vraca samo neobrisane usere")
    void shouldFindByDeletedAtIsNull() {
        User activeUser = createUser("Active User", uniqueEmail("active"), null);
        User deletedUser = createUser("Deleted User", uniqueEmail("deleted"), LocalDateTime.now().minusDays(1));

        userRepository.saveAllAndFlush(List.of(activeUser, deletedUser));

        List<User> users = userRepository.findByDeletedAtIsNull();

        assertThat(users)
                .extracting(User::getId)
                .contains(activeUser.getId())
                .doesNotContain(deletedUser.getId());

        assertThat(users)
                .allMatch(user -> user.getDeletedAt() == null);
    }

    @Test
    @DisplayName("findByIdAndDeletedAtIsNull vraca usera kada nije obrisan")
    void shouldFindByIdAndDeletedAtIsNull() {
        User activeUser = userRepository.saveAndFlush(createUser("Active By Id", uniqueEmail("active-id"), null));
        User deletedUser = userRepository.saveAndFlush(createUser("Deleted By Id", uniqueEmail("deleted-id"), LocalDateTime.now().minusDays(1)));

        Optional<User> activeResult = userRepository.findByIdAndDeletedAtIsNull(activeUser.getId());
        Optional<User> deletedResult = userRepository.findByIdAndDeletedAtIsNull(deletedUser.getId());

        assertThat(activeResult).isPresent();
        assertThat(activeResult.get().getEmail()).isEqualTo(activeUser.getEmail());
        assertThat(deletedResult).isEmpty();
    }

    @Test
    @DisplayName("findByEmailAndDeletedAtIsNull vraca usera kada email pripada neobrisanom useru")
    void shouldFindByEmailAndDeletedAtIsNull() {
        User activeUser = userRepository.saveAndFlush(createUser("Active By Email", uniqueEmail("active-email"), null));
        User deletedUser = userRepository.saveAndFlush(createUser("Deleted By Email", uniqueEmail("deleted-email"), LocalDateTime.now().minusDays(1)));

        Optional<User> activeResult = userRepository.findByEmailAndDeletedAtIsNull(activeUser.getEmail());
        Optional<User> deletedResult = userRepository.findByEmailAndDeletedAtIsNull(deletedUser.getEmail());

        assertThat(activeResult).isPresent();
        assertThat(activeResult.get().getId()).isEqualTo(activeUser.getId());
        assertThat(deletedResult).isEmpty();
    }

    private User createUser(String name, String email, LocalDateTime deletedAt) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        user.setDeletedAt(deletedAt);

        return user;
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@example.com";
    }
}
