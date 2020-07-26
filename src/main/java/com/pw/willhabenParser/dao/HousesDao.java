package com.pw.willhabenParser.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.pw.willhabenParser.model.House;
import com.pw.willhabenParser.repositories.HousesRepository;
import com.pw.willhabenParser.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Singleton
public class HousesDao {

    private final List<House> houseList = new ArrayList<>();
    private final Set<String> linksSet = new HashSet<>();
    private final Set<String> errorLinksSet = new HashSet<>();

    @Autowired
    ValidationService validationService;

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private HousesRepository repository;

    @PostConstruct
    private void createTable() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(House.class);

        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);
    }

    @PostConstruct
    private void loadHouses() {
        System.out.println("Loading data from AWS...");
        Iterable<House> houses = repository.findAll();
        houses.forEach(houseList::add);
        System.out.println("Loaded entries: " + houseList.size());
        linksSet.addAll(houseList.stream().filter(House::isVerified).map(House::getLink).collect(Collectors.toList()));
        linksSet.addAll(houseList.stream().filter(House::isVerified).map(House::getPictureLink).collect(Collectors.toList()));
        errorLinksSet.addAll(houseList.stream().filter(house -> !house.isVerified()).map(House::getLink).filter(Objects::nonNull).collect(Collectors.toList()));
        errorLinksSet.addAll(houseList.stream().filter(house -> !house.isVerified()).map(House::getPictureLink).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public List<House> getAllHouses() {
        if (houseList.isEmpty()) {
            loadHouses();
        }
        return sortEditDate(houseList);
    }

    public Set<String> getVerifiedLinks() {
        return linksSet;
    }

    public Set<String> getErrorLinks() {
        return errorLinksSet;
    }

    public List<House> getHousesBelowAveragePrice() {
        Double averageGlobalPrice = getAllHouses().stream()
                .filter(house -> house.getAvgMeterPrice() != null)
                .collect(Collectors.averagingDouble(House::getAvgMeterPrice));

        System.out.println("Average price: " + averageGlobalPrice);

        return sortAvgPrice(getAllHouses()).stream()
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
                .sorted((house1, house2) -> house2.getEditLocalDateTime().compareTo(house1.getEditLocalDateTime()))
                .collect(Collectors.toList());
    }

    public void persist(House house) {
        if (validationService.isVerified(house)) {
            System.out.println("Persisting house as verified");
            house.setVerified(true);
            getAllHouses().add(house);
        } else {
            System.out.println("Persisting house as error");
            house.setVerified(false);
        }
        repository.save(house);
    }
}
