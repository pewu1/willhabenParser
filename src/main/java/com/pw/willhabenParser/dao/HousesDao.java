package com.pw.willhabenParser.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pw.willhabenParser.model.House;
import com.pw.willhabenParser.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Singleton
public class HousesDao {

    private File errorDir;
    private final List<House> verifiedHouseList = new ArrayList<>();
    private final List<House> errorsList = new ArrayList<>();
    private final Set<String> linksSet = new HashSet<>();
    private final Set<String> errorLinksSet = new HashSet<>();
    private File jsonDir;
    private final String JSON_DIR = getDir("json");
    private final String ERROR_DIR = getDir("errors");

    @Autowired
    ValidationService validationService;

    @Autowired
    ObjectMapper objectMapper;

    private String getDir(String folder) {
        String path = File.listRoots()[0] + File.separator + "parserData" + File.separator + folder + File.separator;
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Unable to create directory: " + path);
        }
        return path;
    }

    private void loadHouses() throws IOException {
        if (getJsonFiles().length > 0) {
            for (File file : getJsonFiles()) {
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
        if (getErrorFiles() != null && getErrorFiles().length > 0) {
            for (File file : getErrorFiles()) {
                House house = objectMapper.readValue(file, House.class);
                if (validationService.isVerified(house)) {
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
    }

    public List<House> getAllHouses() {
        if (verifiedHouseList.isEmpty()) {
            try {
                loadHouses();
            } catch (IOException e) {
                System.out.println("Error while loading persisted houses");
            }
        }
        List<House> completeList = new ArrayList<>();
        completeList.addAll(verifiedHouseList);
        completeList.addAll(errorsList);
        return completeList;
    }

    public List<House> getVerifiedHouses() {
        if (verifiedHouseList.isEmpty()) {
            try {
                loadHouses();
            } catch (IOException e) {
                System.out.println("Error while loading persisted houses");
            }
        }
        return sortEditDate(verifiedHouseList);
    }

    public Set<String> getVerifiedLinks() {
        return linksSet;
    }

    public Set<String> getErrorLinks() {
        return errorLinksSet;
    }

    public boolean isNotPersisted(House house) {
        return isNotPersistedAsVerified(house) && isNotPersistedAsError(house);
    }

    public boolean isNotPersistedAsError(House house) {
        return getErrorFiles() != null && Stream.of(getErrorFiles())
                .noneMatch(file -> compareHashcodes(house, file));
    }

    public boolean isNotPersistedAsVerified(House house) {
        return getJsonFiles() != null && Stream.of(getJsonFiles())
                .noneMatch(file -> compareHashcodes(house, file));
    }

    private boolean compareHashcodes(House house, File file) {
        return Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf("."))) == house.hashCode();
    }

    private File[] getJsonFiles() {
        return new File(JSON_DIR).listFiles();
    }

    private File[] getErrorFiles() {
        return new File(ERROR_DIR).listFiles();
    }

    public List<House> getHousesBelowAveragePrice() {
        List<House> houseList = getAllHouses();
        Double averageGlobalPrice = houseList.stream()
                .filter(house -> house.getAvgMeterPrice() != null)
                .collect(Collectors.averagingDouble(House::getAvgMeterPrice));

        System.out.println("Average price: " + averageGlobalPrice);

        return sortAvgPrice(houseList).stream()
                .filter(house -> house.getAvgMeterPrice() < averageGlobalPrice)
                .collect(Collectors.toList());
    }

    private List<House> sortAvgPrice(List<House> houseList) {
        return houseList.stream()
                .filter(house -> house.getAvgMeterPrice() != null)
                .filter(house -> !house.getPrice().equals("1"))
                .sorted(Comparator.comparing(House::getAvgMeterPrice))
                .collect(Collectors.toList());
    }

    private List<House> sortEditDate(List<House> houseList) {
        return houseList.stream()
                .sorted((house1, house2) -> house2.getEditDateAsLocalDate().compareTo(house1.getEditDateAsLocalDate()))
                .collect(Collectors.toList());
    }

    public void persist(House house) {
        try {
            if (isNotPersistedAsVerified(house) && validationService.isVerified(house)) {
                System.out.println("Persisting house");
                getVerifiedHouses().add(house);
                objectMapper.writeValue(new File(JSON_DIR + house.hashCode() + ".json"), house);
            } else if (isNotPersistedAsError(house)) {
                errorsList.add(house);
                objectMapper.writeValue(new File(ERROR_DIR + house.hashCode() + ".json"), house);
            }
        } catch (IOException e) {
            System.out.println("Error while persisting house");
        }

    }
}
