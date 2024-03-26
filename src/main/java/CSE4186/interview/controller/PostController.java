package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Post;
import CSE4186.interview.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    public ResponseEntity<List<PostDto.Response>> getAllPosts() {
        List<PostDto.Response> response = postService.findAllPosts()
                .stream().map(PostDto.Response::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostDto.Response> addPost(@RequestBody PostDto.Request request) {
        Post post = postService.addPost(request);
        return ResponseEntity.ok(new PostDto.Response(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePost(@PathVariable Long id, @RequestBody PostDto.Request request) {
        postService.updatePost(id, request);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(id);
    }

}
