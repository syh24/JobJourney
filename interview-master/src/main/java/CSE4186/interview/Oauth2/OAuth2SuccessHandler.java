package CSE4186.interview.Oauth2;

import CSE4186.interview.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger= LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    @Autowired
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("successful authentication");

        //1. jwt 토큰 생성
        String accessToken=tokenProvider.createAccessToken(authentication);

        //2. 헤더에 토큰 포함
        response.setHeader("Authorization","Bearer "+accessToken);

    }
}