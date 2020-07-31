package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("rest")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getPageWithLimit(@PathVariable int limit, @PathVariable int page, @RequestParam String state, @RequestParam String district, @RequestParam String location, @RequestParam Long maxPrice, @RequestParam Long minPrice, @RequestParam Integer minSize, @RequestParam Integer maxSize, @RequestParam Integer minArea, @RequestParam Integer minRooms, @RequestParam String type, @RequestParam Boolean verified) {
        List<House> houseList = houseDao.getAllHouses();

        if (state != null && !state.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getStateName().equalsIgnoreCase(state))
                    .collect(Collectors.toList());
        }

        if (district != null && !district.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                    .collect(Collectors.toList());
        }

        if (type != null && !type.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getObjectType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        if (location != null && !location.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getLocationName().equalsIgnoreCase(location))
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() >= minPrice)
                    .collect(Collectors.toList());
        }

        if (minSize != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() >= minSize)
                    .collect(Collectors.toList());
        }

        if (maxSize != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() <= maxSize)
                    .collect(Collectors.toList());
        }

        if (minArea != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getGroundAreaAsValue() != null)
                    .filter(house -> house.getGroundAreaAsValue() >= minArea)
                    .collect(Collectors.toList());
        }

        if (minRooms != null) {
            houseList = houseList.stream()
                    .filter(house -> house.getRoomsAsValue() != null)
                    .filter(house -> house.getRoomsAsValue() >= minRooms)
                    .collect(Collectors.toList());
        }

        if (verified != null) {
            houseList = houseList.stream()
                    .filter(house -> house.isVerified() == verified)
                    .collect(Collectors.toList());
        }

        int skip = limit * (page - 1);
        return houseList.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }
}
