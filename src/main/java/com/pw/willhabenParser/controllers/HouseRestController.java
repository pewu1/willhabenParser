package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/houses")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getAll() {
        return houseDao.getAllHouses();
    }


    @GetMapping(value = "/{state}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForState(@PathVariable String state, @RequestParam(required = false) String maxPrice) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .collect(Collectors.toList());

        if (maxPrice != null) {
            houseList = filterForMaxPrice(houseList, maxPrice);
        }

        return houseList;
    }

    @GetMapping(value = "/{state}/{district}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForDistrict(@PathVariable String state, @PathVariable String district, @RequestParam(required = false) String maxPrice) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .filter(house -> house.getDistrictName() != null)
                .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                .collect(Collectors.toList());

        if (maxPrice != null) {
            houseList = filterForMaxPrice(houseList, maxPrice);
        }


        return houseList;
    }

    @GetMapping(value = "/{state}/{district}/{location}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForLocation(@PathVariable String state, @PathVariable String district, @PathVariable String location, @RequestParam(required = false) String maxPrice) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .filter(house -> house.getDistrictName() != null)
                .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                .filter(house -> house.getLocationName() != null)
                .filter(house -> house.getLocationName().equalsIgnoreCase(location))
                .collect(Collectors.toList());

        if (maxPrice != null) {
            houseList = filterForMaxPrice(houseList, maxPrice);
        }

        return houseList;
    }

    private List<House> paginateList(List<House> houseList, int limit, int page) {
        int skip = limit * (page - 1);
        return houseList.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }

    private List<House> filterForMaxPrice(List<House> houseList, String maxPrice) {
        if (isValidParameter(maxPrice)) {
            double maxPriceVal = Double.parseDouble(maxPrice.replace(",", "."));
            List<House> result = houseList.stream()
                    .filter(house -> house.getPriceAsValue() != null)
                    .filter(house -> house.getPriceAsValue() <= maxPriceVal)
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                result.add(houseList.stream()
                        .filter(house -> house.getPriceAsValue() != null)
                        .min(Comparator.comparingDouble(House::getPriceAsValue))
                        .orElse(null));
            }
            return result;

        } else {
            return houseList;
        }
    }

    private boolean isValidParameter(String param) {
        return param != null && !param.isEmpty() && !param.equalsIgnoreCase("0");
    }
}
