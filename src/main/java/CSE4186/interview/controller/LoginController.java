package CSE4186.interview.controller;


import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/join/check")
    @Operation(summary = "checkNameAndEmail", description = "네임, 이메일 중복 체크")
    public ResponseEntity<BaseResponseDto<List<String>>> check(@RequestBody UserDTO.joinRequest request){

        List<String> result=userService.checkNameAndEmail(request.getName(),request.getEmail());

        return ResponseEntity.ok(
                new BaseResponseDto<>(
                        result.size()>0?"invalid":"valid",
                        "",
                        result
                ));

    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "로그인")
    public ResponseEntity<BaseResponseDto<Map<String, String>>> login(@AuthenticationPrincipal User loginUser){

        Map<String,String> userIdMap=new HashMap<>();
        userIdMap.put("userId", loginUser.getUsername());

        return ResponseEntity.ok(
                            new BaseResponseDto<>(
                                    "success",
                                    "",
                                    userIdMap
                            ));

    }



}