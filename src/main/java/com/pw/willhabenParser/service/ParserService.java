package com.pw.willhabenParser.service;

import com.pw.willhabenParser.dao.HouseDao;
import com.pw.willhabenParser.model.House;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.pw.willhabenParser.service.ScheduledDataFetchService.MAIN_URL;

@Component
public class ParserService {

    @Autowired
    HouseDao dao;

    public House parseEntry(Element entry) throws IllegalArgumentException, IOException {
        House house = new House();
        Element imageSection = entry.getElementsByClass("image-section").first();
        house.setLink(MAIN_URL + imageSection.selectFirst("a").attributes().get("href"));

        if (house.getLink().contains("andere-laender")) {
            throw new IllegalArgumentException("House is abroad");
        }

        house.setPictureLink(imageSection.selectFirst("img").attributes().get("src"));

        if (dao.getErrorLinks().contains(house.getLink()) && dao.getErrorLinks().contains(house.getPictureLink())) {
            throw new IllegalArgumentException("House is already marked as error");
        }

        parseDetailsIfNotAlreadyPersisted(house);
        return house;
    }

    private void parseDetailsIfNotAlreadyPersisted(House house) throws IOException {
        int linksNumber = dao.getVerifiedLinks().size();
        dao.getVerifiedLinks().add(house.getLink());
        dao.getVerifiedLinks().add(house.getPictureLink());
        if (linksNumber == dao.getVerifiedLinks().size() - 2) {
            parseDetails(house);
        } else {
            throw new IllegalArgumentException("House already persisted");
        }
    }

    private void parseDetails(House house) throws IOException {
        if (house == null || house.getLink() == null) {
            throw new IllegalArgumentException("House link is null");
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
        Document doc = Jsoup.connect(house.getLink()).get();
        if (doc == null) {
            throw new IOException("Unable to connect");
        }
        Element contentSection = doc
                .getElementById("skip-to-content");

        if (contentSection == null) {
            throw new IOException("No content section");
        }

        Element detailsSection = contentSection
                .getElementsByAttributeValue("order", "2")
                .first();

        parseLocation(house, detailsSection);

        Elements attributeGroup = detailsSection
                .getElementsByAttributeValue("data-testid", "attribute-group")
                .first()
                .getElementsByAttributeValue("data-testid", "attribute");

        attributeGroup.forEach(element -> processAttributeElement(element, house));
        parsePrice(house, doc);
        parseTransactionFee(house, doc);
        parseEditDateTime(house, doc);
    }

    private void parseEditDateTime(House house, Document doc) {
        String editDateTime = doc.getElementsByAttributeValue("data-testid", "ad-detail-ad-edit-date").html();
        house.setEditDate(editDateTime.split(", ")[0]);
        house.setEditTime(editDateTime.split(", ")[1].substring(0, 5));
    }

    private void parseTransactionFee(House house, Document doc) {
        Element priceInfoSection = doc
                .getElementsByAttributeValue("data-testid", "price-information-box")
                .first();

        String transactionFee = null;
        if (priceInfoSection != null) {
            transactionFee = priceInfoSection
                    .getElementsByAttributeValue("data-testid", "price-information-freetext-attribute-value-0")
                    .html()
                    .replaceAll("&nbsp;", " ");
        }
        house.setTransactionFee(transactionFee);
    }

    private void parsePrice(House house, Document doc) {
        String price = doc.getElementsByAttributeValue("data-testid", "contact-box-price-box-price-value")
                .html()
                .replaceAll("[^0-9,]", "")
                .replaceAll(" ", "");
        house.setPrice(price);
    }

    private void parseLocation(House house, Element detailsSection) {
        String location = detailsSection
                .getElementsByAttributeValue("data-testid", "object-location-address")
                .first()
                .html();
        house.setLocation(location.replaceAll("&amp;", "&"));
        house.setPostalCode();
        house.setLocationName();
        house.setDistrictName();
        house.setStateName();
    }

    private void processAttributeElement(Element element, House house) {
        String attributeName = element.getElementsByAttributeValue("data-testid", "attribute-name").first().child(0).html();
        String attributeValue = element.getElementsByAttributeValue("data-testid", "attribute-value").first().html();

        switch (attributeName) {
            case "Objekttyp":
                house.setObjectType(attributeValue);
                break;
            case "Bautyp":
                house.setAge(attributeValue);
                break;
            case "Zustand":
                house.setCondition(attributeValue);
                break;
            case "Wohnfläche":
                house.setSize(attributeValue.replaceAll("[^0-9,]", ""));
                break;
            case "Zimmer":
                house.setRooms(attributeValue);
                break;
            case "Heizung":
                house.setHeatingType(attributeValue);
                break;
            case "Grundfläche":
                house.setGroundArea(attributeValue.replaceAll("[^0-9,]", ""));
                break;
            case "Nutzfläche":
                String size = attributeValue.replaceAll("[^0-9,]", "");
                if (size.length() < 4 && (house.getSize() == null || house.getSize().isEmpty())) {
                    house.setSize(size);
                }
            default:
                if (house.getInfo() != null) {
                    house.setInfo(house.getInfo() + ", " + attributeName + ": " + attributeValue);
                } else {
                    house.setInfo(attributeName + ": " + attributeValue);
                }
        }
    }
}
