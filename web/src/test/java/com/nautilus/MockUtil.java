package com.nautilus;

import com.nautilus.constants.Authorities;
import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
        userConfig.setCars(new HashSet<Car>() {{
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

    public static Car buildMockCarWithCaptures() {
        Car mockCar = new Car();
        mockCar.setCarId(1L);
        mockCar.setBeaconId(MOCK_CAR_BEACON_ID);
        mockCar.setRegisterNumber("AA1234BB");
        mockCar.setMark("Batmobile");
        mockCar.setModel("XX");
        mockCar.setYearOfProduction("1939");
        mockCar.setDescription("Does batmobile needs a description?");
        mockCar.setStatus(CarStatus.OK);

        Set<CarStatusSnapshot> snapshots = new LinkedHashSet<>();
        snapshots.add(new CarStatusSnapshot(new CarLocation(19.20, 12.22), new Timestamp(1)));
        snapshots.add(new CarStatusSnapshot(new CarLocation(29.20, 22.22), new Timestamp(11)));
        snapshots.add(new CarStatusSnapshot(new CarLocation(39.20, 32.22), new Timestamp(111)));
        snapshots.add(new CarStatusSnapshot(new CarLocation(49.20, 42.22), new Timestamp(1111)));
        mockCar.setStatusSnapshots(snapshots);

        return mockCar;
    }
}
