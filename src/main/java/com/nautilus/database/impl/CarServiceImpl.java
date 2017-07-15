package com.nautilus.database.impl;

import com.nautilus.constants.CarStatus;
import com.nautilus.constants.Status;
import com.nautilus.database.def.CarService;
import com.nautilus.dto.car.CarRegisterDTO;
import org.springframework.stereotype.Component;

@Component
public class CarServiceImpl implements CarService {

    @Override
    public CarStatus checkStatusByCarId(String id) {
        return CarStatus.OK;
    }

    @Override
    public Status addCarForUser(CarRegisterDTO carDto) {
        return Status.ACCEPTED;
    }

    @Override
    public Status updateCar(CarRegisterDTO carDto) {
        return Status.ACCEPTED;
    }
}

