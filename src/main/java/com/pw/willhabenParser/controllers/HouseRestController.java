package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/houses")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getPageWithLimit(@PathVariable int limit, @PathVariable int page, HttpServletRequest request) {
        List<House> houseList = houseDao.getAllHouses();

        Map<String, String[]> paramsMap = request.getParameterMap();

        String state = paramsMap.get("state")[0];
        if (state != null && !state.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getStateName().equalsIgnoreCase(state))
                    .collect(Collectors.toList());
        }

        String district = paramsMap.get("district")[0];
        if (district != null && !district.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                    .collect(Collectors.toList());
        }

        String type = paramsMap.get("type")[0];
        if (type != null && !type.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getObjectType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        String location = paramsMap.get("location")[0];
        if (location != null && !location.isEmpty()) {
            houseList = houseList.stream()
                    .filter(house -> house.getLocationName().equalsIgnoreCase(location))
                    .collect(Collectors.toList());
        }

        String maxPrice = paramsMap.get("maxPrice")[0];
        if (maxPrice != null && !maxPrice.isEmpty()) {
            double maxPriceVal = Double.parseDouble(maxPrice);
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() <= maxPriceVal)
                    .collect(Collectors.toList());
        }

        String minPrice = paramsMap.get("minPrice")[0];
        if (minPrice != null && !minPrice.isEmpty()) {
            double minPriceVal = Double.parseDouble(minPrice);
            houseList = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() >= minPriceVal)
                    .collect(Collectors.toList());
        }

        String size = paramsMap.get("size")[0];
        if (size != null && !size.isEmpty()) {
            double sizeVal = Double.parseDouble(size);
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() >= sizeVal)
                    .collect(Collectors.toList());
        }

        String maxSize = paramsMap.get("maxSize")[0];
        if (maxSize != null && !maxSize.isEmpty()) {
            double maxSizeVal = Double.parseDouble(maxSize);
            houseList = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() <= maxSizeVal)
                    .collect(Collectors.toList());
        }

        String minArea = paramsMap.get("minArea")[0];
        if (minArea != null && !minArea.isEmpty()) {
            double minAreaVal = Double.parseDouble(minArea);
            houseList = houseList.stream()
                    .filter(house -> house.getGroundAreaAsValue() != null)
                    .filter(house -> house.getGroundAreaAsValue() >= minAreaVal)
                    .collect(Collectors.toList());
        }

        String rooms = paramsMap.get("rooms")[0];
        if (rooms != null && !rooms.isEmpty()) {
            double roomsVal = Double.parseDouble(rooms);
            houseList = houseList.stream()
                    .filter(house -> house.getRoomsAsValue() != null)
                    .filter(house -> house.getRoomsAsValue() >= roomsVal)
                    .collect(Collectors.toList());
        }

        String verified = paramsMap.get("verified")[0];
        if (verified != null && !verified.isEmpty()) {
            boolean verifiedVal = verified.equalsIgnoreCase("true");
            houseList = houseList.stream()
                    .filter(house -> house.isVerified() == verifiedVal)
                    .collect(Collectors.toList());
        }

        int skip = limit * (page - 1);
        return houseList.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }
}
