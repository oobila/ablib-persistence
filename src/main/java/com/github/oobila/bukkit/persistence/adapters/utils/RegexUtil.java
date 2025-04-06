package com.github.oobila.bukkit.persistence.adapters.utils;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static final String ANYTHING_NOT_IN_BRACES = "(?:(?<=^)|(?<=\\}))([^{}]+)(?=\\{|$)";

    public static String performWithRegexMatch(String input, String regex, UnaryOperator<String> function) {
        Matcher m = Pattern.compile(regex).matcher(input);
        StringBuilder b = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(b, function.apply(m.group()).replace("\\", "\\\\"));
        }
        m.appendTail(b);
        return b.toString();
    }

    @SuppressWarnings("java:S106")
    public static void main(String[] args){
        String testString = "data/{uuid}/{key}.yml";
        String returnVal = performWithRegexMatch(testString, ANYTHING_NOT_IN_BRACES, Pattern::quote);
        System.out.println(returnVal);
    }

}
