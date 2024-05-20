package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.QuestionDto;
import CSE4186.interview.service.QuestionService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//컨트롤러에 tag
@RestController
@RequiredArgsConstructor
@Tag(name = "Question", description = "Question API")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question/create")
    @Operation(summary = "Create Question", description = "Gemini를 사용하여 질문 생성")
    public ApiUtil.ApiSuccessResult<QuestionDto.Response> createQuestionWithGemini(@RequestBody QuestionDto.Request request) {
        String jsonOutput = questionService.createQuestion(request);
        QuestionDto.Response response = new QuestionDto.Response(jsonOutput);
        return ApiUtil.success(response);
        //return ApiUtil.success(questionService.createQuestion(request));
    }
}
