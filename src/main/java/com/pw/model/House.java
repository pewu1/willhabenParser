package com.pw.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class House implements Serializable {
    String price;
    String location;
    String link;
    String area;
    String pictureLink;
    String rooms;
    String agent;
    String info;
    String objectType;
    String buildTime;
    String state;
    String groundArea;
    String warmType;
    String editDate;
    String transactionFee;


    public House() {
    }

    public House(String price, String location, String link, String area, String pictureLink) {
        this.price = price;
        this.location = location;
        this.link = link;
        this.area = area;
        this.pictureLink = pictureLink;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGroundArea() {
        return groundArea;
    }

    public void setGroundArea(String groundArea) {
        this.groundArea = groundArea;
    }

    public String getWarmType() {
        return warmType;
    }

    public void setWarmType(String warmType) {
        this.warmType = warmType;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public String getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(String transactionFee) {
        this.transactionFee = transactionFee;
    }

    public Double getAvgMeterPrice() {
        if (this.price == null || this.area == null) {
            return null;
        }
        try {
            double priceVal = Double.parseDouble(this.price);
            double areaVal = Double.parseDouble(this.area);
            return (priceVal / areaVal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getAvgMeterPriceStr() {
        Double avgPrice = getAvgMeterPrice();

        if (avgPrice == null) {
            return null;
        }
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(avgPrice).replaceAll(Pattern.quote("."), ",");
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public LocalDate getEditDateAsLocalDate() {
        if (this.getEditDate() == null || this.getEditDate().isEmpty()) {
            return null;
        }
        return LocalDate.parse(this.getEditDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String toString() {
        return Optional.ofNullable(editDate).orElse("") + ";" +
                Optional.ofNullable(location).orElse("") + ";" +
                Optional.ofNullable(price).orElse("") + ";" +
                Optional.ofNullable(area).orElse("") + ";" +
                Optional.ofNullable(getAvgMeterPriceStr()).orElse("") + ";" +
                Optional.ofNullable(groundArea).orElse("") + ";" +
                Optional.ofNullable(rooms).orElse("") + ";" +
                Optional.ofNullable(objectType).orElse("") + ";" +
                Optional.ofNullable(buildTime).orElse("") + ";" +
                Optional.ofNullable(state).orElse("") + ";" +
                Optional.ofNullable(warmType).orElse("") + ";" +
                Optional.ofNullable(info).orElse("") + ";" +
                Optional.ofNullable(getTransactionFee()).orElse("") + ";" +
                Optional.ofNullable(link).orElse("") + ";" +
                Optional.ofNullable(pictureLink).orElse("") + ";" +
                hashCode() + ";" +
                "\r\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return Objects.equals(price, house.price) &&
                Objects.equals(location, house.location) &&
                Objects.equals(area, house.area) &&
                Objects.equals(rooms, house.rooms) &&
                Objects.equals(objectType, house.objectType) &&
                Objects.equals(buildTime, house.buildTime) &&
                Objects.equals(state, house.state) &&
                Objects.equals(groundArea, house.groundArea) &&
                Objects.equals(warmType, house.warmType) &&
                editDate.equals(house.editDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, location, area, rooms, objectType, buildTime, state, groundArea, warmType, editDate);
    }
}
