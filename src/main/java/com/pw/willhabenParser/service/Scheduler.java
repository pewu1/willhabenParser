package com.pw.willhabenParser.service;

import com.pw.willhabenParser.dao.HousesDao;
import com.pw.willhabenParser.model.House;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class Scheduler {

    private static final String MAIN_URL = "https://www.willhaben.at";
    private static final String BUY_HOUSE_URL = MAIN_URL + "/iad/immobilien/haus-kaufen/haus-angebote?rows=100&areaId=";

    @Autowired
    HousesDao dao;

    @Autowired
    ParserService parserService;

    @Scheduled(fixedRate = 3600000)
    public void fetchData() {
        try {
            fetchDataForArea(4);
            for (int i = 1; i <= 9; i++) {
                fetchDataForArea(i);
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void fetchDataForArea(int areaNumber) throws IOException {
        Document doc = Jsoup.connect(BUY_HOUSE_URL + areaNumber).get();
        Element resultList = doc.getElementById("resultlist");
        if (resultList == null) {
            System.out.println("Error, no result list");
        }
        int counter = 2;

        while (resultList != null) {

            Elements entries = resultList.getElementsByClass("search-result-entry").stream()
                    .filter(entry -> entry.hasAttr("itemtype"))
                    .collect(Collectors.toCollection(Elements::new));

            for (int i = 0; i < entries.size(); i++) {
                System.out.println("Processing " + (i + 1) + "/" + entries.size() + " page: " + (counter - 1) + "for area code: " + areaNumber);
                try {
                    House house = parserService.parseEntry(entries.get(i));
                    dao.persist(house);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }

            doc = Jsoup.connect(BUY_HOUSE_URL + "&page=" + counter).get();
            resultList = doc.getElementById("resultlist");
            counter++;
        }
    }
}
