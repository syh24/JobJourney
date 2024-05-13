package CSE4186.interview.service;

import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.UserRepository;
import CSE4186.interview.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public Long addVideo(VideoDto.createRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Video video = Video.builder()
                .link(request.getLink())
                .title(request.getTitle())
                .user(findUser)
                .build();

        videoRepository.save(video);
        return video.getId();
    }

    public Page<Video> findAllVideo(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        return videoRepository.findAll(PageRequest.of(page, pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
