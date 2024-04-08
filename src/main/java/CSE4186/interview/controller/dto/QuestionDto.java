package CSE4186.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QuestionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class Response{
        private String content;
    }
}
