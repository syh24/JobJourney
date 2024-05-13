package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.DislikeDto;
import CSE4186.interview.controller.dto.LikesDto;
import CSE4186.interview.service.DislikeService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/{id}/dislike")
@Tag(name = "Dislike", description = "Dislike API")
public class DislikeController {

    private final DislikeService dislikeService;

    @PostMapping
    public ApiUtil.ApiSuccessResult<String> addDislike(
            @Valid @RequestBody DislikeDto.creteRequest request,
            @PathVariable(name = "id") Long postId
    ) {
        dislikeService.addDislike(request, postId);
        return ApiUtil.success("싫어요");
    }
}
