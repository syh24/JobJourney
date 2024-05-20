package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.PostVideo;
import CSE4186.interview.entity.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class PostVideoDto {

    @Getter
    @Schema(name = "postVideoResponse", description = "게시글 비디오 응답 DTO")
    public static class Response {
        private final VideoDto.Response video;

        public Response(PostVideo postVideo) {
            this.video = new VideoDto.Response(postVideo.getVideo());
        }
    }
}
