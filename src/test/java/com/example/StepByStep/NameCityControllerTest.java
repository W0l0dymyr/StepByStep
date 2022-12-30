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
public class NameCityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @WithUserDetails("aa")
    @Test
    public void nameCityMenuTest() throws Exception {
        this.mockMvc.perform(get("/game/name_city"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @WithUserDetails("aa")
    @Test
    public void playTest() throws Exception {
        this.mockMvc.perform(get("/game/name_city/play"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @WithUserDetails("aa")
    @Test
    public void playWithTest_firstPlayer() throws Exception {
        this.mockMvc.perform(get("/game/name_city/play_with"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @WithUserDetails("aa")
    @Test
    public void playTest_ifCountIsMoreThanZero() throws Exception {
        playTest();
        saveCityTest();
        this.mockMvc.perform(get("/game/name_city/play"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Варшава")));
    }

    @WithUserDetails("aa")
    @Test
    public void playTest_ifUserWin() throws Exception {
        playTest();
        this.mockMvc.perform(post("/game/name_city/save_used").param("name", "Анкара").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/name_city/play"));

        this.mockMvc.perform(get("/game/name_city/play"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Вітаю")));
    }

    @WithUserDetails("aa")
    @Test
    public void saveCityTest() throws Exception {
        this.mockMvc.perform(post("/game/name_city/save_used").param("name", "Львів").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/game/name_city/play"));
    }

    @WithUserDetails("aa")
    @Test
    public void saveCityTest_ifCityNameIsEmpty() throws Exception {
        this.mockMvc.perform(post("/game/name_city/save_used").param("name", "").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Назва населеного пункту не може бути порожньою")));
    }

    @WithUserDetails("aa")
    @Test
    public void gameOverTest() throws Exception {
        playWithTest_firstPlayer();
        this.mockMvc.perform(get("/game/name_city/game_over"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ти програв")));
    }

    @WithUserDetails("aa")
    @Test
    public void bestResultsTest() throws Exception {
        playWithTest_firstPlayer();
        this.mockMvc.perform(get("/game/name_city/best_results"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Найкращі результати")));
    }

}
