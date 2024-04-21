package CSE4186.interview.repository;

import CSE4186.interview.entity.SelfIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelfIntroductionRepository extends JpaRepository<SelfIntroduction,Long> {
    List<SelfIntroduction> findAllByUser_Id(Long UserId);
}
