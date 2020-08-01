package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getPageWithLimit(@PathVariable int limit, @PathVariable int page) {
        List<House> houseList = houseDao.getAllHouses();

        int skip = limit * (page - 1);
        return houseList.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }

    @GetMapping(value = "/state/{state}/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getStatePageWithLimit(@PathVariable int limit, @PathVariable int page, @PathVariable String state) {
        List<House> houseList = houseDao.getAllHouses();

        int skip = limit * (page - 1);
        return houseList.stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .skip(skip).limit(limit).collect(Collectors.toList());
    }

    @GetMapping(value = "/district/{district}/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getDistrictPageWithLimit(@PathVariable int limit, @PathVariable int page, @PathVariable String district) {
        List<House> houseList = houseDao.getAllHouses();

        int skip = limit * (page - 1);
        return houseList.stream()
                .filter(house -> house.getDistrictName() != null)
                .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                .skip(skip).limit(limit).collect(Collectors.toList());
    }
}
