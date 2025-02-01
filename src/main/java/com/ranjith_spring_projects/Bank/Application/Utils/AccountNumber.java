package com.ranjith_spring_projects.Bank.Application.Utils;

import java.time.Year;

public class AccountNumber {

    public static final String ACCOUNT_CODE = "001";
    public static final String ACCOUNT_MESSAGE = "This user Already have account created";

    public static final String ACCOUNT_CODE_CREATION = "002";
    public static final String ACCOUNT_MESSAGE_SUCCESS = "The account has been created Successfully";

    public static String createAccountNumber(){
        Year  year = Year.now();

        int min = 100000;

        int max = 999999;

        int randNumber = (int)Math.floor(Math.random() * (max - min +1) + min);

        String yearof = String.valueOf(year);

        String randomNumber = String.valueOf(randNumber);

        StringBuilder builder = new StringBuilder();

        builder.append(yearof).append(randomNumber);

        return builder.toString();
    }
}
