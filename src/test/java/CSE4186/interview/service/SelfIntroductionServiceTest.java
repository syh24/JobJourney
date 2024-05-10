package CSE4186.interview.service;

import CSE4186.interview.repository.SelfIntroductionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class SelfIntroductionServiceTest {

    //Test 주체
    SelfIntroductionService selfIntroductionService;

    //Test 협력자
    @MockBean
    SelfIntroductionRepository selfIntroductionRepository;

    @MockBean


    @Test
    void findAllSelfIntroductions() {
    }

    @Test
    void save() {
    }
}