package CSE4186.interview.controller;

import CSE4186.interview.config.TestSecurityConfig;
import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.entity.Comment;
import CSE4186.interview.entity.JobField;
import CSE4186.interview.entity.Post;
import CSE4186.interview.entity.User;
import CSE4186.interview.service.CommentService;
import CSE4186.interview.service.PostService;
import CSE4186.interview.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    private final String commentJsonString = """
                        {
                            "verbal": [
                                5,
                                3,
                                5,
                                5,
                                5
                            ],
                            "nonverbal": [
                                4,
                                5,
                                4,
                                5
                            ],
                            "review": "1221"
                        }
                """;

    @Test
    void getAllPosts() throws Exception {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");

        Post post1 = new Post(1L, "title", "content", user, jobField);
        Post post2 = new Post(2L, "title2", "content2", user, jobField);

        List<Post> postList = List.of(post1,post2);
        Page<Post> postPage = new PageImpl<>(postList);

        given(postService.findPostsByCondition(Mockito.any(Pageable.class), Mockito.anyString(), Mockito.anyString())).willReturn(postPage);

        ResultActions actions = mvc.perform(get("/post/list")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.pageCount").value(1));
    }

    @Test
    void getPost() throws Exception {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");

        Post post = new Post(1L, "title", "content", user, jobField);

        given(postService.findPost(Mockito.anyLong())).willReturn(post);

        ResultActions actions = mvc.perform(get("/post/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body.id").value(1L));
    }

    @Test
    void addPost() throws Exception {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");
        Post post = new Post(1L, "title", "content", user, jobField);
        PostDto.CreateRequest request = new PostDto.CreateRequest("title", "content", 1L, 1L, List.of(1L, 2L));

        String content = objectMapper.writeValueAsString(request);


        given(postService.addPost(Mockito.any(PostDto.CreateRequest.class))).willReturn(post);

        ResultActions actions = mvc.perform(post("/post")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void updatePost() throws Exception {
        PostDto.UpdateRequest request = new PostDto.UpdateRequest("title", "content", 1L, List.of(1L, 2L));

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(put("/post/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void deletePost() throws Exception {
        ResultActions actions = mvc.perform(delete("/post/1")
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("게시글이 삭제되었습니다."));
    }

    @Test
    void addComment() throws Exception {
        User user = new User("syh", "syh@gmail.com", "1234");
        JobField jobField = new JobField("백엔드", "BE");
        Post post = new Post(1L, "title", "content", user, jobField);

        Comment comment = new Comment(1L, commentJsonString, user, post, 0, 0);

        CommentDto.CreateRequest request = new CommentDto.CreateRequest(commentJsonString, 1L);

        String content = objectMapper.writeValueAsString(request);


        given(commentService.addComment(Mockito.any(CommentDto.CreateRequest.class), Mockito.anyLong())).willReturn(comment);

        ResultActions actions = mvc.perform(post("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void updateComment() throws Exception {
        CommentDto.UpdateRequest request = new CommentDto.UpdateRequest(1L, commentJsonString);

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(put("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void deleteComment() throws Exception {
        CommentDto.DeleteRequest request = new CommentDto.DeleteRequest(1L);

        String content = objectMapper.writeValueAsString(request);

        ResultActions actions = mvc.perform(delete("/post/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(user("1").password("password"))
        );

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.body").value("댓글이 삭제되었습니다."));
    }
}