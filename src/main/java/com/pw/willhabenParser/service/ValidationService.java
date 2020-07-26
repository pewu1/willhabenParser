package com.pw.willhabenParser.service;

import com.pw.willhabenParser.model.House;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.util.regex.Pattern;

@Service
@Singleton
public class ValidationService {

    public boolean isVerified(House house) {
        return isNotEmpty(house.getPrice()) && isNumber(house.getPrice()) && !house.getPrice().equals("1") &&
                isNotEmpty(house.getSize()) && isNumber(house.getSize()) &&
                isNotEmpty(house.getEditDate()) && isDate(house.getEditDate()) &&
                isNotEmpty(house.getEditTime()) && isTime(house.getEditTime()) &&
                isNotEmpty(house.getLink()) && isLink(house.getLink()) &&
                isNotEmpty(house.getPictureLink()) && isLink(house.getPictureLink()) &&
                isNotEmpty(house.getLocation());
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    private boolean isLink(String str) {
        return str.startsWith("http://") || str.startsWith("https://");
    }

    private boolean isNumber(String str) {
        String toValidate = str.replaceAll("[,. ]", "");
        char[] chars = toValidate.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDate(String str) {
        String[] parsedDate = str.split(Pattern.quote("."));
        return parsedDate.length == 3 &&
                isNumber(parsedDate[0]) &&
                isNumber(parsedDate[1]) &&
                isNumber(parsedDate[2]);
    }

    private boolean isTime(String str) {
        String[] parsedTime = str.split(Pattern.quote(":"));
        return parsedTime.length == 2 &&
                isNumber(parsedTime[0]) &&
                isNumber(parsedTime[1]) &&
                (Integer.parseInt(parsedTime[0]) < 24) &&
                (Integer.parseInt(parsedTime[1]) < 60);
    }
}
