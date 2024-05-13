package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


//dto에 schema
public class QuestionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "questionCreateRequest", description = "질문 생성 DTO")
    public static class Request{
        private int questionNum;
        private String content;
    }
}