package CSE4186.interview.DTO;

import lombok.Builder;
import org.springframework.security.authentication.ProviderNotFoundException;

import java.util.Map;

@Builder
public record Oauth2UserInfo(
        String name,
        String email
) {
    public static Oauth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            default -> throw new ProviderNotFoundException("Unsupported OAuth 2.0 Provider : "+registrationId);
        };
    }

    private static Oauth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return Oauth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .build();
    }

}