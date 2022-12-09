package com.example.StepByStep;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application_test.properties")
@Sql(value = {"/create_user_before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create_user_after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @WithUserDetails("aa")
    @Test
    public void allUsersTest() throws Exception {
        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id=\"user-list\"]").nodeCount(2));
    }
    @WithUserDetails("bb")
    @Test
    public void allUsersTest_ifUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("aa")
    @Test
    public void editUserTest() throws Exception {
        mockMvc.perform(get("/user/2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bb")));
    }

    @WithUserDetails("aa")
    @Test
    public void userSaveTest() throws Exception {
        mockMvc.perform(post("/user/2").param("username", "cc")
                        .param("password", "bbbbbb").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(content().string(containsString("cc")));
    }
}
