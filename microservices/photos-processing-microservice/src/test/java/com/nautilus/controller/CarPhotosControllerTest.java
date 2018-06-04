package com.nautilus.controller;

import com.nautilus.dto.constants.CarStatus;
import com.nautilus.exception.WrongBeaconIdException;
import com.nautilus.node.CarNode;
import com.nautilus.node.UserNode;
import com.nautilus.service.DataService;
import com.nautilus.service.file.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static com.nautilus.controller.CarPhotosController.CAPTURES_MAPPING;
import static com.nautilus.controller.CarPhotosController.CAR_PHOTOS_MAPPING;
import static com.nautilus.controller.CarPhotosController.MICRO_MAPPING;
import static com.nautilus.controller.MockUtil.MOCK_CAR_BEACON_ID;
import static com.nautilus.controller.MockUtil.MOCK_USER_EMAIL;
import static com.nautilus.controller.MockUtil.MOCK_USER_PASSWORD;
import static com.nautilus.controller.MockUtil.buildMockCar;
import static com.nautilus.controller.MockUtil.buildMockUserConfig;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("component_test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarPhotosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockBean
    private DataService dataService;

    @MockBean
    private FileUtil fileUtil;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.contextPath}")
    private String contextPath;

    @Value("${photos.max}")
    private Integer maxPhotos;

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
    public void getCarWhenNoAuth() throws Exception {
        mockMvc.perform(get(CAR_PHOTOS_MAPPING.replace("{beaconId}", MOCK_CAR_BEACON_ID))).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarWhenNotExist() throws Exception {
        doThrow(WrongBeaconIdException.class).when(dataService).checkIfCarExistByBeaconId(anyString());

        mockMvc.perform(get(CAR_PHOTOS_MAPPING.replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarPhotosWhenCarIsStolenAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.STOLEN);

        mockMvc.perform(get(CAR_PHOTOS_MAPPING.replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarPhotosWhenCarIsOkAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);

        mockMvc.perform(get(CAR_PHOTOS_MAPPING.replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarWhenIsOk() throws Exception {
        doNothing().when(dataService).checkIfCarExistByBeaconId(anyString());
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);
        when(fileUtil.getOriginalIndices(MOCK_CAR_BEACON_ID)).thenReturn(List.of(1, 2, 3, 4));
        when(dataService.getEmailByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(MOCK_USER_EMAIL);

        String uris = jsonUtil.json(Set.of(
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/1",
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/2",
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/3",
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/4"));

        mockMvc.perform(get(CAR_PHOTOS_MAPPING.replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(uris));
    }

    @Test
    public void getCarMicroPhotoWhenNoAuth() throws Exception {
        mockMvc.perform(get((CAR_PHOTOS_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarMicroPhotoWhenNotExist() throws Exception {
        doThrow(WrongBeaconIdException.class).when(dataService).checkIfCarExistByBeaconId(anyString());

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarMicroPhotosWhenCarIsStolenAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.STOLEN);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarMicroPhotosWhenCarIsOkAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarMicroPhotosWhenIsOk() throws Exception {
        doNothing().when(dataService).checkIfCarExistByBeaconId(anyString());
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);
        when(fileUtil.getOriginalIndices(MOCK_CAR_BEACON_ID)).thenReturn(List.of(1, 2, 3, 4));
        when(dataService.getEmailByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(MOCK_USER_EMAIL);

        String uris = jsonUtil.json(Set.of(
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/1" + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/2" + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/3" + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/4" + MICRO_MAPPING));

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(uris));
    }

    @Test
    public void getCarCapturesPhotoWhenNoAuth() throws Exception {
        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCapturesPhotoWhenNotExist() throws Exception {
        doThrow(WrongBeaconIdException.class).when(dataService).checkIfCarExistByBeaconId(anyString());

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarCapturesPhotosWhenCarIsStolenAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.STOLEN);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarCapturesPhotosWhenCarIsOkAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCapturesPhotosWhenIsOk() throws Exception {
        doNothing().when(dataService).checkIfCarExistByBeaconId(anyString());
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);
        when(fileUtil.getCaptureIndices(MOCK_CAR_BEACON_ID)).thenReturn(List.of(1, 2, 3, 4));
        when(dataService.getEmailByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(MOCK_USER_EMAIL);

        String uris = jsonUtil.json(Set.of(
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/1" + CAPTURES_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/2" + CAPTURES_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/3" + CAPTURES_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/4" + CAPTURES_MAPPING));

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(uris));
    }
    @Test
    public void getCarCapturesMicroPhotoWhenNoAuth() throws Exception {
        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCapturesMicroPhotoWhenNotExist() throws Exception {
        doThrow(WrongBeaconIdException.class).when(dataService).checkIfCarExistByBeaconId(anyString());

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarCapturesMicroPhotosWhenCarIsStolenAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.STOLEN);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "wrong_name", password = "wrong_pass", roles = "USER")
    public void getCarCapturesMicroPhotosWhenCarIsOkAndUserNotAllowed() throws Exception {
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = MOCK_USER_EMAIL, password = MOCK_USER_PASSWORD, roles = "USER")
    public void getCarCapturesMicroPhotosWhenIsOk() throws Exception {
        doNothing().when(dataService).checkIfCarExistByBeaconId(anyString());
        when(dataService.getCarStatusByCarBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(CarStatus.OK);
        when(fileUtil.getCaptureIndices(MOCK_CAR_BEACON_ID)).thenReturn(List.of(1, 2, 3, 4));
        when(dataService.getEmailByBeaconId(MOCK_CAR_BEACON_ID)).thenReturn(MOCK_USER_EMAIL);

        String uris = jsonUtil.json(Set.of(
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/1" + CAPTURES_MAPPING + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/2" + CAPTURES_MAPPING + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/3" + CAPTURES_MAPPING + MICRO_MAPPING,
                protocol + "://" + host + ":" + port + contextPath + "car/" + MOCK_CAR_BEACON_ID + "/photos/4" + CAPTURES_MAPPING + MICRO_MAPPING));

        mockMvc.perform(get((CAR_PHOTOS_MAPPING + CAPTURES_MAPPING + MICRO_MAPPING).replace("{beaconId}", MOCK_CAR_BEACON_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(uris));
    }
}
