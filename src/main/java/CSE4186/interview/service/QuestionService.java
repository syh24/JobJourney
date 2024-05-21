package CSE4186.interview.service;

import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.SelfIntroductionDetailRepository;
import CSE4186.interview.repository.SelfIntroductionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
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
    private final SelfIntroductionDetailRepository selfIntroductionDetailRepository;
    private final SelfIntroductionRepository selfIntroductionRepository;
    private List<Map<String,String>> message;
    private List<Map<String,String>> questionList;
    private RestTemplate template;
    private String prompt;
    private String url;
    private String system_content_tech="###Role###\n You need to write a script for a development team leader who will conduct an interview." +
            "Your role consists of two tasks: 1. Classify the given self-introduction into achievements and activities the applicant has undertaken" +
            "and the lessons the applicant has learned during the process." +
            "2. Provide %d questions to verify the authenticity of the 'achievements (activities)' " +
            "and %d questions to assess the applicant's 'technical understanding' based on the 'lessons learned'." +
            "\n\n###Note### \n Do not output the results of task 1. For task 2, number each question and separate them with line breaks." +
            "Do not categorize each question into \"action\" or \"lessons learned\". This is a script to be read to the applicant, " +
            "so no additional comments should be added.\n\n" +

            "###Example Response###\n" +
            "<행위 질문>\n" +
            "1. 질문 1\n" +
            "2. 질문 2\n" +
            "<Lessons Learned Questions>\n" +
            "3. 질문 3\n" +
            "4. 질문 4\n";
    private String system_content_personality="###Role###\n You need to write a script for a development team leader who will conduct an interview." +
            "Your role consists of two tasks: 1. Classify the given self-introduction into achievements and activities the applicant has undertaken" +
            "and the lessons the applicant has learned during the process." +
            "2. Provide %d questions to verify the authenticity of the 'achievements (activities)' " +
            "and %d questions to assess the applicant's 'technical understanding' based on the 'lessons learned'." +
            "\n\n###Note### \n Do not output the results of task 1. For task 2, number each question and separate them with line breaks." +
            "Do not categorize each question into \"action\" or \"lessons learned\". This is a script to be read to the applicant, " +
            "so no additional comments should be added.\n\n" +

            "###Example Response###\n" +
            "<행위 질문>\n" +
            "1. 질문 1\n" +
            "2. 질문 2\n" +
            "<Lessons Learned Questions>\n" +
            "3. 질문 3\n" +
            "4. 질문 4\n";

    public QuestionService(@Value("${google.api-key}") String secret, ObjectMapper objectMapper, SelfIntroductionDetailRepository selfIntroductionDetailRepository, SelfIntroductionRepository selfIntroductionRepository){
        apiKey=secret;
        this.objectMapper = objectMapper;
        this.selfIntroductionDetailRepository = selfIntroductionDetailRepository;
        this.selfIntroductionRepository = selfIntroductionRepository;
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

    private void setPrompt(Integer questionNum, String dept, String type, String selfIntroductionContent){
        String messageToJson;
        String system_content=type.matches("tech")?system_content_tech:system_content_personality;

        //1. system의 chat을 만든다
        Map<String,String>system=new HashMap<>();
        system.put("role","system");
        system.put("content",system_content);
        message.add(system);

        //2. user의 chat을 만든다
        Map<String,String>user=new HashMap<>();
        user.put("role","user");
        user.put("content",selfIntroductionContent);
        message.add(system);

        //3. message를 JSON으로 변환하여 prompt에 추가한다.
        try {
            messageToJson=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        prompt="messages="+messageToJson;
        System.out.println(prompt);
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

    private void getQuestions(String textContent){

        //1. 질문을 \n 기준으로 파싱
        String[] questionsParsedByLine=textContent.split("\n");

        //2. 질문을 <숫자.> 기준으로 파싱하고 <숫자.>은 삭제
        String[] rawQuestions=Arrays.stream(questionsParsedByLine)
                .filter(q->q.matches("\\d+\\..*"))
                .map(q->q.replaceAll("\\d+\\.","").trim())
                .toArray(String[]::new);

        //3. List에 <"번호":질문> 형식으로 저장
        List<Map<String,String>> questionsTaggedByNumber=new ArrayList<>();
        IntStream.range(0, rawQuestions.length)
                .forEach(index->{
                    Map<String,String> taggedQuestionMap=new HashMap<>();
                    taggedQuestionMap.put(Integer.toString(index),rawQuestions[index]);
                    questionsTaggedByNumber.add(taggedQuestionMap);
                    System.out.println(rawQuestions[index]);
                });

        //4. questionList에 생성된 질문 담아 리턴
        questionList.addAll(questionsTaggedByNumber);
    }

    private List<SelfIntroductionDetail> getSelfIntroductionDetails(int selfIntroductionId){
        SelfIntroduction selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId))
                .orElseThrow(()->new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + selfIntroductionId));
        return selfIntroduction.getSelfIntroductionDetailList();
    }

    private void createQuestionForEachSelfIntroductionDetails(int questionNum, String dept, String type, String selfIntroductionContent){

        //1. 프롬프트 세팅
        setPrompt(questionNum,dept,type,selfIntroductionContent);

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

        //6. 답변 속 질문을 파싱하여 List 형태로 저장
        getQuestions(textContent);
    }


    public Map<String,List<Map<String,String>>> createQuestion(int questionNum, String dept, int selfIntroductionId){
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // selfIntroduction에 포함된 selfIntroductionDetails를 가져옴
        List<SelfIntroductionDetail> selfIntroductionDetails=getSelfIntroductionDetails(selfIntroductionId);
        selfIntroductionDetails.forEach(s->{
            createQuestionForEachSelfIntroductionDetails(questionNum, dept, s.getType(), s.getContent());
        });

        // 생성된 모든 질문들을 JSON 형태로 저장한 후 리턴
        Map<String, List<Map<String,String>>> questionToJson=new HashMap<>();
        questionToJson.put("questions",questionList);
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
                        .temperature(1.5f)
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
