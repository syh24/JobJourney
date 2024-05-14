package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.Video;
import CSE4186.interview.service.VideoService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@Tag(name = "Video", description = "Video API")
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @Operation(summary = "Add Video", description = "비디오 생성")
    public ApiUtil.ApiSuccessResult<Long> addVideo(@Valid @RequestBody VideoDto.createRequest request) {
        Long videoId = videoService.addVideo(request);
        return ApiUtil.success(videoId);
    }

    @GetMapping("/list")
    @Operation(summary = "Get All Videos", description = "모든 비디오를 조회")
    public ApiUtil.ApiSuccessResult<VideoDto.videoListResponse> getAllVideo(@PageableDefault(page = 1, size = 10) Pageable pageable) {
        Page<Video> findVideos = videoService.findAllVideo(pageable);
        List<VideoDto.Response> videoList = findVideos.stream().map(VideoDto.Response::new).toList();
        return ApiUtil.success(new VideoDto.videoListResponse(videoList, findVideos.getTotalPages()));
    }
}
