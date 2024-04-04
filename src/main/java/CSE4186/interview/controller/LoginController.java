package CSE4186.interview.controller;


import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Login API")
public class LoginController {

    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "Join User", description = "회원가입")
    public ResponseEntity<BaseResponseDto<String>> join(@RequestBody UserDTO.joinRequest request){
        userService.join(request);
        return ResponseEntity.ok(
                new BaseResponseDto<>(
                        "success",
                        "",
                        ""
                ));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "로그인")
    public ResponseEntity<BaseResponseDto<String>> login(HttpServletResponse response) {

        return ResponseEntity.ok(
                            new BaseResponseDto<>(
                                    "success",
                                    "",
                                    ""
                            ));
    }

}