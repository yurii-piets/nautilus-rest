package com.nautilus.rest.controllers.car;

import com.nautilus.JsonUtil;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.services.GlobalService;
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

import java.util.LinkedHashSet;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {

    private static final String MOCK_CAR_BEACON_ID = "2412794";

    private static final String MOCK_USER_EMAIL = "luke.skywalker@nautilus.com";

    private static final String MOCK_USER_PASSWORD = "star_wars";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockBean
    private GlobalService service;

    private static Car mockCar;

    private static UserConfig mockUser;

    @BeforeClass
    public static void beforeClass() {
        mockCar = buildMockCar();
        mockUser = buildMockUserConfig();
        mockCar.setOwner(mockUser);
        mockUser.getCars().add(mockCar);
    }

    @Test
    public void getCarInfoWhenNoAuth() throws Exception {
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar);

        mockMvc.perform(get(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarInfo() throws Exception {
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar);

        String carJson = jsonUtil.json(mockCar);
        mockMvc.perform(get(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(carJson));
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarStatusWhenCarIsOk() throws Exception {
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar);

        mockMvc.perform(get(CarController.CAR_MAPPING + "/status/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(jsonUtil.json(CarStatus.OK)));
    }

    @Test
    public void postCarWhenNoAuth() throws Exception {
        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO();
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCarWhenCarFraud() throws Exception {
        when(service.findUserConfigByEmail(MOCK_USER_EMAIL))
                .thenReturn(mockUser);
        when(service.findCarByBeaconIdOrRegisterNumber(mockCar.getBeaconId(), mockCar.getRegisterNumber()))
                .thenReturn(mockCar);

        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO();
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCar() throws Exception {
        when(service.findUserConfigByEmail(MOCK_USER_EMAIL))
                .thenReturn(mockUser);
        when(service.findCarByBeaconIdOrRegisterNumber(mockCar.getBeaconId(), mockCar.getRegisterNumber()))
                .thenReturn(null);

        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO();
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isOk());
    }

    private CarRegisterDTO buildMockCarRegisterDTO() {
        CarRegisterDTO carRegisterDTO = new CarRegisterDTO();
        carRegisterDTO.setBeaconId(mockCar.getBeaconId());
        carRegisterDTO.setColor(mockCar.getColor());
        carRegisterDTO.setDescription(mockCar.getDescription());
        carRegisterDTO.setMark(mockCar.getMark());
        carRegisterDTO.setModel(mockCar.getModel());
        carRegisterDTO.setRegisterNumber(mockCar.getRegisterNumber());
        carRegisterDTO.setYearOfProduction(mockCar.getYearOfProduction());

        return carRegisterDTO;
    }

    private static Car buildMockCar() {
        Car mockCar = new Car();
        mockCar.setCarId(1L);
        mockCar.setBeaconId(MOCK_CAR_BEACON_ID);
        mockCar.setRegisterNumber("AA1234BB");
        mockCar.setMark("Batmobile");
        mockCar.setModel("XX");
        mockCar.setYearOfProduction("1939");
        mockCar.setDescription("Does batmobile needs a description?");
        mockCar.setStatus(CarStatus.OK);

        return mockCar;
    }

    private static UserConfig buildMockUserConfig() {
        UserConfig userConfig = new UserConfig();
        userConfig.setEmail(MOCK_USER_EMAIL);
        userConfig.setPhoneNumber("111000222");
        userConfig.setName("Luke");
        userConfig.setSurname("Skywalker");
        userConfig.setPassword(MOCK_USER_PASSWORD);
        userConfig.setAuthorities(Authorities.USER);
        userConfig.setEnabled(true);
        userConfig.setCars(new LinkedHashSet<>());

        return userConfig;
    }
}
