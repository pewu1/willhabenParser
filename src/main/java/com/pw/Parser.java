package com.pw;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pw.model.House;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private static final int AREA_CODE = 4;
    private static final String CSV_COLS = "editDate;postalCode;location;district;state;price;size;avgPrice;groundArea;rooms;objectType;age;condition;heatingType;info;transactionFee;link;pictureLink;hashCode\r\n";
    private static final String MAIN_URL = "https://www.willhaben.at";
    private static final String BUY_HOUSE_URL = MAIN_URL + "/iad/immobilien/haus-kaufen/haus-angebote?rows=100&areaId=" + AREA_CODE;
    private static final String DATA_DIR = "D:/parserData/";
    private static final String JSON_DIR = DATA_DIR + "json" + File.separator + AREA_CODE + File.separator;
    private static final String ERROR_DIR = DATA_DIR + "errors" + File.separator;
    public static final String DECIMAL_FORMAT = "###,###.##";


    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect(BUY_HOUSE_URL).get();
        Element resultList = doc.getElementById("resultlist");
        if (resultList == null) {
            System.out.println("Error, no result list");
        }
        int counter = 2;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<House> verifiedHouseList = new ArrayList<>();
        List<House> errorsList = new ArrayList<>();
        Set<String> linksSet = new HashSet<>();
        Set<String> errorLinksSet = new HashSet<>();
        File[] listOfFiles = setupDataSource();
        if (listOfFiles.length > 0) {
            for (File file : listOfFiles) {
                House house = objectMapper.readValue(file, House.class);
                if (!file.getName().contains(String.valueOf(house.hashCode()))) {
                    System.out.println("Updating file");
                    objectMapper.writeValue(new File(JSON_DIR + house.hashCode() + ".json"), house);
                    Files.delete(file.toPath());
                }
                verifiedHouseList.add(house);
                linksSet.add(house.getLink());
                linksSet.add(house.getPictureLink());
            }
        }
        File errorFolder = new File(ERROR_DIR);
        File[] listOfErrorFiles = errorFolder.listFiles();
        if (listOfErrorFiles != null && listOfErrorFiles.length > 0) {
            for (File file : listOfErrorFiles) {
                House house = objectMapper.readValue(file, House.class);
                if (isVerified(house)) {
                    System.out.println("Found correct house in error files");
                    if (!verifiedHouseList.contains(house)) {
                        verifiedHouseList.add(house);
                        objectMapper.writeValue(new File(JSON_DIR + house.hashCode() + ".json"), house);
                    }
                    Files.delete(file.toPath());
                } else {
                    if (!file.getName().contains(String.valueOf(house.hashCode()))) {
                        objectMapper.writeValue(new File(ERROR_DIR + house.hashCode() + ".json"), house);
                        Files.delete(file.toPath());
                    }
                    errorsList.add(house);
                    if (house.getLink() != null && !house.getLink().isEmpty()) {
                        errorLinksSet.add(house.getLink());
                    }
                    if (house.getPictureLink() != null && !house.getPictureLink().isEmpty()) {
                        errorLinksSet.add(house.getPictureLink());
                    }
                }
            }
        }
        try {
            while (resultList != null) {

                Elements entries = resultList.getElementsByClass("search-result-entry").stream()
                        .filter(entry -> entry.hasAttr("itemtype"))
                        .collect(Collectors.toCollection(Elements::new));

                for (int i = 0; i < entries.size(); i++) {
                    System.out.println("Processing " + (i + 1) + "/" + entries.size() + " page: " + (counter - 1));
                    try {
                        House house = parseEntry(entries.get(i), linksSet, errorLinksSet);

                        if (isHouseNotPersisted(listOfFiles, house) && isVerified(house)) {
                            System.out.println("Persisting house");
                            verifiedHouseList.add(house);
                            objectMapper.writeValue(new File(JSON_DIR + house.hashCode() + ".json"), house);
                        } else {
                            errorsList.add(house);
                            objectMapper.writeValue(new File(ERROR_DIR + house.hashCode() + ".json"), house);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                }

                doc = Jsoup.connect(BUY_HOUSE_URL + "&page=" + counter).get();
                resultList = doc.getElementById("resultlist");
                counter++;
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        List<House> completeList = new ArrayList<>();
        completeList.addAll(verifiedHouseList);
        completeList.addAll(errorsList);

        writeToHtmlFile(getHTML(sortAvgPrice(verifiedHouseList)), "avgPrice.html");
        writeToHtmlFile(getHTML(getHousesBelowAveragePrice(verifiedHouseList)), "belowAvg.html");
        writeToHtmlFile(getHTML(sortEditDate(completeList)), "newest.html");
        writeToHtmlFile(getHTML(errorsList), "errors.html");


        writeToCsvFile(sortAvgPrice(verifiedHouseList), "avgPrice.csv");
        writeToCsvFile(errorsList, "errors.csv");
        writeToCsvFile(getHousesBelowAveragePrice(verifiedHouseList), "belowAvg.csv");
        writeToCsvFile(sortEditDate(completeList), "newest.csv");
    }

    private static List<House> getHousesBelowAveragePrice(List<House> houseList)  {
        Double averageGlobalPrice = houseList.stream()
                .filter(house -> house.getAvgMeterPrice() != null)
                .collect(Collectors.averagingDouble(House::getAvgMeterPrice));

        System.out.println("Average price: " + averageGlobalPrice);

        return sortAvgPrice(houseList).stream()
                .filter(house -> house.getAvgMeterPrice() < averageGlobalPrice)
                .collect(Collectors.toList());
    }

    private static List<House> sortAvgPrice(List<House> houseList) {
        return houseList.stream()
                .filter(house -> house.getAvgMeterPrice() != null)
                .sorted(Comparator.comparing(House::getAvgMeterPrice))
                .collect(Collectors.toList());
    }

    private static List<House> sortEditDate(List<House> houseList) {
        return houseList.stream()
                .sorted((house1, house2) -> house2.getEditDateAsLocalDate().compareTo(house1.getEditDateAsLocalDate()))
                .collect(Collectors.toList());
    }

    private static File[] setupDataSource() throws IOException {
        Files.createDirectories(Paths.get(ERROR_DIR));
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.createDirectories(Paths.get(JSON_DIR));
        File folder = new File(JSON_DIR);
        return folder.listFiles();
    }

    private static boolean isHouseNotPersisted(File[] listOfFiles, House house) {
        return listOfFiles != null && Stream.of(listOfFiles)
                .noneMatch(file -> compareHashcodes(house, file));
    }

    private static boolean compareHashcodes(House house, File file) {
        return Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf("."))) == house.hashCode();
    }

    private static void writeToCsvFile(List<House> houseList, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(DATA_DIR + filename, false);

            DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
            outStream.writeChars(getStringFromList(houseList));
            outStream.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void writeToHtmlFile(String content, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(DATA_DIR + filename, false);

            DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
            outStream.writeChars(content);
            outStream.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static String getStringFromList(List<House> houseList) {
        StringBuilder sb = new StringBuilder(CSV_COLS);
        houseList.forEach(house -> sb.append(house.toString().replace(";null;", ";;")));
        return sb.toString();
    }

    private static House parseEntry(Element entry, Set<String> linksSet, Set<String> errorLinksSet) throws IllegalArgumentException, IOException {
        House house = new House();
        Element imageSection = entry.getElementsByClass("image-section").first();
        house.setLink(MAIN_URL + imageSection.selectFirst("a").attributes().get("href"));
        if (house.getLink().contains("andere-laender")) {
            throw new IllegalArgumentException("House is abroad");
        }
        house.setPictureLink(imageSection.selectFirst("img").attributes().get("src"));
        if (errorLinksSet.contains(house.getLink()) && errorLinksSet.contains(house.getPictureLink())) {
            throw new IllegalArgumentException("House is already marked as error");
        }
        int linksNumber = linksSet.size();
        linksSet.add(house.getLink());
        linksSet.add(house.getPictureLink());
        if (linksNumber == linksSet.size() - 2) {
            parseDetails(house);
        } else {
            throw new IllegalArgumentException("House already exists");
        }
        return house;
    }

    private static void parseDetails(House house) throws IOException {
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

        String price = doc.getElementsByAttributeValue("data-testid", "contact-box-price-box-price-value").html().replaceAll("[^0-9,]", "").replaceAll(" ","");
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

    private static void processAttributeElement(Element element, House house) {
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

    private static boolean isVerified(House house) {
        return isNotEmpty(house.getPrice()) && isNumber(house.getPrice()) && !house.getPrice().equals("1") &&
                isNotEmpty(house.getSize()) && isNumber(house.getSize()) &&
                isNotEmpty(house.getEditDate()) && isDate(house.getEditDate()) &&
                isNotEmpty(house.getLink()) && isLink(house.getLink()) &&
                isNotEmpty(house.getPictureLink()) && isLink(house.getPictureLink()) &&
                isNotEmpty(house.getLocation());
    }

    private static boolean isNotEmpty(String str) {
        boolean result = str != null && !str.isEmpty();
        if (!result) {
            System.out.println("Empty string");
        }
        return result;
    }

    private static boolean isLink(String str) {
        boolean result = str.startsWith("http://") || str.startsWith("https://");
        if (!result) {
            System.out.println("Not a link: " + str);
        }
        return result;
    }

    private static boolean isNumber(String str) {
        String toValidate = str.replaceAll("[,. ]", "");
        char[] chars = toValidate.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                System.out.println("Not a number: " + str);
                return false;
            }
        }
        return true;
    }

    private static boolean isDate(String str) {
        String[] parsedDate = str.split(Pattern.quote("."));
        boolean result = parsedDate.length == 3 &&
                isNumber(parsedDate[0]) &&
                isNumber(parsedDate[1]) &&
                isNumber(parsedDate[2]);
        if (!result) {
            System.out.println("Not a date: " + str);
        }
        return result;
    }

    private static String getHTML(List<House> houseList) {
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
                .map(Parser::getHTMLforHouse)
                .forEach(stringBuilder::append);

        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    private static String getHTMLforHouse(House house) {
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
