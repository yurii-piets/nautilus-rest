package com.nautilus.database.def;

import com.nautilus.constants.CarStatus;
import com.nautilus.constants.Status;
import com.nautilus.dto.car.CarRegisterDTO;

public interface CarService {

    CarStatus checkStatusByCarId(String id);
    Status addCarForUser(CarRegisterDTO carDto);
    Status updateCar(CarRegisterDTO carDto);
}
