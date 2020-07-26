package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.service.HTMLRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HouseController {

    @Autowired
    HTMLRenderer htmlRenderer;

    @GetMapping("/newest")
    public String newestHouses() {
        try {
            return htmlRenderer.getHTMLNewestHouses();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    @GetMapping("/belowavg")
    public String belowAvgHouses() {
        try {
            return htmlRenderer.getHTMLBelowAvg();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }
}
