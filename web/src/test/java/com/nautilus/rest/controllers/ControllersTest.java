package com.nautilus.rest.controllers;

import com.nautilus.dto.user.LoginDTO;
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
    public void userLogin() throws Exception {
        String loginJson = json(new LoginDTO("yurii@mail.com", "password"));

        mockMvc.perform(post(properties.getUserLogin())
                .contentType(contentType)
                .content(loginJson))
                .andExpect(status().isOk());
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
        mockMvc.perform(get(properties.getCarFound())).andExpect(status().isOk());
    }

    @Test
    public void carRegister() throws Exception {
        mockMvc.perform(get(properties.getCarRegister())).andExpect(status().isOk());
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
