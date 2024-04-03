package CSE4186.interview.repository;

import CSE4186.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    <Optional>User findByEmail(String email);
}
