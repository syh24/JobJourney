package CSE4186.interview.repository;

import CSE4186.interview.entity.User;

import java.util.Optional;

public interface CustomUserRepository {
    abstract Optional<User> findByNameAndPassword(String name, String password);
    abstract  Optional<User> findByEmail(String email);
}
