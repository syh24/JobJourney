package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.SelfIntroductionDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class SelfIntroductionDetailDto {


    @Getter
    @Schema(name = "selfIntroductionDetailResponse", description = "자소서 항목 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String type;

        public Response(SelfIntroductionDetail selfIntroductionDetail) {
            this.id = selfIntroductionDetail.getId();
            this.title = selfIntroductionDetail.getTitle();
            this.content = selfIntroductionDetail.getContent();
            this.type = selfIntroductionDetail.getType();
        }
    }
}
