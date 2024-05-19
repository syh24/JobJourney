package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.PostVideo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "postCreateRequest", description = "게시글 생성 DTO")
    public static class createRequest {
        @NotNull
        private String title;
        @NotNull
        private String content;
        @NotNull
        private Long userId;
        @NotNull
        private List<Long> videoIdList;
    }

    @Data
    @AllArgsConstructor
    @Schema(name = "postUpdateRequest", description = "게시글 수정 DTO")
    public static class updateRequest {

        @NotNull
        private String title;
        @NotNull
        private String content;
    }

    @RequiredArgsConstructor
    @Getter
    @Schema(name = "postResponse", description = "게시글 응답 DTO")
    public static class Response {
        private final Long id;
        private final String title;
        private final String content;
        private final Integer like;
        private final Integer dislike;
        private final Integer viewCount;
        private final String createdAt;
        private final String updatedAt;
        private final Long userId;
        private String checkLikeOrDislike;
        private final List<CommentDto.Response> comments;
        private final List<PostVideoDto.Response> videoList;

        public Response(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent().replaceAll(System.lineSeparator(), "<br>");;
            this.createdAt = String.valueOf(post.getCreatedAt());
            this.updatedAt = String.valueOf(post.getUpdatedAt());
            this.userId = post.getUser().getId();
            this.comments = post.getComments().stream().map(CommentDto.Response::new).toList();
            this.like = post.getLikeCount();
            this.dislike = post.getDislikeCount();
            this.viewCount = post.getViewCount();
            this.videoList = post.getPostVideo().stream().map(PostVideoDto.Response::new).toList();
        }

        public Response(Post post, String checkLikeOrDislike) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent().replaceAll(System.lineSeparator(), "<br>");;
            this.createdAt = String.valueOf(post.getCreatedAt());
            this.updatedAt = String.valueOf(post.getUpdatedAt());
            this.userId = post.getUser().getId();
            this.comments = post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList());
            this.like = post.getLikeCount();
            this.dislike = post.getDislikeCount();
            this.viewCount = post.getViewCount();
            this.checkLikeOrDislike = checkLikeOrDislike;
            this.videoList = post.getPostVideo().stream().map(PostVideoDto.Response::new).toList();
        }
    }

    @Data
    @RequiredArgsConstructor
    @Schema(name = "postListResponse", description = "게시글 전체 list 응답 DTO")
    public static class postListResponse {
        @NotNull
        private final List<PostDto.Response> list;
        private final int pageCount;
    }

    @Getter
    @Schema(name = "postUpdateResponse", description = "게시글 수정 DTO")
    public static class updateResponse {
        private final Long id;

        public updateResponse(Long id) {
            this.id = id;
        }
    }
}
