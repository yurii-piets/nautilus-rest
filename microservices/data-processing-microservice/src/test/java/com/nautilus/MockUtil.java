package com.nautilus;

import com.nautilus.dto.car.CarRegisterDto;
import com.nautilus.dto.car.Location;
import com.nautilus.dto.constants.Authorities;
import com.nautilus.dto.constants.CarStatus;
import com.nautilus.node.CarNode;
import com.nautilus.node.CarStatusSnapshotNode;
import com.nautilus.node.UserNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MockUtil {

    public static final String MOCK_USER_EMAIL = "luke.skywalker@nautilus.com";

    public static final String MOCK_USER_PASSWORD = "star_wars";

    public static final String MOCK_CAR_BEACON_ID = "2412794";

    public static UserNode buildMockUserWithCar() {
        UserNode user = new UserNode();
        user.setEmail(MOCK_USER_EMAIL);
        user.setPhoneNumber("111000222");
        user.setName("Luke");
        user.setSurname("Skywalker");
        user.setPassword(MOCK_USER_PASSWORD);
        user.setAuthorities(Set.of(Authorities.USER));
        user.setEnabled(true);
        user.setCars(new HashSet<>(){{
            CarNode car = new CarNode();
            car.setOwner(user);
            car.setBeaconId("1");
            car.setRegisterNumber("AA1234AA");
            car.setMark("Batmobile");
            car.setColor("Black");
            car.setYearOfProduction("1939");
            car.setDescription("The Batmobile's shields are made of ceramic fractal armor panels");
            add(car);
        }});

        return user;
    }

    public static CarRegisterDto buildMockCarRegisterDTO(CarNode car) {
        CarRegisterDto carRegisterDTO = new CarRegisterDto();
        carRegisterDTO.setBeaconId(car.getBeaconId());
        carRegisterDTO.setColor(car.getColor());
        carRegisterDTO.setDescription(car.getDescription());
        carRegisterDTO.setMark(car.getMark());
        carRegisterDTO.setModel(car.getModel());
        carRegisterDTO.setRegisterNumber(car.getRegisterNumber());
        carRegisterDTO.setYearOfProduction(car.getYearOfProduction());

        return carRegisterDTO;
    }

    public static CarNode buildMockCar() {
        CarNode mockCar = new CarNode();
        mockCar.setId(1L);
        mockCar.setBeaconId(MOCK_CAR_BEACON_ID);
        mockCar.setRegisterNumber("AA1234BB");
        mockCar.setMark("Batmobile");
        mockCar.setModel("XX");
        mockCar.setYearOfProduction("1939");
        mockCar.setDescription("Does batmobile needs a description?");
        mockCar.setStatus(CarStatus.OK);
        return mockCar;
    }

    public static UserNode buildMockUserConfig() {
        UserNode user = new UserNode();
        user.setEmail(MOCK_USER_EMAIL);
        user.setPhoneNumber("111000222");
        user.setName("Luke");
        user.setSurname("Skywalker");
        user.setPassword(MOCK_USER_PASSWORD);
        user.setAuthorities(Set.of(Authorities.USER));
        user.setEnabled(true);
        user.setCars(new LinkedHashSet<>());
        return user;
    }

    public static CarNode buildMockCarWithCaptures() {
        CarNode mockCar = new CarNode();
        mockCar.setId(1L);
        mockCar.setBeaconId(MOCK_CAR_BEACON_ID);
        mockCar.setRegisterNumber("AA1234BB");
        mockCar.setMark("Batmobile");
        mockCar.setModel("XX");
        mockCar.setYearOfProduction("1939");
        mockCar.setDescription("Does batmobile needs a description?");
        mockCar.setStatus(CarStatus.OK);

        Set<CarStatusSnapshotNode> snapshots = new LinkedHashSet<>();
        snapshots.add(new CarStatusSnapshotNode(new Location(BigDecimal.valueOf(19.20), BigDecimal.valueOf(12.22)), mockCar, LocalDateTime.now()));
        snapshots.add(new CarStatusSnapshotNode(new Location(BigDecimal.valueOf(29.20), BigDecimal.valueOf(22.22)), mockCar, LocalDateTime.now()));
        snapshots.add(new CarStatusSnapshotNode(new Location(BigDecimal.valueOf(39.20), BigDecimal.valueOf(32.22)), mockCar, LocalDateTime.now()));
        snapshots.add(new CarStatusSnapshotNode(new Location(BigDecimal.valueOf(49.20), BigDecimal.valueOf(42.22)), mockCar, LocalDateTime.now()));
        mockCar.setStatusSnapshots(snapshots);

        return mockCar;
    }
}
