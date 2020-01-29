package com.setpace.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;

import java.io.IOException;
import java.util.Arrays;

public class LogHelper {

    static String prettyJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Object json = null;
        try {
            json = mapper.readValue(jsonString, Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = null;
        try {
            s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    static String indent(String string) {
        return string.replaceAll("(?m)^",
                "\t\t\t\t\t\t\t\t\t");
    }

    public static String headersToString(Header[] headers) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(headers).forEach((x) -> {
            builder.append(System.lineSeparator() + " Name: " + x.getName() + " Value: " + x.getValue());
        });
        return builder.toString();
    }
}