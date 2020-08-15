package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.service.ScheduledDataFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class DataController {

    @Autowired
    ScheduledDataFetchService scheduledDataFetchService;

    @GetMapping("/")
    public RedirectView redirectToIndex(RedirectAttributes attributes) {
        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView("/index.html");
    }

    @GetMapping("/fetchdata")
    public String keepAlive() {
        try {
            scheduledDataFetchService.start();
        } catch (IllegalStateException e) {
            return "Thread is already running";
        }
        return "OK";
    }
}
