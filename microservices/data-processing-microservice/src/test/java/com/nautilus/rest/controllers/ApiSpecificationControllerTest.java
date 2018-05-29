package com.nautilus.rest.controllers;

import com.nautilus.controller.ApiSpecificationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiSpecificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void indexNoAuthTest() throws Exception {
        mockMvc.perform(get(ApiSpecificationController.INDEX_MAPPING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void indexAuthWhenNoRoleTest() throws Exception {
        mockMvc.perform(get(ApiSpecificationController.INDEX_MAPPING))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = "ADMIN")
    public void indexAuthWhenOkTest() throws Exception {
        mockMvc.perform(get(ApiSpecificationController.INDEX_MAPPING))
                .andExpect(status().isOk());
    }
}
