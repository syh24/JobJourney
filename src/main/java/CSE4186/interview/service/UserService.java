package CSE4186.interview.service;

import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.entity.User;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.login.Role;
import CSE4186.interview.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
    }

    public void join(UserDTO.joinRequest request) {
        String encodedPassword=bCryptPasswordEncoder.encode(request.getPassword());
        User newUser=new User(request.getName(), request.getEmail(), encodedPassword, Role.USER);
        userRepository.save(newUser);
    }

    public String getUserId(String email) throws JsonProcessingException {
        Map<String, Long> userIdMap=new HashMap<>();
        Optional<User> user=userRepository.findByEmail(email);

        if(user.isPresent()){
            userIdMap.put("userId",user.get().getId());
        }

        return objectMapper.writeValueAsString(userIdMap);
    }


    public List<String> checkNameAndEmail(String name, String email) {
        List<String>invalidProperties=new ArrayList<>();
        if(userRepository.findByName(name).isPresent()) invalidProperties.add("name");
        if(userRepository.findByEmail(email).isPresent()) invalidProperties.add("email");
        return invalidProperties;
    }

    public void checkAccountStatus(String userId) throws Exception {
        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        if (user.getSuspensionStatus()) {
            throw new Exception("해당 계정은 정지된 계정입니다.");
        }
    }
}