package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/houses")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getPageWithLimit(@PathVariable int limit, @PathVariable int page, @RequestParam Map<String, String> reqParam) {
        List<House> houseList = houseDao.getAllHouses();


        if (reqParam.containsKey("state")) {
            houseList = houseList.stream()
                    .filter(house -> house.getStateName().equalsIgnoreCase(reqParam.get("state")))
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("district")) {
            houseList = houseList.stream()
                    .filter(house -> house.getDistrictName().equalsIgnoreCase(reqParam.get("district")))
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("type")) {
            houseList = houseList.stream()
                    .filter(house -> house.getObjectType().equalsIgnoreCase(reqParam.get("type")))
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("location")) {
            houseList = houseList.stream()
                    .filter(house -> house.getLocationName().equalsIgnoreCase(reqParam.get("location")))
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("maxPrice")) {
            double maxPriceVal = Double.parseDouble(reqParam.get("maxPrice"));
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() <= maxPriceVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("minPrice")) {
            double minPriceVal = Double.parseDouble(reqParam.get("minPrice"));
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() >= minPriceVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("size")) {
            double sizeVal = Double.parseDouble(reqParam.get("size"));
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() >= sizeVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("maxSize")) {
            double maxSizeVal = Double.parseDouble(reqParam.get("maxSize"));
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() <= maxSizeVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("area")) {
            double minAreaVal = Double.parseDouble(reqParam.get("area"));
            houseList = houseList.stream()
                    .filter(house -> house.getGroundAreaAsValue() != null)
                    .filter(house -> house.getGroundAreaAsValue() >= minAreaVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("rooms")) {
            double roomsVal = Double.parseDouble(reqParam.get("rooms"));
            houseList = houseList.stream()
                    .filter(house -> house.getRoomsAsValue() != null)
                    .filter(house -> house.getRoomsAsValue() >= roomsVal)
                    .collect(Collectors.toList());
        }

        if (reqParam.containsKey("verified")) {
            boolean verifiedVal = reqParam.get("verified").equalsIgnoreCase("true");
            houseList = houseList.stream()
                    .filter(house -> house.isVerified() == verifiedVal)
                    .collect(Collectors.toList());
        }

        int skip = limit * (page - 1);
        return houseList.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }
}
