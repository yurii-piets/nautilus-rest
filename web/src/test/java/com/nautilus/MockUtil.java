package com.nautilus;

import com.nautilus.constants.Authorities;
import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class MockUtil {

    public static final String MOCK_USER_EMAIL = "luke.skywalker@nautilus.com";

    public static final String MOCK_USER_PASSWORD = "star_wars";

    public static final String MOCK_CAR_BEACON_ID = "2412794";

    public static UserConfig buildMockUserWithCar() {
        UserConfig userConfig = new UserConfig();
        userConfig.setEmail(MOCK_USER_EMAIL);
        userConfig.setPhoneNumber("111000222");
        userConfig.setName("Luke");
        userConfig.setSurname("Skywalker");
        userConfig.setPassword(MOCK_USER_PASSWORD);
        userConfig.setAuthorities(Authorities.USER);
        userConfig.setEnabled(true);
        userConfig.setCars(new HashSet<Car>(){{
            Car car = new Car();
            car.setOwner(userConfig);
            car.setBeaconId("1");
            car.setRegisterNumber("AA1234AA");
            car.setMark("Batmobile");
            car.setColor("Black");
            car.setYearOfProduction("1939");
            car.setDescription("The Batmobile's shields are made of ceramic fractal armor panels");
            add(car);
        }});

        return userConfig;
    }

    public static CarRegisterDTO buildMockCarRegisterDTO(Car car) {
        CarRegisterDTO carRegisterDTO = new CarRegisterDTO();
        carRegisterDTO.setBeaconId(car.getBeaconId());
        carRegisterDTO.setColor(car.getColor());
        carRegisterDTO.setDescription(car.getDescription());
        carRegisterDTO.setMark(car.getMark());
        carRegisterDTO.setModel(car.getModel());
        carRegisterDTO.setRegisterNumber(car.getRegisterNumber());
        carRegisterDTO.setYearOfProduction(car.getYearOfProduction());

        return carRegisterDTO;
    }

    public static Car buildMockCar() {
        Car mockCar = new Car();
        mockCar.setCarId(1L);
        mockCar.setBeaconId(MOCK_CAR_BEACON_ID);
        mockCar.setRegisterNumber("AA1234BB");
        mockCar.setMark("Batmobile");
        mockCar.setModel("XX");
        mockCar.setYearOfProduction("1939");
        mockCar.setDescription("Does batmobile needs a description?");
        mockCar.setStatus(CarStatus.OK);

        return mockCar;
    }

    public static UserConfig buildMockUserConfig() {
        UserConfig userConfig = new UserConfig();
        userConfig.setEmail(MOCK_USER_EMAIL);
        userConfig.setPhoneNumber("111000222");
        userConfig.setName("Luke");
        userConfig.setSurname("Skywalker");
        userConfig.setPassword(MOCK_USER_PASSWORD);
        userConfig.setAuthorities(Authorities.USER);
        userConfig.setEnabled(true);
        userConfig.setCars(new LinkedHashSet<>());

        return userConfig;
    }
}
