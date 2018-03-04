package com.nautilus.rest.controllers.user;

import com.nautilus.JsonUtil;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void getWhenUserNoAuth() throws Exception {
        mockMvc.perform(get(UserController.USER_MAPPING))
                .andExpect(status().isUnauthorized());
    }

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

    @Test
    public void postWhenUserIsOk() throws Exception {
        RegisterUserDTO userDTO = RegisterUserDTO.builder()
                .email("luke.skywalker@nautilus.com")
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password("star_wars")
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree("luke.skywalker@nautilus.com")).thenReturn(true);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    public void postWhenEmailIsNotFree() throws Exception {
        RegisterUserDTO userDTO = RegisterUserDTO.builder()
                .email("luke.skywalker@nautilus.com")
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password("star_wars")
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree("luke.skywalker@nautilus.com")).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        List<RegisterStatus> statuses = new ArrayList<RegisterStatus>(){{
            add(RegisterStatus.EMAIL_NOT_FREE);
        }};

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(content().json(jsonUtil.json(statuses)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void postWhenEmailAndPhoneNumberIsNotFree() throws Exception {
        RegisterUserDTO userDTO = RegisterUserDTO.builder()
                .email("luke.skywalker@nautilus.com")
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password("star_wars")
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree("luke.skywalker@nautilus.com")).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(false);

        List<RegisterStatus> statuses = new ArrayList<RegisterStatus>(){{
            add(RegisterStatus.EMAIL_NOT_FREE);
            add(RegisterStatus.PHONE_NUMBER_NOT_FREE);
        }};

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(content().json(jsonUtil.json(statuses)))
                .andExpect(status().isNotAcceptable());
    }
}