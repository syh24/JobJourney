package CSE4186.interview.controller;


import CSE4186.interview.controller.dto.BaseResponseDto;
import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/join")
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
    public ResponseEntity<BaseResponseDto<String>> login(@RequestBody UserDTO.loginRequest request){

        if(!userService.login(request)){
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "fail",
                            "",
                            ""
                    ));
        }

        else{
            return ResponseEntity.ok(
                    new BaseResponseDto<>(
                            "success",
                            "",
                            ""
                    ));
        }

    }

}
