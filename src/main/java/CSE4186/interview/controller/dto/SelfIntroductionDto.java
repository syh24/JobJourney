package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroduction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class SelfIntroductionDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionCreateRequest", description = "자소서 생성 DTO")
    public static class Request{
        @NotNull private String title;
        @NotNull private Long userId;
        @NotNull private String content;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionRequest", description = "자소서 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String createdAt;
        private String updatedAt;

        public Response(SelfIntroduction selfIntroduction){
            this.id= selfIntroduction.getId();
            this.title = selfIntroduction.getTitle();
            this.content= selfIntroduction.getContent().replaceAll(System.lineSeparator(), "<br>");
            this.createdAt = String.valueOf(selfIntroduction.getCreatedAt());
            this.updatedAt = String.valueOf(selfIntroduction.getUpdatedAt());
        }
    }

    @Data
    @RequiredArgsConstructor
    @Schema(name = "selfIntroductionListResponse", description = "자소서 전체 list 응답 DTO")
    public static class selfIntroductionListResponse {
        @NotNull
        private final List<SelfIntroductionDto.Response> list;
        private final int pageCount;
    }


}
