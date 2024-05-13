package CSE4186.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TextToSpeechService {

    /*public byte[] synthesizeText(String text) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // Build the voice request
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")  // languageCode = "ko-KR"
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)  // MP3 audio.
                    .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();
            return audioContents.toByteArray();
        }
    }*/
    private final String apiKey;
    public TextToSpeechService(@Value("${google.api-key}") String secret){
        apiKey=secret;
        //this.objectMapper = objectMapper;
        //this.textToSpeechService = textToSpeechService;
    }
    //private static final String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=";

    //@PostMapping("/tts")
    public byte[] convertTextToSpeech(@RequestBody String text) {
        String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + apiKey;
        /*RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJson = "{\"input\":{\"text\":" + text + "},\"voice\":{\"languageCode\":\"ko-KR\",\"name\":\"ko-KR-Neural2-c\", \"ssmlGender\":\"MALE\"},\"audioConfig\":{\"audioEncoding\":\"MP3\"}}";
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(GOOGLE_TTS_API_URL, HttpMethod.POST, entity, byte[].class);
        return response.getBody();*/
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestMap = new HashMap<>();
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("text", text);

        Map<String, String> voiceMap = new HashMap<>();
        voiceMap.put("languageCode", "ko-KR");
        voiceMap.put("name", "ko-KR-Neural2-C");

        Map<String, String> audioConfigMap = new HashMap<>();
        audioConfigMap.put("audioEncoding", "MP3");

        requestMap.put("input", inputMap);
        requestMap.put("voice", voiceMap);
        requestMap.put("audioConfig", audioConfigMap);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(GOOGLE_TTS_API_URL, HttpMethod.POST, entity, byte[].class);


        return response.getBody();
    }
}