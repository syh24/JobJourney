package CSE4186.interview.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    /**
     * 서버 스펙의 Cors Policy 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:9000",
                        "https://ec2-54-180-118-228.ap-northeast-2.compute.amazonaws.com:9000",
                        "http://ec2-54-180-118-228.ap-northeast-2.compute.amazonaws.com:9000",
                        "http://ec2-54-180-118-228.ap-northeast-2.compute.amazonaws.com:80"
                        )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
}
