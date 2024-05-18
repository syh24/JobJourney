package CSE4186.interview.service;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.SelfIntroductionRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SelfIntroductionService {

    private final SelfIntroductionRepository selfIntroductionRepository;
    private final UserRepository userRepository;

    public Page<SelfIntroduction> findAllSelfIntroductions(Pageable pageable, Long userId) {
        int page = pageable.getPageNumber() - 1;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        return selfIntroductionRepository.findAllByUser(PageRequest.of(page, pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")), user);
    }

    public SelfIntroduction save(Long id, String title, String content) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        return selfIntroductionRepository.save(
                SelfIntroduction.builder()
                        .user(user)
                        .title(title)
                        .content(content)
                        .build());
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
