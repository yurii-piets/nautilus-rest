package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;

import static com.nautilus.rest.controllers.car.RegisterCarController.REGISTER_CAR_MAPPING;

@RestController
@RequestMapping(path = REGISTER_CAR_MAPPING)
@RequiredArgsConstructor
public class RegisterCarController {

    public final static String REGISTER_CAR_MAPPING = "/car/register";

    private final GlobalService service;

    private final FileAccessUtility fileSaveUtility;

    @RequestMapping(value = "/{phoneNumber}", method = RequestMethod.POST)
    public ResponseEntity<?> register(@PathVariable String phoneNumber,
                                   @RequestBody @Valid CarRegisterDTO carRegisterDTO) {

        UserConfig user = service.findUserConfigByPhoneNumber(phoneNumber);

        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Car duplicateCar = service.findCarByBeaconIdOrRegisterNumber(
                carRegisterDTO.getBeaconId(), carRegisterDTO.getRegisterNumber());

        if(duplicateCar != null){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        Car car = new Car(carRegisterDTO);

        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);

        user.getCars().add(car);

        service.save(car);

        service.save(new HashSet<UserConfig>() {{
            add(user);
        }});

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/photos/{carId}", method = RequestMethod.PUT)
    public ResponseEntity<?> registerCarPhotos(@PathVariable String carId,
                                            @RequestParam("file") List<MultipartFile> files) {

        Car car = service.getCarById(carId);

        if (car == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileSaveUtility.saveCarPhotos(carId, files);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
