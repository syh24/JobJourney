package CSE4186.interview.service;

import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.PostVideo;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.PostRepository;
import CSE4186.interview.repository.PostVideoRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final PostVideoRepository postVideoRepository;

    @Transactional
    public Page<Post> findPostsByCondition(Pageable pageable, String q, String condition) {
        int page = pageable.getPageNumber() - 1;

        if(!q.isEmpty() && !condition.isEmpty()) {
            return postRepository.findPostsBySearchCondition(q, condition, PageRequest.of(page, pageable.getPageSize()));
        }

        return postRepository.findAll(PageRequest.of(page, pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public Post addPost(PostDto.createRequest request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        Post post = postRepository.save(Post.builder()
                .user(findUser)
                .title(request.getTitle())
                .content(request.getContent())
                .build());

        for (Long videoId : request.getVideoIdList()) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new NotFoundException("해당 비디오가 존재하지 않습니다."));

            postVideoRepository.save(PostVideo.builder()
                    .video(video)
                    .post(post)
                    .build());
        }

        return post;
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));

        postRepository.delete(post);
    }

    @Transactional
    public void updatePost(Long id, PostDto.updateRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));
        post.updatePost(request.getTitle(), request.getContent());
        postVideoRepository.deleteByPost(post);

        for (Long videoId : request.getVideoIdList()) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new NotFoundException("해당 비디오가 존재하지 않습니다."));

            postVideoRepository.save(PostVideo.builder()
                    .video(video)
                    .post(post)
                    .build());
        }
    }

    public Post findPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + id));
        post.addViewCount();
        return post;
    }
}
