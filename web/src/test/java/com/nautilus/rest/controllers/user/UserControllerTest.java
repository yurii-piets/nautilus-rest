package com.nautilus.rest.controllers.user;

import com.nautilus.JsonUtil;
import com.nautilus.constants.Authorities;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.UserInfo;
import com.nautilus.services.GlobalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockBean
    private GlobalService service;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void getWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get(UserController.USER_MAPPING))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "luke.skywalker@nautilus.com", password = "star_wars", roles = "USER")
    public void getWhenUserExist() throws Exception {
        UserInfo user = UserInfo.builder()
                .email("luke.skywalker@nautilus.com")
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .build();
        String userJson = jsonUtil.json(user);
        UserConfig userConfig = new UserConfig();
        userConfig.setEmail("luke.skywalker@nautilus.com");
        userConfig.setPhoneNumber("111000222");
        userConfig.setName("Luke");
        userConfig.setSurname("Skywalker");
        userConfig.setPassword("star_wars");
        userConfig.setAuthorities(Authorities.USER);
        userConfig.setEnabled(true);

        when(service.findUserConfigByEmail("luke.skywalker@nautilus.com")).thenReturn(userConfig);

        mockMvc.perform(get(UserController.USER_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(userJson));
    }
}