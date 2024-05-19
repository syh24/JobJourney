package CSE4186.interview.service;

import CSE4186.interview.controller.dto.BaseResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import io.swagger.v3.core.util.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.IntStream;

@Service
@Transactional
public class QuestionService {

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private String prompt;
    private String prompt_head="###상황 설정### 너는 지금부터 취업준비생을 위한 가상 면접관이 돼주어야 해. 구체적으로 3명의 역할을 수행해줬으면 좋겠어. 우선 인사담당자의 관점에서 내 자기소개서에 대해 질문을 해줘. 그리고 %s 부서의 담당자로, 실무진의 관점에서 질문해줘. 마지막은 회사의 임원진이야. 회사에 애착을 가지고 오랜 기간 근무한 임원진의 관점에서 질문을 해줘. ###답변 형식### 총 15개의 질문을 생성하고, 면접관마다 비슷한 개수의 질문을 만들어줘.각 질문마다 번호를 붙여줘. 그리고 모든 질문들은 줄바꿈으로 구분해줘. 이 때 어느 면접관이 질문인지 구분하지 않아야 돼. 이게 정말 중요해. 답변 예시는 아래와 같아. 1. 하기 싫은 업무를 맡게 된다면 어떻게 할 것인가요? 2. 이 활동을 통해 어떤 경험과 목표를 달성하고 싶은가요?\n\n";
    private final String prompt_body="###입력값### 이제 내 자소서를 보내줄게. 자기소개서는 문항과 답변으로 이루어져 있어.\n";
    private final String prompt_tail="\n\n###답변 형식### 너의 가장 중요한 역할은 답변 형식을 잘 맞추는거야. 너는 질문을 카테고리화 할 필요 없어. 어느 면접관의 질문인지 나눌 필요 없어. 질문에 대한 설명이나 답변에 대한 설명도 필요 없어. 오로지 15줄의 질문만 답하면 돼. 이게 정말 중요해. 오직 1~15번의 질문 외에는 아무런 답변도 추가하면 안돼.";

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

    private void setPrompt(Integer questionNum, String dept, String selfIntroductionContent){
        prompt_head=String.format(prompt_head,questionNum,dept);
        prompt=prompt_head+prompt_body+selfIntroductionContent+prompt_tail;
    }

    private HttpEntity<String> setHeader(String requestBody){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        return requestEntity;
    }

    private String getTextContent(String response) {
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> candidates= (Map<String, Object>)((List<Object>)jsonMap.get("candidates")).get(0);
        List<Map<String, Object>> parts=(List<Map<String,Object>>)((Map<String,Object>) candidates.get("content")).get("parts");
        String textContent = (String)parts.get(0).get("text");
        return textContent;
    }

    private Map<String,List<Map<String,String>>> getQuestions(String textContent){

        //1. 질문을 \n 기준으로 파싱
        String[] questionsParsedByLine=textContent.split("\n");

        //2. 질문을 <숫자.> 기준으로 파싱하고 <숫자.>은 삭제
        String[] rawQuestions=Arrays.stream(questionsParsedByLine)
                .map(q->q.replaceAll("\\d+\\.","").trim())
                .toArray(String[]::new);

        //3. List에 <"번호":질문> 형식으로 저장
        List<Map<String,String>> questionsTaggedByNumber=new ArrayList<>();
        IntStream.range(0, rawQuestions.length)
                .forEach(index->{
                    Map<String,String> taggedQuestionMap=new HashMap<>();
                    taggedQuestionMap.put(Integer.toString(index),rawQuestions[index]);
                    questionsTaggedByNumber.add(taggedQuestionMap);
                });

        //4. Map에 <"questions":질문 list> 형식으로 저장하여 리턴
        Map<String, List<Map<String,String>>> questionToJson = new HashMap<>();
        questionToJson.put("questions",questionsTaggedByNumber);
        return questionToJson;
    }


    public Map<String,List<Map<String,String>>> createQuestion(int questionNum, String dept, String selfIntroductionContent){
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        //1. 프롬프트 세팅
        setPrompt(questionNum,dept,selfIntroductionContent);

        //2. requestBody 만들기
        String requestBody=createRequestBody(selfIntroductionContent);

        //3. 헤더 세팅
        HttpEntity<String> requestEntity = setHeader(requestBody);

        //4. rest 통신
        ResponseEntity<String> responseEntity = template.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);
        String response = responseEntity.getBody();

        //5. response에서 gemini 답변 부분만 가져오기
        String textContent=getTextContent(response);

        //6. 답변 속 질문을 파싱하여 JSON 형태로 저장한 후 리턴
        Map<String, List<Map<String,String>>> questionToJson=getQuestions(textContent);

        return questionToJson;
    }

    public String createRequestBody(String selfIntroductionContent){
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

    public String convertToJson(ApiRequestBody apiRequestBody){
        try {
            return objectMapper.writeValueAsString(apiRequestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
