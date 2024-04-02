package CSE4186.interview.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger= LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER="Authorization";
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider=tokenProvider;
    }

    private String ResolveAccessToken(HttpServletRequest httpServletRequest) {
        String bearerToken= httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        //jwt 토큰은 bearer로 시작함
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            logger.info("JwtFilter-ResolveToken:succeed");
            //bearer 뒤부터 유의미한 정보이기 때문에
            return bearerToken.substring(7);
        }
        logger.info("failed..");
        return null;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken=ResolveAccessToken(request);
        String requestURI=request.getRequestURI();

        //유효한 토큰 보유
        if(StringUtils.hasText(accessToken) && tokenProvider.validateAccessToken(accessToken,request)){
            Authentication authentication=tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }
}
