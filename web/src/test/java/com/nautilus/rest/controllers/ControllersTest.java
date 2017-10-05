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
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Year;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
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

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Test
    public void index() throws Exception {
        mockMvc.perform(get(properties.getIndex())).andExpect(status().isOk());
    }

    @Test
    public void userRegister() throws Exception {
        String registerJson = json(new RegisterUserDTO(
                "yurii@mail.com",
                "+48777111444",
                "Jack",
                "Core",
                "password")
        );

        mockMvc.perform(post(properties.getUserRegister())
                .contentType(contentType)
                .content(registerJson))
                .andExpect(status().isOk());
    }

    @Test
    public void userUpdate() throws Exception {
        mockMvc.perform(get(properties.getUserUpdate())).andExpect(status().isOk());
    }

    @Test
    public void carFound() throws Exception {
        CarStatusDTO carStatusDTO = new CarStatusDTO("11111", new CarLocationDTO(159.00, 11.0));
        String carStatusJson = json(carStatusDTO);
        mockMvc.perform(post(properties.getCarFound())
                .contentType(contentType)
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
        carRegisterDTO.setYearOfProduction(Year.of(1999));
        carRegisterDTO.setDescription("My car is very nice. I love my car");
        String registerJson = json(carRegisterDTO);

        mockMvc.perform(post(properties.getCarRegister())
                .contentType(contentType)
                .content(registerJson))
                .andExpect(status().isOk());
    }

    @Test
    public void carUpdate() throws Exception {
        mockMvc.perform(get(properties.getCarUpdate())).andExpect(status().isOk());
    }


    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
