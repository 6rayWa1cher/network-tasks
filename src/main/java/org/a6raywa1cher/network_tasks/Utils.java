package org.a6raywa1cher.network_tasks;

import com.github.javafaker.Faker;

import java.net.URL;

public final class Utils {
    private static final Faker faker = new Faker();

    public static String urlToResource(URL url) {
        String output = !url.getPath().equals("") ? url.getPath() : "/";
        if (url.getQuery() != null && !url.getQuery().equals("")) {
            output += '?' + url.getQuery();
        }
        return output;
    }

    public static String generateRandomString() {
        return faker.name().firstName();
    }
}
