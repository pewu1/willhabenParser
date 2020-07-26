package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.service.HTMLRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HouseController {

    @Autowired
    HTMLRenderer htmlRenderer;

    @GetMapping("/newest")
    public String newestHouses() throws IOException {
        return htmlRenderer.getHTMLNewestHouses();
    }

    @GetMapping("/belowavg")
    public String belowAvgHouses() throws IOException {
        return htmlRenderer.getHTMLBelowAvg();
    }


}
