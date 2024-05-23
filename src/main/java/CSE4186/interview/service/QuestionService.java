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
            "Do not categorize each question into \"achievements(activities)\" or \"lessons learned\". This is a script to be read to the applicant, " +
            "so no additional comments should be added.\n\n" +

            "###Example Response###\n" +
            "<행위 질문>\n" +
            "1. 질문 1\n" +
            "2. 질문 2\n" +
            "<배운점 질문>\n" +
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
            "<배운점 질문>\n" +
            "3. 질문 3\n" +
            "4. 질문 4\n";

    private String system_content_followUp="###Role###\nYou need to write a script for a team lead of a development team who is acting as an interviewer.\n" +
            "The team lead should continuously engage in conversation with the applicant.\n" +
            "Based on the context of the chat and the applicant's latest answer, the team lead should ask follow-up questions to assess the applicant's knowledge level. " +
            "The team lead can ask for detailed explanations about the technologies mentioned by the applicant or request the applicant to elaborate on advanced knowledge related to those technologies.\n\n"+
            "###Note###\nYou should consider the context of the chat between you and the applicant so far.\n" +
            "And you need to create questions based on the applicant's latest answer.\n" +
            "Create questions to verify the development knowledge included in the user's last answer. " +
            "Also, the answers to your questions should not be found in the applicant's previous response."+
            "You have to make questions in Korean.\n"+
            "I will now provide the conversation between you and the applicant.\n" +
            "You asked the first question, and your question is as follows:\n";
    private String system_content_followUp_tail="\n###Role###\nPlease ask additional questions based on web development keywords in user's response.\n"+
            "You should give higher priority to the most recent user chat when generating new questions.\n"+
            "You need to ask 3 questions to verify user's knowledge level. For example, you can ask user to explain web development keywords included in user's answer.\n"+
            "###Note###\nnumber each question and separate them with line breaks.\n"+
            "###Example Response###\n"+
            "1. 질문 1\n"+
            "2. 질문 2\n"+
            "3. 질문 3\n";

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

    private void createChat(String role,String content){
        Map chat=new HashMap<>();
        chat.put("role",role);
        chat.put("content",content);
        message.add(chat);
    }

    private void setPrompt(Integer questionNum, String dept, String type, String selfIntroductionContent){
        String messageToJson;
        String system_content=type.matches("tech")?system_content_tech:system_content_personality;
        message=new ArrayList<>();

        //1. system의 chat을 만든다
        createChat("system",system_content);

        //2. user의 chat을 만든다
        //createChat("user",selfIntroductionContent);

        //3. message를 JSON으로 변환하여 prompt에 추가한다.
        try {
            messageToJson=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        prompt="messages="+messageToJson+selfIntroductionContent;
        //입력은 chat으로 주지 않는게 가장 나은 듯.
        System.out.println(prompt);
    }

    /*프롬프트 테스트용. 프론트에서 사용자의 답변을 텍스트로 변환해서 줬다고 가정. STT 적용되면 수정해야 함.*/
    private void setPromptForFollowUp(int turn, List<Map<String,String>>prevChat){
        String messageToJson;

        //1. turn이 0이라면 시스템 프롬프트를 추가한다.
        if(turn==0){
            //1.1 [시스템 프롬프트+prevChat의 첫번째 system chat(최초의 질문)]을 하나의 system chat으로
            createChat("system",system_content_followUp+prevChat.get(0).get("content"));

            //1.2 prevChat의 첫번째 system chat과 마지막 user chat을 제외하고 message 배열에 추가.
            message.addAll(prevChat.subList(1, prevChat.size()-1));
        }
        //아닐 시 마지막 user chat을 제외하고 전부 message 배열에 추가한다.
        else{
            message.addAll(prevChat.subList(0,prevChat.size()-1));
        }

        //2. 마지막 user chat의 뒤에 유저 프롬프트를 추가한다.
        createChat("user",prevChat.get(prevChat.size()-1).get("content"));

        //3. 시스템의 프로므트를 다시 설정하여 chat에 추가
        createChat("system",system_content_followUp_tail);

        //4. message를 JSON으로 변환
        try {
            messageToJson=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("gemini output");
        message.forEach(m->{
            System.out.println(m);
        });

        //5. message에서 3에서 추가한 시스템 프롬프트 챗을 제거한다. (프론트에게 전달하기 위함)
        message.removeLast();
        prompt="messages"+messageToJson;
    }
    /*프롬프트 테스트용. STT 적용되면 수정해야 함.*/

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

    private Boolean getQuestions(Integer requiredQuestionNum, String textContent){
        System.out.println(textContent);
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
                });

        //4.1 올바른 형식인지 검사 - 빈 리스트인가? OR 원래 질문보다 적은 수가 생성되었는가? -> 재요청
        if(questionsTaggedByNumber.size()==0 || questionsTaggedByNumber.size()<requiredQuestionNum) return false;
        //4.2 올바른 형식인지 검사 - 질문 형식이 맞는가? -> 재요청
        String firstQuestion=questionsTaggedByNumber.get(0).get("0");
        System.out.println("firstQuestion : "+firstQuestion);
        if(!(firstQuestion.endsWith("?") || firstQuestion.equals("요.")||firstQuestion.equals("까"))){
            System.out.println("wrong format");
            return false;
        }
        //4.3 올바른 형식인지 검사 - 원래 질문보다 많은 수가 생성되었는가? -> 원래 개수만큼 선택 => (m개, m개)인데 (n개, n개)가 생성됨. (0~m-1)인덱스 선택. (n~n+m-1)인덱스 선택.
        if(questionsTaggedByNumber.size()>requiredQuestionNum){
            questionList.addAll(questionsTaggedByNumber.subList(0,requiredQuestionNum));
            questionList.addAll(questionsTaggedByNumber.subList(questionsTaggedByNumber.size()/2,questionsTaggedByNumber.size()/2+requiredQuestionNum-1));
            return true;
        }
        //5. questionList에 생성된 질문 담기
        questionList.addAll(questionsTaggedByNumber);
        return true;
    }

    private List<SelfIntroductionDetail> getSelfIntroductionDetails(int selfIntroductionId){
        SelfIntroduction selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId))
                .orElseThrow(()->new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + selfIntroductionId));
        return selfIntroduction.getSelfIntroductionDetailList();
    }

    private void createQuestionForEachSelfIntroductionDetails(int requiredQuestionNum, String dept, String type, String selfIntroductionContent){

        System.out.println("selfIntroductionContent ; "+selfIntroductionContent);
        Boolean isQuestionCreatedNormally=false;
        int callNum=0;

        //1. 프롬프트 세팅
        setPrompt(requiredQuestionNum,dept,type,selfIntroductionContent);

        //2. requestBody 만들기
        String requestBody=createRequestBody();

        //3. 헤더 세팅
        HttpEntity<String> requestEntity = setHeader(requestBody);

        // 잘못된 형식의 답변이 생성되면 재요청
        while(!isQuestionCreatedNormally && callNum<3) {
            //4. rest 통신
            ResponseEntity<String> responseEntity = template.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            String response = responseEntity.getBody();

            //5. response에서 gemini 답변 부분만 가져오기
            String textContent = getTextContent(response);

            //6. 답변 속 질문을 파싱하여 List 형태로 저장
            isQuestionCreatedNormally=getQuestions(requiredQuestionNum, textContent);
            callNum+=1;
        }
    }


    public Map<String,List<Map<String,String>>> createQuestion(int requiredQuestionNum, String dept, int selfIntroductionId){
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        message=new ArrayList<>();
        questionList=new ArrayList<>();

        // selfIntroduction에 포함된 selfIntroductionDetails를 가져옴
        List<SelfIntroductionDetail> selfIntroductionDetails=getSelfIntroductionDetails(selfIntroductionId);
        selfIntroductionDetails.forEach(s->{createQuestionForEachSelfIntroductionDetails(requiredQuestionNum, dept, s.getType(), s.getContent());});

        // 생성된 모든 질문들을 JSON 형태로 저장한 후 리턴
        Map<String, List<Map<String,String>>> questionToJson=new HashMap<>();
        questionToJson.put("questions",questionList);
        return questionToJson;
    }

    public Map<String,Object> createFollowUpQuestion(int turn, int selfIntroductionId, List<Map<String,String>>prevChat){
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        questionList=new ArrayList<>();
        message=new ArrayList<>();
        Boolean isQuestionCreatedNormally=false;
        int callNum=0;

        // 1. turn을 계산한다.
        if(turn+1>2) return null;

        // 2. 이전 질문들과 새로운 요구사항을 붙여서 프롬프트를 생성한다.
        setPromptForFollowUp(turn,prevChat);

        //3. requestBody 만들기
        String requestBody=createRequestBody();

        //4. 헤더 세팅
        HttpEntity<String> requestEntity = setHeader(requestBody);

        // 잘못된 형식의 답변이 생성되면 재요청
        while(!isQuestionCreatedNormally && callNum<3) {
            //5. rest 통신
            ResponseEntity<String> responseEntity = template.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            String response = responseEntity.getBody();

            //6. response에서 gemini 답변 부분만 가져오기
            String textContent = getTextContent(response);

            //7. 질문 목록 파싱하여 리스트로 저장하기
            isQuestionCreatedNormally=getQuestions(3, textContent);
            callNum+=1;
        }

        //8. 생성된 모든 질문들을 JSON 형태로 저장한 후 리턴
        Map<String, Object> questionToJson=new HashMap<>();
        questionToJson.put("followUps=",questionList);
        questionToJson.put("messages=",message);
        questionToJson.put("turn",String.valueOf(turn+1));
        return questionToJson;
    }

    private String createRequestBody(){
        ApiRequestBody apiRequestBody = ApiRequestBody.builder()
                .contents(Arrays.asList(ApiRequestBody.Contents.builder()
                                .parts(
                                        Arrays.asList(ApiRequestBody.PartElem.builder()
                                                .text(prompt)
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
