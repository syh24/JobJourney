package CSE4186.interview.service;

import CSE4186.interview.controller.dto.BaseResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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


    public ResponseEntity<BaseResponseDto<String>> createQuestion(int questionNum, String selfIntroductionContent) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        prompt=String.format(prompt,questionNum);
        String requestBody=createRequestBody(selfIntroductionContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);
        String result = responseEntity.getBody();
        try{
            Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> candidates= (Map<String, Object>)((List<Object>)jsonMap.get("candidates")).get(0);
            List<Map<String, Object>> parts=(List<Map<String,Object>>)((Map<String,Object>) candidates.get("content")).get("parts");
            String response = (String)parts.get(0).get("text");
            System.out.println(response);

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
                return ResponseEntity.ok(
                        new BaseResponseDto<String>(
                                "success",
                            "",
                                    jsonResponseString
                        )
                );
            } catch (JsonProcessingException e) {
                return ResponseEntity.ok(
                        new  BaseResponseDto<String>(
                                "fail",
                                "",
                                ""
                        )
                );
            }

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(
                    new  BaseResponseDto<String>(
                            "fail",
                            "",
                            ""
                    )
            );
        }

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
