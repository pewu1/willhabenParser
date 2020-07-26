package com.pw.willhabenParser.service;

import com.pw.willhabenParser.dao.HousesDao;
import com.pw.willhabenParser.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class HTMLRenderer {

    @Autowired
    HousesDao dao;

    public String getHTMLNewestHouses() throws IOException {
        return getHTML(dao.getAllHouses());
    }

    public String getHTMLBelowAvg() throws IOException {
        return getHTML(dao.getHousesBelowAveragePrice());
    }

    private String getHTML(List<House> houseList) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("<table style=\"width:100%\"><tr>" +
                "<th width=\"200\">Photo</th>" +
                "<th width=\"40\">Size</th>" +
                "<th>Rooms</th>" +
                "<th width=\"40\">Ground</th>" +
                "<th width=\"100\">Price</th>" +
                "<th width=\"200\">Location</th>" +
                "<th>Type</th>" +
                "<th>Heating</th>" +
                "<th>Condition</th>" +
                "<th>Age</th>" +
                "<th width=\"150\">Additional info</th>" +
                "</tr>");
        houseList.stream()
                .map(this::getHTMLforHouse)
                .forEach(stringBuilder::append);

        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    private String getHTMLforHouse(House house) {
        return "<tr>" +
                "<td><center><a href=\"" + Optional.ofNullable(house.getLink()).orElse("-") + "\">" +
                "<img src=\"" + Optional.ofNullable(house.getPictureLink()).orElse("-") + "\"width=\"200\"/>" +
                "</a></center></td>" +
                "<td><center>" + Optional.ofNullable(house.getSize()).orElse("-") + " m2</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getRooms()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getGroundArea()).orElse("-") + " m2</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getFormattedPrice()).orElse("-") + " EUR<br>(" +
                Optional.ofNullable(house.getAvgMeterPriceStr()).orElse("-") + "/m2)</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getLocation()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getObjectType()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getHeatingType()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getCondition()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getAge()).orElse("-") + "</center></td>" +
                "<td><center>" + Optional.ofNullable(house.getInfo()).orElse("-") + "</center></td>" +
                "</tr>";
    }
}
