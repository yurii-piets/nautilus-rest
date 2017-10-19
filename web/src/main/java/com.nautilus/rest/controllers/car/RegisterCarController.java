package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;

@RestController
public class RegisterCarController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileAccessUtility fileSaveUtility;

    @RequestMapping(value = "${car.register}", method = RequestMethod.POST)
    public ResponseEntity register(@RequestBody @Valid CarRegisterDTO carRegisterDTO) {
        Car car = new Car(carRegisterDTO);

        UserConfig user = service.findUserConfigByPhoneNumber(carRegisterDTO.getUserPhoneNumber());

        if(user == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);

        user.getCars().add(car);

        service.save(car);

        service.save(new HashSet<UserConfig>() {{
            add(user);
        }});

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "${car.register-car-photos}", method = RequestMethod.PUT)
    public ResponseEntity registerCarPhotos(@RequestParam String carId,
                                        @RequestParam("file") List<MultipartFile> files) {
        if (carId == null || carId.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileSaveUtility.saveCarPhotos(carId, files);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
