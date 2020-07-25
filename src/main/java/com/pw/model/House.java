package com.pw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.pw.Parser.DECIMAL_FORMAT;

public class House implements Serializable {


    String price;
    String location;
    String link;
    String size;
    String pictureLink;
    String rooms;
    String info;
    String objectType;
    String age;
    String condition;
    String groundArea;
    String heatingType;
    String editDate;
    String transactionFee;

    public House() {
    }

    public String getPrice() {
        return price;
    }

    @JsonIgnore
    public String getFormattedPrice() {
        if (this.price != null && !this.price.isEmpty()) {
            Double priceVal = Double.parseDouble(this.price);
            return getFormattedString(priceVal);
        } else {
            return this.price;
        }
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getGroundArea() {
        return groundArea;
    }

    public void setGroundArea(String groundArea) {
        this.groundArea = groundArea;
    }

    public String getHeatingType() {
        return heatingType;
    }

    public void setHeatingType(String heatingType) {
        this.heatingType = heatingType;
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

    @JsonIgnore
    public Double getAvgMeterPrice() {
        if (this.price == null || this.size == null) {
            return null;
        }
        try {
            double priceVal = Double.parseDouble(this.price);
            double areaVal = Double.parseDouble(this.size);
            return (priceVal / areaVal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonIgnore
    public String getAvgMeterPriceStr() {
        Double avgPrice = getAvgMeterPrice();

        if (avgPrice == null) {
            return null;
        }
        try {
            return getFormattedString(avgPrice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonIgnore
    public LocalDate getEditDateAsLocalDate() {
        if (this.getEditDate() == null || this.getEditDate().isEmpty()) {
            return null;
        }
        return LocalDate.parse(this.getEditDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String toString() {
        return Optional.ofNullable(editDate).orElse("") + ";" +
                Optional.ofNullable(getPostalCode()).orElse("") + ";" +
                Optional.of(getLocationName()).orElse("") + ";" +
                Optional.of(getDistrictName()).orElse("") + ";" +
                Optional.of(getStateName()).orElse("") + ";" +
                Optional.ofNullable(price).orElse("") + ";" +
                Optional.ofNullable(size).orElse("") + ";" +
                Optional.ofNullable(getAvgMeterPriceStr()).orElse("") + ";" +
                Optional.ofNullable(groundArea).orElse("") + ";" +
                Optional.ofNullable(rooms).orElse("") + ";" +
                Optional.ofNullable(objectType).orElse("") + ";" +
                Optional.ofNullable(age).orElse("") + ";" +
                Optional.ofNullable(condition).orElse("") + ";" +
                Optional.ofNullable(heatingType).orElse("") + ";" +
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
                Objects.equals(size, house.size) &&
                Objects.equals(rooms, house.rooms) &&
                Objects.equals(objectType, house.objectType) &&
                Objects.equals(age, house.age) &&
                Objects.equals(condition, house.condition) &&
                Objects.equals(groundArea, house.groundArea) &&
                Objects.equals(heatingType, house.heatingType) &&
                editDate.equals(house.editDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, location, size, rooms, objectType, age, condition, groundArea, heatingType, editDate);
    }

    @JsonIgnore
    private String getFormattedString(Number value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT, formatSymbols);
        return formatter.format(value);
    }

    private String parsePostalCode(String location) {
        if (location.length() < 6) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = location.toCharArray();
        for (int i = 0; i < 4; i++) {
            if (Character.isDigit(chars[i])) {
                sb.append(chars[i]);
            } else {
                return null;
            }
        }
        return sb.toString();
    }

    @JsonIgnore
    private String getPostalCode() {
        String[] locationSplitted = this.location.split(", ");
        return Stream.of(locationSplitted)
                .map(this::parsePostalCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");
    }

    @JsonIgnore
    private String getLocationName() {
        String[] locationSplitted = this.location.split(", ");
        Optional<String> resultOpt = Stream.of(locationSplitted)
                .filter(s -> parsePostalCode(s) != null)
                .findFirst();
        if (resultOpt.isEmpty()) {
            return locationSplitted[0];
        } else {
            return resultOpt.get().replaceAll("[0-9]", "").trim();
        }
    }

    @JsonIgnore
    private String getDistrictName() {
        String[] locationSplitted = this.location.split(", ");
        for (int i = 0; i < locationSplitted.length; i++) {
            if (parsePostalCode(locationSplitted[i]) != null) {
                if (i + 3 > locationSplitted.length) {
                    return getLocationName();
                } else {
                    return locationSplitted[i + 1];
                }
            }
        }
    return "";
    }

    @JsonIgnore
    private String getStateName() {
        return location.substring(location.lastIndexOf(", ") + 2);
    }
}