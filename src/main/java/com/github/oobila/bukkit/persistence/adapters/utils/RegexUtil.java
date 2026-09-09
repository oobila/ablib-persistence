package com.github.oobila.bukkit.persistence.adapters.utils;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small regex helper used by {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle}
 * to turn a path template's literal segments into a matchable regex while leaving its
 * {@code {uuid}}/{@code {key}} placeholders untouched for separate substitution.
 */
public class RegexUtil {

    /** Matches each run of literal text between/around {@code {placeholder}} segments, e.g. splits {@code "data/{uuid}/{key}.yml"} into {@code "data/"} and {@code ".yml"}. */
    public static final String ANYTHING_NOT_IN_BRACES = "(?:(?<=^)|(?<=\\}))([^{}]+)(?=\\{|$)";

    /** Applies {@code function} to each substring of {@code input} matching {@code regex}, leaving the rest of the string untouched. */
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
