package com.nautilus.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("http://photos-processing")
public interface CarPhotosClient {

    @RequestMapping(value = "/car/{beaconId}/photos", method = RequestMethod.DELETE)
    void deleteCarPhotos(@PathVariable("beaconId") String beaconId);
}
