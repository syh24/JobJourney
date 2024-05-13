package CSE4186.interview.config;

import CSE4186.interview.jwt.JwtFilter;
import CSE4186.interview.jwt.TokenProvider;
import CSE4186.interview.login.CustomAuthenticationManager;
import CSE4186.interview.login.LoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web)->web.ignoring().requestMatchers(
                "/css/**","/js/**","/images/**","/favicon.ico","/error");
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig.disable())
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        //config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","POST","DELETE","OPTIONS"));
                        config.setAllowCredentials(true);
                        config.setExposedHeaders(List.of("Authorization"));
                        //config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setAllowedHeaders(List.of("Content-Type"));
                        config.setMaxAge(3600L); //1시간
                        return config;
                    }
                }))
//                .sessionManagement(session->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/login/oauth2/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/join/**").permitAll()
                                .requestMatchers("/token/**").permitAll()
                                .requestMatchers("/question/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .anyRequest().hasAnyRole("USER")
                )

                .oauth2Login(oauth2 ->
                        oauth2
                                .authorizationEndpoint(a->{
                                    a.baseUri("/login/oauth2");
                                })
                )

                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoginFilter(tokenProvider,customAuthenticationManager,objectMapper), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}