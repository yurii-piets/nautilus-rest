package com.nautilus.rest.controllers.car;

import com.nautilus.JsonUtil;
import com.nautilus.domain.Car;
import com.nautilus.dto.car.CarLocationDTO;
import com.nautilus.dto.car.CarStatusDTO;
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

import java.sql.Timestamp;

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
    private GlobalService service;

    private static Car mockCar;

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
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn("wrong_email@nautilus.com");

        mockMvc.perform(get(CarCapturesController.CAR_FOUND_MAPPING + "/captures/" + MOCK_CAR_BEACON_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCaptures() throws Exception {
        when(service.findEmailByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(MOCK_USER_EMAIL);
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        String carCapturesContent = jsonUtil.json(mockCar.getStatusSnapshots());

        mockMvc.perform(get(CarCapturesController.CAR_FOUND_MAPPING + "/captures/" + MOCK_CAR_BEACON_ID))
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(carCapturesContent))
                .andExpect(status().isOk());
    }

    @Test
    public void postCarCaptureWhenNoAuth() throws Exception {
        String carStatusDTOJson = jsonUtil.json(new CarStatusDTO());

        mockMvc.perform(post(CarCapturesController.CAR_FOUND_MAPPING + "/capture/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(carStatusDTOJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCarCapture() throws Exception {
        when(service.findCarByBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar);

        when(service.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID))
                .thenReturn(mockCar.getStatus());

        String carStatusDTOJson
                = jsonUtil.json(new CarStatusDTO(new CarLocationDTO(11.22, 33.44), new Timestamp(1)));
        String okStatusJson = jsonUtil.json(mockCar.getStatus());

        mockMvc.perform(post(CarCapturesController.CAR_FOUND_MAPPING + "/capture/" + MOCK_CAR_BEACON_ID)
                .contentType(jsonUtil.getContentType())
                .content(carStatusDTOJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonUtil.getContentType()))
                .andExpect(content().json(okStatusJson));
    }
}