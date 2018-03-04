package com.nautilus.rest.controllers.user;

import com.nautilus.JsonUtil;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.Car;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final String MOCK_USER_EMAIL = "luke.skywalker@nautilus.com";
    private static final String MOCK_USER_PASSWORD = "star_wars";

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
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getWhenUserExist() throws Exception {
        UserInfo user = UserInfo.builder()
                .email(MOCK_USER_EMAIL)
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .build();
        String userJson = jsonUtil.json(user);
        createMockUser();
        mockMvc.perform(get(UserController.USER_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(userJson));
    }

    @Test
    public void postWhenUserIsOk() throws Exception {
        RegisterUserDTO userDTO = RegisterUserDTO.builder()
                .email(MOCK_USER_EMAIL)
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password(MOCK_USER_PASSWORD)
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(true);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    public void postWhenEmailIsNotFree() throws Exception {
        RegisterUserDTO userDTO = RegisterUserDTO.builder()
                .email(MOCK_USER_EMAIL)
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password(MOCK_USER_PASSWORD)
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        List<RegisterStatus> statuses = new ArrayList<RegisterStatus>() {{
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
                .email(MOCK_USER_EMAIL)
                .phoneNumber("111000222")
                .userName("Luke")
                .userSurname("Skywalker")
                .password(MOCK_USER_PASSWORD)
                .build();
        String userJson = jsonUtil.json(userDTO);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(false);

        List<RegisterStatus> statuses = new ArrayList<RegisterStatus>() {{
            add(RegisterStatus.EMAIL_NOT_FREE);
            add(RegisterStatus.PHONE_NUMBER_NOT_FREE);
        }};

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(content().json(jsonUtil.json(statuses)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void patchWhenNoAuth() throws Exception {
        mockMvc.perform(patch(UserController.USER_MAPPING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchUserEmail() throws Exception {
        createMockUser();

        String patchJson = "[{\"op\": \"replace\", \"path\": \"/phoneNumber\", \"value\": \"123000789\" }]";

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldDoesNotExist() throws Exception {
        createMockUser();

        String patchJson = "[{\"op\": \"replace\", \"path\": \"/not_exist\", \"value\": \"asd\" }]";

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteWhenNoAuth() throws Exception {
        mockMvc.perform(delete(UserController.USER_MAPPING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void deleteUser() throws Exception {
        createMockUser();

        mockMvc.perform(delete(UserController.USER_MAPPING))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserCarsWhenNoAuth() throws Exception {
        mockMvc.perform(get(UserController.USER_MAPPING + UserController.USER_CAR_MAPPING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getUserCars() throws Exception {
        createMockUser();

        Set<Car> set = new HashSet<Car>(){{
            Car car = new Car();
            car.setOwner(null);
            car.setBeaconId("1");
            car.setRegisterNumber("AA1234AA");
            car.setMark("Batmobile");
            car.setColor("Black");
            car.setYearOfProduction("1939");
            car.setDescription("The Batmobile's shields are made of ceramic fractal armor panels");
            add(car);
        }};

        mockMvc.perform(get(UserController.USER_MAPPING + UserController.USER_CAR_MAPPING))
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(jsonUtil.json(set)))
                .andExpect(status().isOk());
    }

    private void createMockUser() {
        UserConfig userConfig = new UserConfig();
        userConfig.setEmail(MOCK_USER_EMAIL);
        userConfig.setPhoneNumber("111000222");
        userConfig.setName("Luke");
        userConfig.setSurname("Skywalker");
        userConfig.setPassword(MOCK_USER_PASSWORD);
        userConfig.setAuthorities(Authorities.USER);
        userConfig.setEnabled(true);
        userConfig.setCars(new HashSet<Car>(){{
            Car car = new Car();
            car.setOwner(userConfig);
            car.setBeaconId("1");
            car.setRegisterNumber("AA1234AA");
            car.setMark("Batmobile");
            car.setColor("Black");
            car.setYearOfProduction("1939");
            car.setDescription("The Batmobile's shields are made of ceramic fractal armor panels");
            add(car);
        }});
        when(service.findUserConfigByEmail(MOCK_USER_EMAIL)).thenReturn(userConfig);
    }

}