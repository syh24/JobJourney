package CSE4186.interview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class QuestionService {

    //라이브러리 사용해서 json 파싱하기
    //기본 json 파일 만들어두고 text 부분만 수정하기

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private String prompt="넌 이제부터 면접관이야. 아래에 있는 자기소개서를 읽고 질문 %d개를 만들어줘. 이 때, 질문 앞에 1. 2. 처럼 숫자와 온점을 찍어서 질문을 구분해줘. 예를 들면 1. 하기 싫은 업무를 맡게 되면 어떻게 할 것인가요? << 이런식으로.\n 이제 자기소개서를 보내줄게.";

    public QuestionService(@Value("${google.api-key}") String secret, ObjectMapper objectMapper){
        apiKey=secret;
        this.objectMapper = objectMapper;
    }

    @Builder
    @Getter
    public static class ApiRequestBody {

        private List<Contents> contents;
        private List<SafetySettings> safetySettings;
        private GenerationConfig generationConfig;

        @Getter
        @Builder
        public static class Contents {
            List<PartElem> parts;
        }

        @Getter
        @Builder
        static class SafetySettings {
            String category;
            String threshold;
        }

        @Getter
        @Builder
        static class GenerationConfig {
            List<SequenceElem> stopSequences;
            float temperature;
            int maxOutputTokens;
            float topP;
            int topK;
        }

        @Getter
        @Builder
        static class PartElem {
            String text;
        }

        @Getter
        @Builder
        static class SequenceElem {
            List<String> array;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ApiResponseBody{

        private List<Candidate> candidates;
        private PromptFeedback promptFeedback;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Candidate{
            private Content content;
            private String finishReason;
            private int index;
            private List<ratingElem> safetyRatings;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Content{
            private List<Part> parts;
            private String role;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class PromptFeedback{
            private List<ratingElem> safetyRatings;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Part{
            private String text;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        static class ratingElem{
            private String category;
            private String probability;
        }
    }


    public Mono<ResponseEntity<String>> createQuestion(int questionNum, String selfIntroductionContent) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

        String requestBody=createRequestBody(selfIntroductionContent);
        WebClient webClient = WebClient.create();
        prompt=String.format(prompt,questionNum);
        System.out.println("rr");

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(ApiResponseBody.class)
                .flatMap(body -> {
                    String response=
                            body.getCandidates()
                                .get(0)
                                .getContent()
                                .getParts()
                                .get(0)
                                .getText();

                    String[] parsedQuestions=response.split("\n");
                    String[] rawQuestions=Arrays.stream(parsedQuestions)
                                                .map(q->q.replaceAll("\\d+\\.","").trim())
                                                .toArray(String[]::new);

                    List<Map<String,String>> taggedQuestions=new ArrayList<>();
                    IntStream.range(0, rawQuestions.length)
                            .forEach(index->{
                                Map<String,String> taggedQuestionMap=new HashMap<>();
                                taggedQuestionMap.put(Integer.toString(index),rawQuestions[index]);
                                taggedQuestions.add(taggedQuestionMap);
                            });

                    Map<String, List<Map<String,String>>> questionToJson = new HashMap<>();
                    questionToJson.put("questions",taggedQuestions);

                    try {
                        String jsonResponseString = objectMapper.writeValueAsString(questionToJson);
                        return Mono.just(ResponseEntity.ok(jsonResponseString));
                    } catch (JsonProcessingException e) {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });


    }

    public String createRequestBody(String selfIntroductionContent) throws Exception {

        String requirements = prompt + "\n" + selfIntroductionContent;

        ApiRequestBody apiRequestBody = ApiRequestBody.builder()
                .contents(Arrays.asList(ApiRequestBody.Contents.builder()
                                .parts(
                                        Arrays.asList(ApiRequestBody.PartElem.builder()
                                                .text(requirements)
                                                .build()
                                        )
                                )
                                .build()
                        )
                )
                .safetySettings(Arrays.asList(ApiRequestBody.SafetySettings.builder()
                                .category("HARM_CATEGORY_DANGEROUS_CONTENT")
                                .threshold("BLOCK_ONLY_HIGH")
                                .build()
                        )
                )
                .generationConfig(ApiRequestBody.GenerationConfig.builder()
                        .stopSequences(Collections.emptyList())
                        .temperature(1.0f)
                        .maxOutputTokens(800)
                        .topP(0.8f)
                        .topK(10)
                        .build()
                )
                .build();

        return convertToJson(apiRequestBody);
    }

    public String convertToJson(ApiRequestBody apiRequestBody) throws Exception{
        return objectMapper.writeValueAsString(apiRequestBody);
    }

}
