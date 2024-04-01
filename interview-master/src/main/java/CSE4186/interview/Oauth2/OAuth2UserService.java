package CSE4186.interview.Oauth2;

import CSE4186.interview.DTO.Oauth2Principal;
import CSE4186.interview.DTO.Oauth2UserInfo;
import CSE4186.interview.entity.Authority;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.AuthRepository;
import CSE4186.interview.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final Logger logger= LoggerFactory.getLogger(OAuth2UserService.class);

    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthorizationException {

        // 로그인 성공 후 토큰까지 받은 이후 실행됨.
        logger.info("loadUser");

        // 1. 상위 클래스의 loadUser을 통해서 가져온 user의 정보를 저장
        Map<String, Object> userAttributes = super.loadUser(oAuth2UserRequest).getAttributes();

        // 2. 어떤 서비스를 통해 로그인한 유저인지 판정
        String registrationId=oAuth2UserRequest.getClientRegistration().getRegistrationId();

        // 3. 유저 아이디 가져오기
        // userNameAttributeName : 인증 후 유저 정보를 제공하는 리소스 서버에서 사용자를 구분하는 키 값
        String userNameAttributeName=oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //4. 회원가입 또는 로그인
        Oauth2UserInfo oAuth2UserInfo= Oauth2UserInfo.of(registrationId,userAttributes);
        User user = getUser(oAuth2UserInfo);

        //5. OAuth2User 리턴
        return new Oauth2Principal(user,userAttributes,userNameAttributeName);
    }

    private User getUser(Oauth2UserInfo oAuth2UserInfo){
        Optional<User> memberOptional=userRepository.findByEmail(oAuth2UserInfo.email());
        User user=memberOptional.orElseGet(()->{
            Authority authority = authRepository.findById("ROLE_USER").orElseGet(()->{
                Authority newAuthority = new Authority("ROLE_USER");
                authRepository.save(newAuthority);
                return newAuthority;
            });
            User newUser = new User(oAuth2UserInfo.name(), oAuth2UserInfo.email(),authority);
            userRepository.save(newUser);
            return newUser;
        });
        return user;
    }
}
