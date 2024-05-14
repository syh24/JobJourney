package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.service.LikesService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/{id}/like")
@Tag(name = "Likes", description = "Likes API")
public class LikesController {

    private final LikesService likeService;

    @PostMapping
    public ApiUtil.ApiSuccessResult<String> addLike(
            @Valid @RequestBody LikesDto.creteRequest request,
            @PathVariable(name = "id") Long postId
    ) {
        likeService.addLike(request, postId);
        return ApiUtil.success("좋아요");
    }
}
