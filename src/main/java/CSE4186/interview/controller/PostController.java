package CSE4186.interview.controller;

import CSE4186.interview.annotation.LoginUser;
import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.service.CommentService;
import CSE4186.interview.service.PostService;
import CSE4186.interview.service.UserService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.User;
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
    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "Get All Posts", description = "모든 게시글을 조회")
    public ApiUtil.ApiSuccessResult<PostDto.postListResponse> getAllPosts(
            @PageableDefault(page = 1, size = 10) Pageable pageable,
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "searchBy", defaultValue = "") String searchBy
    ) {

        Page<Post> postsByCondition = postService.findPostsByCondition(pageable, q, searchBy);
        List<PostDto.Response> response = postsByCondition
                .stream().map(PostDto.Response::new)
                .toList();

        return ApiUtil.success(new PostDto.postListResponse(response, postsByCondition.getTotalPages()));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get Post", description = "게시글 상세")
    public ApiUtil.ApiSuccessResult<PostDto.Response> getPost(
            @PathVariable(name = "id") Long id,
            @LoginUser User loginUser
    ) {
        Long userId = Long.valueOf(loginUser.getUsername());
        Post post = postService.findPost(id);
        String checkLikeOrDislike = userService.checkLikeOrDislike(id, userId);

        return ApiUtil.success(new PostDto.Response(post, checkLikeOrDislike));
    }

    @PostMapping
    @Operation(summary = "Add Post", description = "게시글 생성")
    public ApiUtil.ApiSuccessResult<PostDto.Response> addPost(@Valid @RequestBody PostDto.createRequest request) {
        Post post = postService.addPost(request);
        return ApiUtil.success(new PostDto.Response(post));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Post", description = "게시글 수정")
    public ApiUtil.ApiSuccessResult<PostDto.updateResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto.updateRequest request) {
        postService.updatePost(id, request);
        return ApiUtil.success(new PostDto.updateResponse(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Post", description = "게시글 삭제")
    public ApiUtil.ApiSuccessResult<String> delete(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return ApiUtil.success("게시글이 삭제되었습니다.");
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Add Comment", description = "댓글 생성")
    public ApiUtil.ApiSuccessResult<CommentDto.Response> addPost(@Valid @RequestBody CommentDto.createRequest request,
                                                                 @PathVariable(name = "id") Long id) {
        Comment comment = commentService.addComment(request, id);
        return ApiUtil.success(new CommentDto.Response(comment));
    }

    @PutMapping("/{id}/comment")
    @Operation(summary = "Update Comment", description = "댓글 수정")
    public ApiUtil.ApiSuccessResult<CommentDto.updateResponse> updateComment(@Valid @RequestBody CommentDto.updateRequest request) {
        commentService.updateComment(request);
        return ApiUtil.success(new CommentDto.updateResponse(request.getId()));
    }

    @DeleteMapping("/{id}/comment")
    @Operation(summary = "Delete Comment", description = "댓글 삭제")
    public ApiUtil.ApiSuccessResult<String> deleteComment(@Valid @RequestBody CommentDto.deleteRequest request) {
        commentService.deleteComment(request.getId());
        return ApiUtil.success("댓글이 삭제되었습니다.");
    }
}
