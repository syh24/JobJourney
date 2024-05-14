package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroduction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
            this.content= selfIntroduction.getContent();
            this.createdAt = String.valueOf(selfIntroduction.getCreatedAt());
            this.updatedAt = String.valueOf(selfIntroduction.getUpdatedAt());
        }

    }


}
