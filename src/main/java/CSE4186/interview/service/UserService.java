package CSE4186.interview.service;

import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.entity.Authority;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.AuthRepository;
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
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no user"));
    }

    public void join(UserDTO.joinRequest request) {
        Authority authority = authRepository.findById("ROLE_USER").orElseGet(()-> {
            Authority newAuthority=new Authority("ROLE_USER");
            authRepository.save(newAuthority);
            return newAuthority;
        });
        String encodedPassword=bCryptPasswordEncoder.encode(request.getPassword());
        User newUser=new User(request.getName(), request.getEmail(), encodedPassword,authority);

        userRepository.save(newUser);
    }

    public String getUserId(String email) throws JsonProcessingException {
        Map<String, Long> userIdMap=new HashMap<>();
        Optional<User> user=userRepository.findByEmail(email);

        if(user.isPresent()){
            userIdMap.put("userId",user.get().getId());
            System.out.println("d");
        }

        return objectMapper.writeValueAsString(userIdMap);
    }


    public List<String> checkNameAndEmail(String name, String email) {
        List<String>invalidProperties=new ArrayList<>();
        if(userRepository.findByName(name).isPresent()) invalidProperties.add("name");
        if(userRepository.findByEmail(email).isPresent()) invalidProperties.add("email");
        return invalidProperties;
    }
}