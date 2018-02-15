package com.nautilus.rest.controllers;

import com.nautilus.dto.car.CarLocationDTO;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.dto.car.CarStatusDTO;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.rest.mapping.MappingProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllersTest {

    @Autowired
    private MappingProperties properties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonUtility jsonUtility;

    @Test
    public void userRegister() throws Exception {
        String registerJson = jsonUtility.json(new RegisterUserDTO(
                "yurii@mail.com",
                "+48777111444",
                "Jack",
                "Core",
                "password")
        );

        mockMvc.perform(post(properties.getUserRegister())
                .contentType(jsonUtility.getContentType())
                .content(registerJson))
                .andExpect(status().isOk());
    }

    @Test
    public void userUpdate() throws Exception {

//        mockMvc.perform(get(properties.getUserUpdate())).andExpect(status().isOk());
    }

    @Test
    public void carFound() throws Exception {
        CarStatusDTO carStatusDTO = new CarStatusDTO("11111", new CarLocationDTO(159.00, 11.0));
        String carStatusJson = jsonUtility.json(carStatusDTO);
        mockMvc.perform(post(properties.getCarFound())
                .contentType(jsonUtility.getContentType())
                .content(carStatusJson))
                .andExpect(status().isOk());
    }

    @Test
    public void carRegister() throws Exception {
        CarRegisterDTO carRegisterDTO = new CarRegisterDTO();
        carRegisterDTO.setBeaconId("77777");
        carRegisterDTO.setUserPhoneNumber("731739644");
        carRegisterDTO.setRegisterNumber("KR7903");
        carRegisterDTO.setMark("Alfa romeo");
        carRegisterDTO.setModel("146");
        carRegisterDTO.setColor("red");
        carRegisterDTO.setYearOfProduction("1999");
        carRegisterDTO.setDescription("My info is very nice. I love my info");
        String registerJson = jsonUtility.json(carRegisterDTO);

        mockMvc.perform(post(properties.getCarRegister())
                .contentType(jsonUtility.getContentType())
                .content(registerJson))
                .andExpect(status().isOk());
    }

    @Test
    public void carUpdate() throws Exception {
        mockMvc.perform(get(properties.getCarUpdate())).andExpect(status().isOk());
    }
}
