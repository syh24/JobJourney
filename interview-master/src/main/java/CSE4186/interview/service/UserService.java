package CSE4186.interview.service;

import CSE4186.interview.controller.dto.UserDTO;
import CSE4186.interview.entity.Authority;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.AuthRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no user"));
    }

    public boolean login(UserDTO.loginRequest request) {
        User user=userRepository.findByEmail(request.getEmail());

        if(user!=null && bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) return true;
        else return false;
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
}
