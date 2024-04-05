package CSE4186.interview.config;

import CSE4186.interview.OAuth2.OAuth2SuccessHandler;
import CSE4186.interview.OAuth2.OAuth2UserService;
import CSE4186.interview.jwt.JwtFilter;
import CSE4186.interview.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenProvider tokenProvider;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web)->web.ignoring().requestMatchers(
                "/css/**","/js/**","/images/**","/favicon.ico","/error");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig.disable())
//                .sessionManagement(session->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login/**").permitAll()
                                .requestMatchers("/login/oauth2/**").permitAll()
                                .requestMatchers("/join").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 ->
                        oauth2
                                .userInfoEndpoint(o -> o.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .authorizationEndpoint(a->{
                                    a.baseUri("/login/oauth2");
                                })
                )

                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}