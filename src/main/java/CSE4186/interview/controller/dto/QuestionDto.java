package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//dto에 schema
public class QuestionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "questionCreateRequest", description = "질문 생성 DTO")
    public static class Request{
        private int questionNum;
        private Long selfIntroductionId;
        private String job;
        private List<String> additionalQuestions;
        private List<Integer> additionalQuestionsSequence;
    }

    @Builder
    @NoArgsConstructor
    @Data
    @Schema(name = "questionCreateResponse", description = "질문 응답 DTO")
    public static class Response {
        private String content;
        public Response(String Content) {
            this.content = Content;
        }
    }
}