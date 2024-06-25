package CSE4186.interview.service;

import CSE4186.interview.entity.JobField;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("조건으로 검색")
    void findPostsByCondition() {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");

        Post post1 = new Post(1L, "title", "content", user, jobField);
        Post post2 = new Post(2L, "title2", "content2", user, jobField);

        List<Post> postList = List.of(post1,post2);
        Page<Post> postPage = new PageImpl<>(postList);

        given(postRepository.findPostsBySearchCondition(Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .willReturn(postPage);

        Page<Post> result = postService.findPostsByCondition(PageRequest.of(1, 10), "test", "title");

        List<Post> content = result.getContent();

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(content.get(0).getTitle()).isEqualTo("title");
        assertThat(content.get(1).getTitle()).isEqualTo("title2");
    }

    @Test
    @DisplayName("전체 검색")
    void findAll() {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");

        Post post1 = new Post(1L, "title", "content", user, jobField);
        Post post2 = new Post(2L, "title2", "content2", user, jobField);

        List<Post> postList = List.of(post1,post2);
        Page<Post> postPage = new PageImpl<>(postList);

        given(postRepository.findAll(Mockito.any(Pageable.class))).willReturn(postPage);

        Page<Post> result = postService.findPostsByCondition(PageRequest.of(1, 10), "", "");

        List<Post> content = result.getContent();

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(content.get(0).getTitle()).isEqualTo("title");
        assertThat(content.get(1).getTitle()).isEqualTo("title2");
    }
}