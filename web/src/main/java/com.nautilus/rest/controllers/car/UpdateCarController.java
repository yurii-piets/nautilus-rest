package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.dto.car.CarUpdateDTO;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UpdateCarController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileAccessUtility fileSaveUtility;

    @RequestMapping(value = "${car.update}", method = RequestMethod.POST)
    public HttpStatus update(@RequestBody @Valid CarUpdateDTO updateDto) {

        Car car = service.findCarByBeaconId(updateDto.getBeaconId());

        if (car != null) {
            Car updateCar = Car.mergeWithUpdateDto(car, updateDto);
            service.save(updateCar);
        }

        return HttpStatus.OK;
    }


    @RequestMapping(value = "${car.update-photos}", method = RequestMethod.PUT)
    public HttpStatus updatePhotos(@RequestParam String carId,
                                   @RequestParam("file") List<MultipartFile> files) {
        if (carId == null || carId.isEmpty()) {
            return HttpStatus.NOT_ACCEPTABLE;
        }

        if(files == null || files.isEmpty()){
            return HttpStatus.NOT_ACCEPTABLE;
        }

        fileSaveUtility.deleteAndSaveCarPhotos(carId, files);

        return HttpStatus.ACCEPTED;
    }

}
