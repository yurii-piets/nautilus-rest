package com.nautilus.rest.controllers.car;

import com.nautilus.JsonUtil;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.service.file.FileUtil;
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

import static com.nautilus.MockUtil.MOCK_CAR_BEACON_ID;
import static com.nautilus.MockUtil.MOCK_USER_EMAIL;
import static com.nautilus.MockUtil.MOCK_USER_PASSWORD;
import static com.nautilus.MockUtil.buildMockCar;
import static com.nautilus.MockUtil.buildMockCarRegisterDTO;
import static com.nautilus.MockUtil.buildMockUserConfig;
import static org.mockito.Mockito.doNothing;
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
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockBean
    private GlobalService service;

    @MockBean
    private FileUtil fileUtil;

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
        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO(mockCar);
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

        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO(mockCar);
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

        CarRegisterDTO carRegisterDTO = buildMockCarRegisterDTO(mockCar);
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isOk());
    }

    @Test
    public void patchCarWhenNoAuth() throws Exception {
        mockMvc.perform(patch(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchWhenNoRightToModifyCar() throws Exception {
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn("wrong_email@nautilus.com");

        String jsonPatchContent = "[{\"op\": \"replace\", \"path\": \"/color\", \"value\": \"White\" }]";
        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(jsonPatchContent))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void patchCar() throws Exception {
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        String jsonPatchContent = "[{\"op\": \"replace\", \"path\": \"/color\", \"value\": \"White\" }]";
        mockMvc.perform(patch(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(jsonPatchContent))
        .andExpect(status().isOk());
    }

    @Test
    public void deleteCarWhenNoAuth() throws Exception {
        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void deleteCarWhenNoRightToModifyCar() throws Exception {
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn("wrong_email@nautilus.com");

        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void deleteCar() throws Exception {
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);
        doNothing().when(fileUtil).delete(MOCK_CAR_BEACON_ID);

        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk());
    }
}
