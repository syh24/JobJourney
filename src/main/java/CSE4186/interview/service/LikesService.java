package CSE4186.interview.service;

import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.entity.Likes;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.DislikeRepository;
import CSE4186.interview.repository.LikesRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

    private final DislikeRepository dislikeRepository;
    private final LikesRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public void addLike(LikesDto.creteRequest request, Long postId) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다."));

        Optional<Likes> findLike = likeRepository.findAllByPostAndUser(post.getId(), findUser.getId());

        if (findLike.isPresent()) {
            likeRepository.delete(findLike.get());
            post.subLikeCount();
        } else {
            dislikeRepository.findAllByPostAndUser(post.getId(), findUser.getId()).ifPresent(d -> {
                throw new IllegalStateException("이미 싫어요를 눌렀습니다.");
            });
            likeRepository.save(Likes.builder()
                    .user(findUser)
                    .post(post)
                    .build());
            post.addLikeCount();
        }
    }

}
