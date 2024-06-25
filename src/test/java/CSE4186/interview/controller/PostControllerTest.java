package CSE4186.interview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostControllerTest {

    private MockMvc mvc;



    void setUp(@Autowired PostController postController){
        //MockMvc
        mvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

}