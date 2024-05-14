package CSE4186.interview.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    /**
     * 서버 스펙의 Cors Policy 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9000", "https://accounts.google.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
}
