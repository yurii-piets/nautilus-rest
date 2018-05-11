package com.nautilus.rest.controllers.car;

import com.nautilus.controller.car.CarCapturesController;
import com.nautilus.dto.car.CarStatusSnapshotDto;
import com.nautilus.dto.car.Location;
import com.nautilus.node.CarNode;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nautilus.MockUtil.MOCK_CAR_BEACON_ID;
import static com.nautilus.MockUtil.buildMockCarWithCaptures;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarCapturesControllerTest {

    private static final String MOCK_USER_EMAIL = "luke.skywalker@nautilus.com";

    private static final String MOCK_USER_PASSWORD = "star_wars";

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService service;

    private static CarNode mockCar;

    @BeforeClass
    public static void beforeClass() {
        mockCar = buildMockCarWithCaptures();
    }

    @Test
    public void getCarCapturesWhenNoAuth() throws Exception {
        mockMvc.perform(get(CarCapturesController.CAR_FOUND_MAPPING + "/captures/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCapturesWhenNoRightToRead() throws Exception {
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn("wrong_email@nautilus.com");

        mockMvc.perform(get(CarCapturesController.CAR_FOUND_MAPPING + "/captures/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCaptures() throws Exception {
        when(service.getEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        String carCapturesContent = jsonUtil.json(mockCar.getStatusSnapshots());

        mockMvc.perform(get(CarCapturesController.CAR_FOUND_MAPPING + "/captures/" + MOCK_CAR_BEACON_ID))
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(carCapturesContent))
                .andExpect(status().isOk());
    }

    @Test
    public void postCarCaptureWhenNoAuth() throws Exception {
        String carStatusDTOJson = jsonUtil.json(new CarStatusSnapshotDto());

        mockMvc.perform(post(CarCapturesController.CAR_FOUND_MAPPING + "/capture/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(carStatusDTOJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCarCapture() throws Exception {
        when(service.getCarNodeByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        when(service.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar.getStatus());

        String carStatusDTOJson
                = jsonUtil.json(new CarStatusSnapshotDto(new Location(BigDecimal.valueOf(11.22), BigDecimal.valueOf(33.44)), LocalDateTime.now()));
        String okStatusJson = jsonUtil.json(mockCar.getStatus());

        mockMvc.perform(post(CarCapturesController.CAR_FOUND_MAPPING + "/capture/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(carStatusDTOJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(okStatusJson));
    }
}