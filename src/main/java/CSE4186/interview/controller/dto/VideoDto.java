package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

public class VideoDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "videoCreateRequest", description = "비디오 생성 DTO")
    public static class createRequest {
        private String title;
        private String link;
        @NotBlank
        private Long userId;
    }

    @RequiredArgsConstructor
    @Data
    @Schema(name = "videoResponse", description = "비디오 응답 DTO")
    public static class Response {
        private final Long id;
        private final String title;
        private final String link;
        private final Long userId;
        private final String createdAt;
        private final String updatedAt;

        public Response(Video video) {
            this.id = video.getId();
            this.title = video.getTitle();
            this.link = video.getLink();
            this.userId = video.getUser().getId();
            this.createdAt = video.getCreatedAt();
            this.updatedAt = video.getUpdatedAt();
        }
    }

    @Data
    @AllArgsConstructor
    @Schema(name = "videoListResponse", description = "비디오 전체 list 응답 DTO")
    public static class videoListResponse {
        private List<Response> list;
        private int pageCount;
    }
}
