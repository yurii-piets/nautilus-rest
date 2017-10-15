package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileSaveUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class RegisterCarController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileSaveUtility fileSaveUtility;

    @RequestMapping(value = "${car.register}", method = RequestMethod.POST)
    public HttpStatus register(@RequestBody @Valid CarRegisterDTO carRegisterDTO) {
        Car car = new Car(carRegisterDTO);

        UserConfig user = service.findUserConfigByPhoneNumber(carRegisterDTO.getUserPhoneNumber());
        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);

        user.getCars().add(car);

        service.save(car);

        service.save(new HashSet<UserConfig>() {{
            add(user);
        }});

        return HttpStatus.OK;
    }

    @RequestMapping(value = "${car.register-car-photos}", method = RequestMethod.PUT)
    public HttpStatus registerCarPhotos(@RequestParam String carId,
                                        @RequestParam("file") List<MultipartFile> files) {
        if (carId == null || carId.isEmpty()) {
            return HttpStatus.NOT_ACCEPTABLE;
        }

        if (files == null || files.isEmpty()) {
            return HttpStatus.NOT_ACCEPTABLE;
        }

        fileSaveUtility.saveCarPhotos(carId, files);

        return HttpStatus.ACCEPTED;
    }
}
