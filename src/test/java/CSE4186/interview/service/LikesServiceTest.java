package CSE4186.interview.service;

import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.entity.*;
import CSE4186.interview.repository.DislikeRepository;
import CSE4186.interview.repository.LikesRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LikesServiceTest {

    @InjectMocks
    private LikesService likesService;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private DislikeRepository dislikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("좋아요 성공 테스트")
    void successLike() {
        User user = new User(1L, "syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");
        Post post = new Post(1L, "title", "content", user, jobField);
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());
        given(dislikeRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());

        String result = likesService.addLike(request, 1L);
        assertThat(result).isEqualTo("좋아요 성공");
    }

    @Test
    @DisplayName("좋아요 실패 테스트 (싫어요 누름)")
    void failLikeByDisLike() {
        User user = new User(1L, "syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");
        Post post = new Post(1L, "title", "content", user, jobField);
        Dislike dislike = new Dislike(1L, user, post);
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.empty());
        given(dislikeRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(dislike));

        assertThrows(IllegalStateException.class, () -> likesService.addLike(request, 1L));
    }

    @Test
    @DisplayName("좋아요 실패 테스트")
    void failLike() {
        User user = new User(1L, "syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");
        Post post = new Post(1L, "title", "content", user, jobField);
        Likes like = new Likes(1L, user, post);
        LikesDto.CreteRequest request = new LikesDto.CreteRequest(1L);

        given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.of(post));
        given(likesRepository.findAllByPostAndUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(like));

        String result = likesService.addLike(request, 1L);
        assertThat(result).isEqualTo("좋아요 취소");
    }
}