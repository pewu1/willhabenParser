package com.pw.willhabenParser.controllers;

import com.pw.willhabenParser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    @Autowired
    ParserService parserService;

    @GetMapping("/newest")
    public String index() throws IOException {
        return parserService.getHTML();
    }


}
