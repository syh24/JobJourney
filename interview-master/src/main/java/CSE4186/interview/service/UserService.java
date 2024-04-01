package CSE4186.interview.service;

import CSE4186.interview.entity.Authority;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.AuthRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no user"));
    }

    public void join(String name, String email, String password) {
        Authority authority = authRepository.findById("ROLE_USER").orElseGet(()-> {
            Authority newAuthority=new Authority("ROLE_USER");
            authRepository.save(newAuthority);
            return newAuthority;
        });
        User newUser=new User(name,email,password,authority);
        userRepository.save(newUser);
    }

    public boolean login(String name, String password) {
        if(userRepository.findByNameAndPassword(name,password)!=null) return true;
        else return false;
    }
}
