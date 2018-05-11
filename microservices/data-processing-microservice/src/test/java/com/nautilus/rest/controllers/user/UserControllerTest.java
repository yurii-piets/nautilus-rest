package com.nautilus.rest.controllers.user;

import com.nautilus.controller.user.UserController;
import com.nautilus.dto.PatchDto;
import com.nautilus.dto.car.CarDto;
import com.nautilus.dto.constants.RegisterError;
import com.nautilus.dto.user.RegisterUserDto;
import com.nautilus.dto.user.UserInfoDto;
import com.nautilus.node.UserNode;
import com.nautilus.rest.JsonUtil;
import com.nautilus.service.DataService;
import org.junit.BeforeClass;
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

import java.util.List;
import java.util.Set;

import static com.nautilus.MockUtil.buildMockUserWithCar;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockBean
    private DataService service;

    private static UserNode mockUser;

    @BeforeClass
    public static void beforeClass() {
        mockUser = buildMockUserWithCar();
    }

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
        UserInfoDto user = new UserInfoDto();
        user.setEmail(MOCK_USER_EMAIL);
        user.setPhoneNumber("111000222");
        user.setName("Luke");
        user.setSurname("Skywalker");
        String userJson = jsonUtil.json(user);
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        mockMvc.perform(get(UserController.USER_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(userJson));
    }

    @Test
    public void postWhenUserIsOk() throws Exception {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail(MOCK_USER_EMAIL);
        userDto.setPhoneNumber("111000222");
        userDto.setName("Luke");
        userDto.setSurname("Skywalker");
        userDto.setPassword(MOCK_USER_PASSWORD);
        String userJson = jsonUtil.json(userDto);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(true);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    public void postWhenEmailIsNotFree() throws Exception {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail(MOCK_USER_EMAIL);
        userDto.setPhoneNumber("111000222");
        userDto.setName("Luke");
        userDto.setSurname("Skywalker");
        userDto.setPassword(MOCK_USER_PASSWORD);

        String userJson = jsonUtil.json(userDto);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(true);

        List<RegisterError> statuses =List.of(RegisterError.EMAIL_NOT_FREE);

        mockMvc.perform(post(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(userJson))
                .andExpect(content().json(jsonUtil.json(statuses)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void postWhenEmailAndPhoneNumberIsNotFree() throws Exception {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail(MOCK_USER_EMAIL);
        userDto.setPhoneNumber("111000222");
        userDto.setName("Luke");
        userDto.setSurname("Skywalker");
        userDto.setPassword(MOCK_USER_PASSWORD);
        String userJson = jsonUtil.json(userDto);

        when(service.checkEmailIsFree(MOCK_USER_EMAIL)).thenReturn(false);
        when(service.checkPhoneNumberIsFree("111000222")).thenReturn(false);

        List<RegisterError> statuses = List.of(RegisterError.EMAIL_NOT_FREE, RegisterError.PHONE_NUMBER_NOT_FREE);

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
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/phoneNumber");
        op.setValue("123000789");
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldDoesNotExist() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/not_exist");
        op.setValue("asd");
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldEnabledNotAllowed() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/enabled");
        op.setValue(false);
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldLockedNotAllowed() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/locked");
        op.setValue(false);
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldExpiredNotAllowed() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/expired");
        op.setValue(false);
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldCredentialsExpiredNotAllowed() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("replace");
        op.setPath("/credentialsExpired");
        op.setValue(false);
        String patchJson = jsonUtil.json(Set.of(op));

        mockMvc.perform(patch(UserController.USER_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(patchJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenFieldAuthoritiesNotAllowed() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        PatchDto op = new PatchDto();
        op.setOp("remove");
        op.setPath("/authorities");
        op.setValue(false);
        String patchJson = jsonUtil.json(Set.of(op));

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
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

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
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL)).thenReturn(mockUser);

        CarDto expectedCar = new CarDto();
        expectedCar.setBeaconId("1");
        expectedCar.setRegisterNumber("AA1234AA");
        expectedCar.setMark("Batmobile");
        expectedCar.setColor("Black");
        expectedCar.setYearOfProduction("1939");
        expectedCar.setDescription("The Batmobile's shields are made of ceramic fractal armor panels");


        mockMvc.perform(get(UserController.USER_MAPPING + UserController.USER_CAR_MAPPING))
                .andExpect(content().json(jsonUtil.json(Set.of(expectedCar))))
                .andExpect(status().isOk());
    }
}