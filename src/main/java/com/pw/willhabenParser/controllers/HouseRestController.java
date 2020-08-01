package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/houses")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getAll(@RequestParam(required = false) String maxPrice, @RequestParam(required = false) String minSize, @RequestParam(required = false) String rooms, @RequestParam(required = false) String type, @RequestParam(required = false) String age, @RequestParam(required = false) String condition, @RequestParam(required = false) String minGround, @RequestParam(required = false) String postedAfter, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page, @RequestParam(required = false) String postedToday) {
        List<House> houseList = houseDao.getAllHouses();
        houseList = processFilters(maxPrice, minSize, rooms, type, age, condition, minGround, postedAfter, limit, page, houseList, postedToday);

        return houseList;
    }

    @GetMapping(value = "/{state}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForState(@PathVariable String state, @RequestParam(required = false) String maxPrice, @RequestParam(required = false) String minSize, @RequestParam(required = false) String rooms, @RequestParam(required = false) String type, @RequestParam(required = false) String age, @RequestParam(required = false) String condition, @RequestParam(required = false) String minGround, @RequestParam(required = false) String postedAfter, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page, @RequestParam(required = false) String postedToday) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .collect(Collectors.toList());

        houseList = processFilters(maxPrice, minSize, rooms, type, age, condition, minGround, postedAfter, limit, page, houseList, postedToday);
        return houseList;
    }

    @GetMapping(value = "/{state}/{district}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForDistrict(@PathVariable String state, @PathVariable String district, @RequestParam(required = false) String maxPrice, @RequestParam(required = false) String minSize, @RequestParam(required = false) String rooms, @RequestParam(required = false) String type, @RequestParam(required = false) String age, @RequestParam(required = false) String condition, @RequestParam(required = false) String minGround, @RequestParam(required = false) String postedAfter, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page, @RequestParam(required = false) String postedToday) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .filter(house -> house.getDistrictName() != null)
                .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                .collect(Collectors.toList());

        houseList = processFilters(maxPrice, minSize, rooms, type, age, condition, minGround, postedAfter, limit, page, houseList, postedToday);
        return houseList;
    }

    @GetMapping(value = "/{state}/{district}/{location}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getForLocation(@PathVariable String state, @PathVariable String district, @PathVariable String location, @RequestParam(required = false) String maxPrice, @RequestParam(required = false) String minSize, @RequestParam(required = false) String rooms, @RequestParam(required = false) String type, @RequestParam(required = false) String age, @RequestParam(required = false) String condition, @RequestParam(required = false) String minGround, @RequestParam(required = false) String postedAfter, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page, @RequestParam(required = false) String postedToday) {
        List<House> houseList = houseDao.getAllHouses().stream()
                .filter(house -> house.getStateName() != null)
                .filter(house -> house.getStateName().equalsIgnoreCase(state))
                .filter(house -> house.getDistrictName() != null)
                .filter(house -> house.getDistrictName().equalsIgnoreCase(district))
                .filter(house -> house.getLocationName() != null)
                .filter(house -> house.getLocationName().equalsIgnoreCase(location))
                .collect(Collectors.toList());

        houseList = processFilters(maxPrice, minSize, rooms, type, age, condition, minGround, postedAfter, limit, page, houseList, postedToday);
        return houseList;
    }

    private List<House> processFilters(String maxPrice, String minSize, String rooms, String type, String age, String condition, String minGround, String postedAfter, Integer limit, Integer page, List<House> houseList, String postedToday) {
        if (maxPrice != null) {
            houseList = filterForMaxPrice(houseList, maxPrice);
        }
        if (minSize != null) {
            houseList = filterForMinSize(houseList, minSize);
        }
        if (rooms != null) {
            houseList = filterForRooms(houseList, rooms);
        }
        if (type != null) {
            houseList = filterForType(houseList, type);
        }
        if (age != null) {
            houseList = filterForAge(houseList, age);
        }
        if (condition != null) {
            houseList = filterForCondition(houseList, condition);
        }
        if (minGround != null) {
            houseList = filterForMinGround(houseList, minGround);
        }
        if (postedAfter != null) {
            houseList = filterForPostedAfter(houseList, postedAfter);
        }
        if (postedToday != null) {
            houseList = filterForPostedAfter(houseList, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        }
        if (limit != null && page == null) {
            houseList = paginateList(houseList, limit, 1);
        }
        if (limit != null && page != null) {
            houseList = paginateList(houseList, limit, page);
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

    private List<House> filterForMinSize(List<House> houseList, String minSize) {
        if (isValidParameter(minSize)) {
            double minSizeVal = Double.parseDouble(minSize.replace(",", "."));
            List<House> result = houseList.stream()
                    .filter(house -> house.getSizeAsValue() != null)
                    .filter(house -> house.getSizeAsValue() >= minSizeVal)
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                result.add(houseList.stream()
                        .filter(house -> house.getSizeAsValue() != null)
                        .max(Comparator.comparingDouble(House::getSizeAsValue))
                        .orElse(null));
            }
            return result;

        } else {
            return houseList;
        }
    }

    private List<House> filterForRooms(List<House> houseList, String rooms) {
        if (isValidParameter(rooms)) {
            double roomsVal = Double.parseDouble(rooms.replace(",", "."));
            List<House> result = houseList.stream()
                    .filter(house -> house.getRoomsAsValue() != null)
                    .filter(house -> house.getRoomsAsValue() >= roomsVal)
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                result.add(houseList.stream()
                        .filter(house -> house.getRoomsAsValue() != null)
                        .max(Comparator.comparingDouble(House::getSizeAsValue))
                        .orElse(null));
            }
            return result;

        } else {
            return houseList;
        }
    }

    private List<House> filterForType(List<House> houseList, String type) {
        if (isValidParameter(type)) {
            List<House> result = houseList.stream()
                    .filter(house -> house.getObjectType() != null)
                    .filter(house -> house.getObjectType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            return result;
        } else {
            return houseList;
        }
    }

    private List<House> filterForAge(List<House> houseList, String age) {
        if (isValidParameter(age)) {
            List<House> result = houseList.stream()
                    .filter(house -> house.getAge() != null)
                    .filter(house -> house.getAge().equalsIgnoreCase(age))
                    .collect(Collectors.toList());
            return result;
        } else {
            return houseList;
        }
    }

    private List<House> filterForCondition(List<House> houseList, String condition) {
        if (isValidParameter(condition)) {
            List<House> result = houseList.stream()
                    .filter(house -> house.getCondition() != null)
                    .filter(house -> house.getCondition().equalsIgnoreCase(condition))
                    .collect(Collectors.toList());
            return result;
        } else {
            return houseList;
        }
    }

    private List<House> filterForMinGround(List<House> houseList, String minGround) {
        if (isValidParameter(minGround)) {
            double minGroundVal = Double.parseDouble(minGround.replace(",", "."));
            List<House> result = houseList.stream()
                    .filter(house -> house.getGroundAreaAsValue() != null)
                    .filter(house -> house.getGroundAreaAsValue() >= minGroundVal)
                    .collect(Collectors.toList());
            if (result.isEmpty()) {
                result.add(houseList.stream()
                        .filter(house -> house.getRoomsAsValue() != null)
                        .max(Comparator.comparingDouble(House::getGroundAreaAsValue))
                        .orElse(null));
            }
            return result;

        } else {
            return houseList;
        }
    }

    private List<House> filterForPostedAfter(List<House> houseList, String postedAfter) {
        if (isValidParameter(postedAfter)) {
            String postedAfterDateTimeStr = postedAfter + " 00:00";
            LocalDateTime postedAfterDateTime = LocalDateTime.parse(postedAfterDateTimeStr, DateTimeFormatter.ofPattern("ddMMyyyy HH:mm"));
            return filterForPostedAfter(houseList, postedAfterDateTime);
        } else {
            return houseList;
        }
    }

    private List<House> filterForPostedAfter(List<House> houseList, LocalDateTime postedAfter) {
        List<House> result = houseList.stream()
                .filter(house -> house.getEditLocalDateTime() != null)
                .filter(house -> house.getEditLocalDateTime().isAfter(postedAfter))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            result.add(houseList.stream()
                    .filter(house -> house.getEditLocalDateTime() != null)
                    .max(Comparator.comparing(House::getEditLocalDateTime))
                    .orElse(null));
        }
        return result;
    }


    private boolean isValidParameter(String param) {
        return param != null && !param.isEmpty() && !param.equalsIgnoreCase("0");
    }
}
