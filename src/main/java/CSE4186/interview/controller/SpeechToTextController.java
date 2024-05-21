package CSE4186.interview.controller;

import CSE4186.interview.service.SpeechToTextService;
import CSE4186.interview.utils.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SpeechToTextController {

    @Autowired
    private SpeechToTextService speechToTextService;

    @PostMapping("/transcribe")
    public ApiUtil.ApiSuccessResult<String> transcribe(@RequestBody Map<String, String> request) {
        String base64Audio = request.get("audio");
        return ApiUtil.success(speechToTextService.convertSpeechToText(base64Audio));
    }
}
