package com.nautilus.rest.controllers.car;

import com.nautilus.controller.car.CarController;
import com.nautilus.dto.PatchDto;
import com.nautilus.dto.car.CarRegisterDto;
import com.nautilus.dto.constants.CarStatus;
import com.nautilus.node.CarNode;
import com.nautilus.node.UserNode;
import com.nautilus.rest.JsonUtil;
import com.nautilus.service.DataService;
import com.nautilus.service.file.FileUtil;
import org.junit.Before;
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

import java.util.Set;

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
    private DataService service;

    @MockBean
    private FileUtil fileUtil;

    private static CarNode mockCar;

    private static UserNode mockUser;

    @Before
    public void before() {
        mockCar = buildMockCar();
        mockUser = buildMockUserConfig();
        mockCar.setOwner(mockUser);
        mockUser.getCars().add(mockCar);
    }

    @Test
    public void getCarInfoWhenNoAuth() throws Exception {
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar);

        mockMvc.perform(get(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarInfo() throws Exception {
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar);

        String carJson = jsonUtil.json(mockCar.toCarDto());
        mockMvc.perform(get(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(carJson));
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarStatusWhenCarIsOk() throws Exception {
        when(service.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar.getStatus());

        mockMvc.perform(get(CarController.CAR_MAPPING + "/status/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUtil.json(CarStatus.OK)));
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarStatusWhenCarIsStolen() throws Exception {
        mockCar.setStatus(CarStatus.STOLEN);
        when(service.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar.getStatus());

        mockMvc.perform(get(CarController.CAR_MAPPING + "/status/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUtil.json(CarStatus.STOLEN)));
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarStatusWhenCarIsTesting() throws Exception {
        mockCar.setStatus(CarStatus.TESTING);
        when(service.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(mockCar.getStatus());

        mockMvc.perform(get(CarController.CAR_MAPPING + "/status/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUtil.json(CarStatus.TESTING)));
    }

    @Test
    public void postCarWhenNoAuth() throws Exception {
        CarRegisterDto carRegisterDTO = buildMockCarRegisterDTO(mockCar);
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCarWhenCarFraud() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL))
                .thenReturn(mockUser);
        when(service.getCarNodeByBeaconIdOrRegisterNumber(mockCar.getBeaconId(), mockCar.getRegisterNumber()))
                .thenReturn(mockCar);

        CarRegisterDto carRegisterDTO = buildMockCarRegisterDTO(mockCar);
        String carJson = jsonUtil.json(carRegisterDTO);

        mockMvc.perform(post(CarController.CAR_MAPPING)
                .contentType(jsonUtil.getContentType())
                .content(carJson))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCar() throws Exception {
        when(service.getUserNodeByEmail(MOCK_USER_EMAIL))
                .thenReturn(mockUser);
        when(service.getCarNodeByBeaconIdOrRegisterNumber(mockCar.getBeaconId(), mockCar.getRegisterNumber()))
                .thenReturn(null);

        CarRegisterDto carRegisterDTO = buildMockCarRegisterDTO(mockCar);
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
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
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
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        PatchDto patch = new PatchDto();
        patch.setOp("replace");
        patch.setPath("/color");
        patch.setValue("White");

        String jsonPatch = jsonUtil.json(Set.of(patch));

        mockMvc.perform(patch(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(jsonPatch))
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
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn("wrong_email@nautilus.com");

        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void deleteCar() throws Exception {
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);
        doNothing().when(fileUtil).delete(MOCK_CAR_BEACON_ID);

        mockMvc.perform(delete(CarController.CAR_MAPPING + "/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isOk());
    }
}
