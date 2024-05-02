package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.QuestionDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
//컨트롤러에 tag
@RestController
@RequiredArgsConstructor
@Tag(name = "Question", description = "Question API")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question/create")
    @Operation(summary = "Create Question", description = "Gemini를 사용하여 질문 생성")
    public Mono<ResponseEntity<String>> createQuestionWithGemini(@RequestBody QuestionDto.Request request) {

        int questionNum= request.getQuestionNum();;
        String selfIntroductionContent = request.getContent();

        try {
            return questionService.createQuestion(questionNum,selfIntroductionContent)
                    .map(responseEntity -> {
                        return ResponseEntity.ok(responseEntity.getBody());
                    });
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
