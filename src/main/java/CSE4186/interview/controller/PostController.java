package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.service.CommentService;
import CSE4186.interview.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "Post API")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/list")
    @Operation(summary = "Get All Posts", description = "모든 게시글을 조회한다.")
    public ResponseEntity<BaseResponseDto<List<PostDto.Response>>> getAllPosts() {
        List<PostDto.Response> response = postService.findAllPosts()
                .stream().map(PostDto.Response::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                new BaseResponseDto<List<PostDto.Response>>(
                "success",
                "",
                response
        ));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get Post", description = "게시글 상세")
    public ResponseEntity<BaseResponseDto<PostDto.Response>> getPost(@PathVariable(name = "id") Long id) {
        Post post = postService.findPost(id);
        return ResponseEntity.ok(
                new BaseResponseDto<PostDto.Response>(
                        "success",
                        "",
                new PostDto.Response(post)
        ));
    }

    @PostMapping
    @Operation(summary = "Add Post", description = "게시글 생성")
    public ResponseEntity<BaseResponseDto<PostDto.Response>> addPost(@RequestBody PostDto.createRequest request) {
        Post post = postService.addPost(request);
        return ResponseEntity.ok(
                new BaseResponseDto<PostDto.Response>(
                        "success",
                        "",
                        new PostDto.Response(post)
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Post", description = "게시글 수정")
    public ResponseEntity<BaseResponseDto<Long>> updatePost(@PathVariable Long id, @RequestBody PostDto.updateRequest request) {
        postService.updatePost(id, request);
        return ResponseEntity.ok(
                new BaseResponseDto<Long>(
                        "success",
                        "",
                        id
                ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Post", description = "게시글 삭제")
    public ResponseEntity<BaseResponseDto<Long>> delete(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(
                new BaseResponseDto<Long>(
                        "success",
                        "",
                        id
                ));
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Add Comment", description = "댓글 생성")
    public ResponseEntity<BaseResponseDto<CommentDto.Response>> addPost(@RequestBody CommentDto.createRequest request,
                                                       @PathVariable(name = "id") Long id) {
        Comment comment= commentService.addComment(request, id);
        return ResponseEntity.ok(
            new BaseResponseDto<CommentDto.Response>(
                    "success",
                    "",
                    new CommentDto.Response(comment)
            ));
    }

}
