package CSE4186.interview.controller;

import CSE4186.interview.service.TextToSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tts")
public class TextToSpeechController {

    private final TextToSpeechService textToSpeechService;

    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody String text) {
        try {
            byte[] audioContent = textToSpeechService.synthesizeText(text);
            return ResponseEntity.ok()
                    .header("Content-Type", "audio/mpeg")
                    .body(audioContent);
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}