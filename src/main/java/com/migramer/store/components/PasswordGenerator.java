package com.migramer.store.components;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    private final char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final char[] uppercase = "ABCDEFGJKLMNPRSTUVWXYZ".toCharArray();
    private final char[] numbers = "0123456789".toCharArray();
    private final char[] symbols = "^$?!@#%&".toCharArray();
    private final char[] allAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789^$?!@#%&".toCharArray();
    private final Integer length = 8;

    public String generatePassword() {

        Random random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length - 4; i++) {
            password.append(allAllowed[random.nextInt(allAllowed.length)]);
        }

        password.insert(random.nextInt(password.length()), lowercase[random.nextInt(lowercase.length)]);
        password.insert(random.nextInt(password.length()), uppercase[random.nextInt(uppercase.length)]);
        password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
        password.insert(random.nextInt(password.length()), symbols[random.nextInt(symbols.length)]);
        return password.toString();
    }

}