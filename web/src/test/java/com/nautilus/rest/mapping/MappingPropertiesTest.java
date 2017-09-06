package com.nautilus.rest.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingPropertiesTest {

    @Autowired
    private MappingProperties properties;

    @Test
    public void getLogin() throws Exception {
        assertEquals("/login", properties.getUserLogin());
    }

    @Test
    public void getUserRegister() throws Exception {
        assertEquals("/registerUser", properties.getUserRegister());
    }

    @Test
    public void getUserUpdate() throws Exception {
        assertEquals("/updateUser", properties.getUserUpdate());
    }

    @Test
    public void getCarFound() throws Exception {
        assertEquals("/found", properties.getCarFound());
    }

    @Test
    public void getCarRegister() throws Exception {
        assertEquals("/registerCar", properties.getCarRegister());
    }

    @Test
    public void getCarUpdate() throws Exception {
        assertEquals("/updateCar", properties.getCarUpdate());
    }

    @Test
    public void getIndex() throws Exception {
        assertEquals("/index", properties.getIndex());
    }
}
