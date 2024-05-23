package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class SelfIntroductionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionCreateRequest", description = "자소서 생성 DTO")
    public static class CreateRequest{
        @NotNull private String title;
        @NotNull private List<SelfIntroductionDetailRequest> detailList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionUpdateRequest", description = "자소서 수정 DTO")
    public static class UpdateRequest{
        @NotNull private String title;
        @NotNull private List<SelfIntroductionDetailRequest> detailList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "selfIntroductionDetailRequest", description = "자소서 detail DTO")
    public static class SelfIntroductionDetailRequest{
        @NotNull private String title;
        @NotNull private String content;
        @NotNull private String type;
    }


    @Data
    @AllArgsConstructor
    @Schema(name = "selfIntroductionRequest", description = "자소서 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String createdAt;
        private String updatedAt;
        private List<SelfIntroductionDetailDto.Response> detailList;

        public Response(SelfIntroduction selfIntroduction){
            this.id= selfIntroduction.getId();
            this.title = selfIntroduction.getTitle();
            this.createdAt = String.valueOf(selfIntroduction.getCreatedAt());
            this.updatedAt = String.valueOf(selfIntroduction.getUpdatedAt());
            this.detailList = selfIntroduction.getSelfIntroductionDetailList().stream().map(SelfIntroductionDetailDto.Response::new).toList();
        }
    }

    @Data
    @RequiredArgsConstructor
    @Schema(name = "selfIntroductionListResponse", description = "자소서 전체 list 응답 DTO")
    public static class SelfIntroductionListResponse {
        @NotNull
        private final List<SelfIntroductionDto.Response> list;
        private final int pageCount;
    }


}
