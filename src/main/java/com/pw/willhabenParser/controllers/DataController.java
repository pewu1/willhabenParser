package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.service.HTMLRenderer;
import com.pw.willhabenParser.service.ScheduledDataFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    @Autowired
    ScheduledDataFetchService scheduledDataFetchService;

    @Autowired
    HTMLRenderer htmlRenderer;

    @GetMapping("/newest")
    public String newestHouses() {
        return htmlRenderer.getHTMLNewestHouses();
    }

    @GetMapping("/belowavg")
    public String belowAvgHouses() {
        return htmlRenderer.getHTMLBelowAvg();
    }

    @GetMapping("/oo")
    public String newestUpperAustria() {
        return htmlRenderer.getHTMLNewestUpperAustria();
    }

    @GetMapping("/fetchdata")
    public String keepAlive() {
        try {
            scheduledDataFetchService.start();
        } catch (IllegalStateException e) {
            return e.getLocalizedMessage();
        }
        return "OK";
    }
}