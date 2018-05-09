package com.nautilus.rest.controllers.car;

import com.nautilus.exception.WrongBeaconIdException;
import com.nautilus.node.CarNode;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.DataService;
import com.nautilus.service.file.FileUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nautilus.rest.controllers.car.CarPhotosController.CAR_PHOTOS_MAPPING;

@RestController
@RequestMapping(path = CAR_PHOTOS_MAPPING)
@RequiredArgsConstructor
public class CarPhotosController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public final static String CAR_PHOTOS_MAPPING = "/car/photos";

    private static final String MICRO_MAPPING = "/micro";

    private static final String CAPTURES_MAPPING = "/captures";

    private final DataService service;

    private final FileUtil fileUtil;

    private final AuthorizationService authorizationService;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.contextPath}")
    private String contextPath;

    @Value("${photos.max}")
    private Integer maxPhotos;

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> photos(@PathVariable String beaconId) {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        Collection<Integer> indices = fileUtil.getOriginalIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, null))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = MICRO_MAPPING + "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> micro(@PathVariable String beaconId) {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        Collection<Integer> indices = fileUtil.getOriginalIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, MICRO_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> captures(@PathVariable String beaconId) {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        Collection<Integer> indices = fileUtil.getCaptureIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, CAPTURES_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + MICRO_MAPPING + "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> capturesMicro(@PathVariable String beaconId) {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        Collection<Integer> indices = fileUtil.getCaptureIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, CAPTURES_MAPPING + MICRO_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> photo(@PathVariable String beaconId,
                                   @PathVariable Integer index
    ) throws IOException {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getOriginal(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);

    }

    @RequestMapping(value = MICRO_MAPPING + "/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> microPhoto(@PathVariable String beaconId,
                                        @PathVariable Integer index
    ) throws IOException {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getMicro(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + "/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> capturePhoto(@PathVariable String beaconId,
                                          @PathVariable Integer index
    ) throws IOException {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getCapture(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + MICRO_MAPPING + "/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> microCapture(@PathVariable String beaconId,
                                          @PathVariable Integer index
    ) throws IOException {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getCaptureMicro(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.POST)
    public ResponseEntity<?> register(@PathVariable String beaconId,
                                      @RequestParam("file") Collection<MultipartFile> files) throws IOException {

        authorizationService.hasAccessByBeaconId(beaconId);

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        if (files.size() > maxPhotos) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        fileUtil.saveOriginal(beaconId, files);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = CAPTURES_MAPPING + "/{beaconId}", method = RequestMethod.POST)
    public ResponseEntity<?> capture(@PathVariable String beaconId,
                                     @RequestParam("file") List<MultipartFile> files) throws IOException {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileUtil.saveCapture(beaconId, files);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        fileUtil.delete(beaconId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}/{index}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String beaconId,
                                    @PathVariable Integer index
    ) throws FileNotFoundException {

        authorizationService.hasAccessByBeaconId(beaconId);
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        fileUtil.delete(beaconId, index);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private URL buildPhotoUrl(String beaconId, int index, String postfix) {
        StringBuilder pathBuilder = new StringBuilder()
                .append(contextPath)
                .append(CAR_PHOTOS_MAPPING);

        if (!(postfix == null || postfix.isEmpty())) {
            pathBuilder.append(postfix);
        }

        pathBuilder
                .append("/").append(beaconId)
                .append("/").append(index);

        URL url = null;
        try {
            url = new URL(protocol, host, port, pathBuilder.toString());
        } catch (MalformedURLException e) {
            logger.error("Unexpected: ", e);
        }
        return url;
    }
}
