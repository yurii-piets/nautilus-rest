package com.nautilus.rest.controllers.car;

import com.nautilus.JsonUtil;
import com.nautilus.domain.Car;
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

import static com.nautilus.MockUtil.MOCK_CAR_BEACON_ID;
import static com.nautilus.MockUtil.buildMockCarWithCaptures;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void postCarCaptureWhenNoAuth() {
        fail();
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void postCarCapture() {
        fail();
    }
}