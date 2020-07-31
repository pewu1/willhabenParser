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
@RequestMapping("rest")
public class HouseRestController {

    @Autowired
    HouseDao houseDao;

    @GetMapping(value = "/{limit}/{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<House> getPageWithLimit(@PathVariable int limit, @PathVariable int page) {
        int skip = limit * (page - 1);
        return houseDao.getAllHouses().stream().skip(skip).limit(limit).collect(Collectors.toList());
    }
}
