package CSE4186.interview.service;

import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.CommentRepository;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Comment addComment(CommentDto.createRequest request, Long postId) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("no user"));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("no post"));


        return commentRepository.save(Comment.builder()
                .user(findUser)
                .post(findPost)
                .content(request.getContent())
                .build());

    }
}
