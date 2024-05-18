package CSE4186.interview.service;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.SelfIntroductionRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SelfIntroductionService {

    private final SelfIntroductionRepository selfIntroductionRepository;
    private final UserRepository userRepository;

    public List<SelfIntroduction> findAllSelfIntroductions(Long userId) {
        return selfIntroductionRepository.findAllByUser_Id(userId);
    }
    //유저아이디,
    public void save(Long id, String title, String content) {
        User user=userRepository.findById(id).orElseThrow();
        //System.out.println(title);
        selfIntroductionRepository.save(
                SelfIntroduction.builder()
                        .user(user)
                        .title(title)
                        .content(content)
                        .build()
        );
    }

    public String findSelfIntroductionById(Long selfIntroductionId) {
        Optional<SelfIntroduction> selfIntroduction = selfIntroductionRepository.findById(selfIntroductionId);
        if (selfIntroduction.isPresent()) {
            return selfIntroduction.get().getContent();
        } else {
            throw new NoSuchElementException("SelfIntroduction not found with id " + selfIntroductionId);
        }
    }
}
