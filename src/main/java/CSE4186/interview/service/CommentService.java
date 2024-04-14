package CSE4186.interview.service;

import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
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

import static CSE4186.interview.controller.dto.CommentDto.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Comment addComment(createRequest request, Long postId) {
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

    public void updateComment(updateRequest request) {
        Comment comment = commentRepository.findById(request.getId()).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + request.getId()));

        comment.updateComment(request.getContent());
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + id));

        commentRepository.delete(comment);
    }
}
