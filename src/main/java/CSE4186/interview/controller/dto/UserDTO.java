package CSE4186.interview.controller.dto;

import lombok.*;

public class UserDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class joinRequest{
        private String name;
        private String email;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class loginRequest{
        private String email;
        private String password;
    }



}