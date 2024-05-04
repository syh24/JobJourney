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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "Get All Posts", description = "모든 게시글을 조회")
    public ResponseEntity<BaseResponseDto<List<PostDto.Response>>> getAllPosts(
            @PageableDefault(page = 1, size = 10) Pageable pageable,
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "searchBy", defaultValue = "") String searchBy
    ) {
        try {
            List<PostDto.Response> response = postService.findPostsByCondition(pageable, q, searchBy)
                    .stream().map(PostDto.Response::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(
                    new BaseResponseDto<List<PostDto.Response>>(
                            "success",
                            "",
                            response
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get Post", description = "게시글 상세")
    public ResponseEntity<BaseResponseDto<PostDto.Response>> getPost(@PathVariable(name = "id") Long id) {
        try {
            Post post = postService.findPost(id);
            return ResponseEntity.ok(
                    new BaseResponseDto<PostDto.Response>(
                            "success",
                            "",
                            new PostDto.Response(post)
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @PostMapping
    @Operation(summary = "Add Post", description = "게시글 생성")
    public ResponseEntity<BaseResponseDto<PostDto.Response>> addPost(@RequestBody PostDto.createRequest request) {
        try {
            Post post = postService.addPost(request);
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            new PostDto.Response(post)
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Post", description = "게시글 수정")
    public ResponseEntity<BaseResponseDto<PostDto.updateResponse>> updatePost(@PathVariable Long id, @RequestBody PostDto.updateRequest request) {
        try {
            postService.updatePost(id, request);
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            new PostDto.updateResponse(id)
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Post", description = "게시글 삭제")
    public ResponseEntity<BaseResponseDto<String>> delete(@PathVariable(name = "id") Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            ""
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Add Comment", description = "댓글 생성")
    public ResponseEntity<BaseResponseDto<CommentDto.Response>> addPost(@RequestBody CommentDto.createRequest request,
                                                                        @PathVariable(name = "id") Long id) {
        try {
            Comment comment = commentService.addComment(request, id);
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            new CommentDto.Response(comment)
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @PutMapping("/{id}/comment")
    @Operation(summary = "Update Comment", description = "댓글 수정")
    public ResponseEntity<BaseResponseDto<CommentDto.updateResponse>> updateComment(@RequestBody CommentDto.updateRequest request) {
        try {
            commentService.updateComment(request);
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            new CommentDto.updateResponse(request.getId())
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}/comment")
    @Operation(summary = "Delete Comment", description = "댓글 삭제")
    public ResponseEntity<BaseResponseDto<String>> deleteComment(@RequestBody CommentDto.deleteRequest request) {
        try {
            commentService.deleteComment(request.getId());
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            ""
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponseDto.fail(e.getMessage())
            );
        }
    }


}
