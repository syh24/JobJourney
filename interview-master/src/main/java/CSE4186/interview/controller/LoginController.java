package CSE4186.interview.controller;


import CSE4186.interview.DTO.UserDTO;
import CSE4186.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/join")
    public void join(@RequestBody UserDTO userDTO){
        userService.join(userDTO.getName(),userDTO.getEmail(),userDTO.getPassword());
    }

    //일반적인 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO){

        if(!userService.login(userDTO.getName(),userDTO.getPassword())){
            return ResponseEntity.status(401).body("Unauthorized: Invalid username or password");
        }

        return (ResponseEntity<String>) ResponseEntity.ok();
    }

}
