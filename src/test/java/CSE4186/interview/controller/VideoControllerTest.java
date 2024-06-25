package CSE4186.interview.controller;

import CSE4186.interview.annotation.WithCustomMockUser;
import CSE4186.interview.config.TestSecurityConfig;
import CSE4186.interview.controller.dto.VideoDto;
import CSE4186.interview.entity.User;
import CSE4186.interview.entity.Video;
import CSE4186.interview.login.CustomUserDetailsService;
import CSE4186.interview.service.UserService;
import CSE4186.interview.service.VideoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private VideoService videoService;

    @MockBean
    private UserService userService;

    private Authentication atc;

    @BeforeEach
    void setUp() {
        atc = new TestingAuthenticationToken("test1@gmail.com", null, "ROLE_USER");

    }

    @Test
    void addVideo() throws Exception {
        VideoDto.CreateRequest request = new VideoDto.CreateRequest("title", "link", 1L);

        given(videoService.addVideo(Mockito.any(VideoDto.CreateRequest.class))).willReturn(1L);

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(post("/video")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(authentication(atc))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value(1));

    }

    @Test
    @WithCustomMockUser
    void getAllVideo() throws Exception {
        User user = new User("syh", "syh@gmail.com", "1234");

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@gmail.com");



        Video video1 = new Video("title", "link", user);
        Video video2 = new Video("title2", "link2", user);
        List<Video> videoList = List.of(video1,video2);
        Page<Video> videoPage = new PageImpl<>(videoList);

        given(videoService.findAllVideoByUser(Mockito.any(Pageable.class), Mockito.anyLong())).willReturn(videoPage);

        ResultActions actions = mvc.perform(get("/video/list")
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.pageCount").value(1));
    }

    @Test
    void deleteVideo() throws Exception {

        ResultActions actions = mvc.perform(delete("/video/1"));
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("비디오 삭제 성공"));
    }
}