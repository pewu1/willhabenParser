package com.pw.willhabenParser.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@DynamoDBTable(tableName = "houses")
public class House implements Serializable {

    private String id;
    private String price;
    private String location;
    private String link;
    private String size;
    private String pictureLink;
    private String rooms;
    private String info;
    private String objectType;
    private String age;
    private String condition;
    private String groundArea;
    private String heatingType;
    private String editDate;
    private String editTime;
    private String transactionFee;
    private boolean isVerified;
    private String postalCode;
    private String locationName;
    private String stateName;
    private String districtName;

    public House() {
    }


    @JsonIgnore
    @DynamoDBAttribute
    public boolean isVerified() {
        return isVerified;
    }

    @JsonIgnore
    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    @JsonIgnore
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public String getPrice() {
        return price;
    }

    @JsonIgnore
    @DynamoDBIgnore
    public String getFormattedPrice() {
        if (this.price != null && !this.price.isEmpty()) {
            Double priceVal = Double.parseDouble(this.price.replace(",", "."));
            return getFormattedString(priceVal);
        } else {
            return this.price;
        }
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @DynamoDBAttribute
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        setPostalCode();
        setLocationName();
        setDistrictName();
        setStateName();
    }

    @DynamoDBAttribute
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @DynamoDBAttribute
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @DynamoDBAttribute
    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    @DynamoDBAttribute
    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    @DynamoDBAttribute
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @DynamoDBAttribute
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @DynamoDBAttribute
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @DynamoDBAttribute
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @DynamoDBAttribute
    public String getGroundArea() {
        return groundArea;
    }

    public void setGroundArea(String groundArea) {
        this.groundArea = groundArea;
    }

    @DynamoDBAttribute
    public String getHeatingType() {
        return heatingType;
    }

    public void setHeatingType(String heatingType) {
        this.heatingType = heatingType;
    }

    @DynamoDBAttribute
    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    @DynamoDBAttribute
    public String getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(String transactionFee) {
        this.transactionFee = transactionFee;
    }

    @DynamoDBAttribute
    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    @JsonIgnore
    @DynamoDBIgnore
    public Double getAvgMeterPrice() {
        if (this.price == null || this.size == null) {
            return null;
        }
        try {
            double priceVal = Double.parseDouble(this.price.replace(",", "."));
            double areaVal = Double.parseDouble(this.size.replace(",", "."));
            return (priceVal / areaVal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonIgnore
    @DynamoDBIgnore
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
    @DynamoDBIgnore
    public LocalDateTime getEditLocalDateTime() {
        if (this.getEditDate() == null || this.getEditDate().isEmpty()) {
            return null;
        }
        String editDateTime;
        if (this.getEditTime() == null || this.getEditTime().isEmpty()) {
            editDateTime = this.getEditDate() + " 00:00";
        } else {
            editDateTime = this.getEditDate() + " " + this.getEditTime();
        }
        return LocalDateTime.parse(editDateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
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
    @DynamoDBIgnore
    private String getFormattedString(Number value) {
        final String DECIMAL_FORMAT = "###,###.##";
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

    public void setPostalCode() {
        String[] locationSplitted = this.location.split(", ");
        this.postalCode = Stream.of(locationSplitted)
                .map(this::parsePostalCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");
    }

    public void setLocationName() {
        String[] locationSplitted = this.location.split(", ");

        if (this.postalCode == null || this.postalCode.isEmpty()) {
            this.locationName = locationSplitted[0];
        } else {
            Optional<String> resultOpt = Stream.of(locationSplitted)
                    .filter(s -> s.startsWith(this.postalCode))
                    .findFirst();

            if (resultOpt.isEmpty()) {
                this.locationName = locationSplitted[0];
            } else {
                this.locationName = resultOpt.get().replaceAll("[0-9]", "").trim();
            }
        }
    }

    public void setDistrictName() {
        String[] locationSplitted = this.location.split(", ");
        for (int i = 0; i < locationSplitted.length; i++) {
            if (parsePostalCode(locationSplitted[i]) != null) {
                if (i + 3 > locationSplitted.length) {
                    this.districtName = getLocationName();
                } else {
                    this.districtName = locationSplitted[i + 1];
                }
            }
        }
        this.districtName = "";
    }

    public void setStateName() {
        this.stateName = location.substring(location.lastIndexOf(", ") + 2);
    }

    @DynamoDBAttribute
    public String getPostalCode() {
        return postalCode;
    }

    @DynamoDBAttribute
    public String getLocationName() {
        return locationName;
    }

    @DynamoDBAttribute
    public String getStateName() {
        return stateName;
    }

    @DynamoDBAttribute
    public String getDistrictName() {
        return districtName;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}