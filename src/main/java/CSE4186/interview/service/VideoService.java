package CSE4186.interview.service;

import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import CSE4186.interview.repository.UserRepository;
import CSE4186.interview.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public Long addVideo(VideoDto.createRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("no user"));

        Video video = Video.builder()
                .link(request.getLink())
                .title(request.getTitle())
                .user(findUser)
                .build();

        videoRepository.save(video);
        return video.getId();
    }
}
