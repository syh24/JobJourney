package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.service.VideoService;
import CSE4186.interview.utils.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ApiUtil.ApiSuccessResult<Long> addVideo(@Valid @RequestBody VideoDto.createRequest request) {
        Long videoId = videoService.addVideo(request);
        return ApiUtil.success(videoId);
    }
}
