package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.QuestionDto;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question/create")
    public Mono<ResponseEntity<String>> createQuestion(@RequestBody SelfIntroductionDto.Request request) {

        String selfIntroductionContent = request.getContent();

        try {
            return questionService.createQuestion(selfIntroductionContent)
                    .map(responseEntity -> {
                        return ResponseEntity.ok(responseEntity.getBody());
                    });
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
