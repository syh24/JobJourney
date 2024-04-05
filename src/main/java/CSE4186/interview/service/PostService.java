package CSE4186.interview.service;

import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Post addPost(PostDto.createRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("no user"));
        return postRepository.save(Post.builder()
                        .user(findUser)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build()
        );
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        postRepository.delete(post);
    }

    public void updatePost(Long id, PostDto.updateRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
        post.updatePost(request.getTitle(), request.getContent());
    }

    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }
}
