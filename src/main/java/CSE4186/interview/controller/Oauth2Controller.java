package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.service.Oauth2UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Oauth2", description = "Oauth2 Login API")
public class Oauth2Controller {

    private final Oauth2UserService OAuth2UserService;

    @PostMapping("oauth2/google")
    BaseResponseDto<Map<String,String>> getOauth2Token(@RequestBody UserDTO.oauth2LoginRequest oauth2LoginRequest, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        //1. 코드를 받는다.
        String code= oauth2LoginRequest.getCode();
        System.out.println(code);

        //2. 코드를 사용해 토큰을 받아온다.
        String token= OAuth2UserService.requestGoogleToken(code);

        //2. 토큰을 통해 구글 서버에서 계정 정보를 가져온다.
        BaseResponseDto<Map<String,String>> response= OAuth2UserService.requestGoogleAccountAndLogin(token);

        return response;
    }
}
