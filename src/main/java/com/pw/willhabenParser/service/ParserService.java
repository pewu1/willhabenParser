package com.pw.willhabenParser.service;

import com.pw.willhabenParser.dao.HousesDao;
import com.pw.willhabenParser.model.House;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ParserService {

    private final String MAIN_URL = "https://www.willhaben.at";
    public static final String DECIMAL_FORMAT = "###,###.##";

    @Autowired
    HousesDao dao;


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
        int linksNumber = dao.getVerifiedLinks().size();
        dao.getVerifiedLinks().add(house.getLink());
        dao.getVerifiedLinks().add(house.getPictureLink());
        if (linksNumber == dao.getVerifiedLinks().size() - 2) {
            parseDetails(house);
        } else {
            throw new IllegalArgumentException("House already exists");
        }
        return house;
    }

    private void parseDetails(House house) throws IOException {
        if (house == null || house.getLink() == null) {
            throw new IllegalArgumentException("House link is null");
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
        String location = detailsSection
                .getElementsByAttributeValue("data-testid", "object-location-address")
                .first()
                .html();
        house.setLocation(location.replaceAll("&amp;", "&"));

        Elements attributeGroup = detailsSection
                .getElementsByAttributeValue("data-testid", "attribute-group")
                .first()
                .getElementsByAttributeValue("data-testid", "attribute");

        attributeGroup.forEach(element -> processAttributeElement(element, house));

        String price = doc.getElementsByAttributeValue("data-testid", "contact-box-price-box-price-value").html().replaceAll("[^0-9,]", "").replaceAll(" ", "");
        house.setPrice(price);

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

        String editDate = doc.getElementsByAttributeValue("data-testid", "ad-detail-ad-edit-date").html().split(",")[0];
        house.setEditDate(editDate);
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
            default:
                if (house.getInfo() != null) {
                    house.setInfo(house.getInfo() + ", " + attributeName + ": " + attributeValue);
                } else {
                    house.setInfo(attributeName + ": " + attributeValue);
                }
        }
    }


}
