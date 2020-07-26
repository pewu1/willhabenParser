package com.pw.willhabenParser.repositories;

import com.pw.willhabenParser.model.House;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface HouseRepository extends CrudRepository<House, String> {
}