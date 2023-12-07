package com.chopvitebackend.chopvite.util;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

    private static final Random randomGenerator = new Random();
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#,###.00");
    private static final DecimalFormat volumeFormatter = new DecimalFormat("#,###");

    public static boolean isBlank(final String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }

    public static boolean isAnyBlank(final String... strings) {
        return Arrays.stream(strings)
                .map(StringUtil::isBlank)
                .reduce(false, (total, current) -> total || current);
    }

    public static boolean isNumeric(final String string){
        if (isBlank(string))
            return false;

        return string.matches("\\d+");
    }

    public static String stripPunct(final String s) {
        if (isBlank(s))
            return null;

        return s.replaceAll("\\W", "");
    }

    public static boolean hasOnlyLetters(final String str) {
        return !isBlank(str) && !str.matches(".*\\d.*");
    }

    public static String cleanSpaceAndSymbols(String s) {
        s = s.replaceAll("\\s+","");
        s = s.replaceAll("-", "").replaceAll(",", "").replaceAll("\\+", "");
        return s;
    }

    public static boolean equalsAllowNull(final String str1, final String str2) {
        if (str1 == null && str2 == null)
            return true;

        if ((str1 != null && str2 == null) || str1 == null)
            return false;

        return str1.equals(str2);
    }

    public static String getRandomInList(final List<String> list) {
        final int index = randomGenerator.nextInt(list.size());
        return list.get(index);
    }


    public static Optional<String> stripNonDigit(final String str) {
        if (isBlank(str))
            return Optional.empty();

        final String res = str.replaceAll("[^\\d.]", "");
        if (isBlank(res))
            return Optional.empty();

        return Optional.of(res);
    }

    public static String parse0906(final String str) {
        if (isBlank(str) || !str.contains("0906"))
            return null;

        final int index = str.indexOf("0906");
        if (index + 11 > str.length())
            return null;

        return str.substring(index, index + 11);
    }

    public static String getNDigitTextFromString(final int n, final String text) {
        final Pattern p = Pattern.compile( "(\\d{" + n + "})" );
        final Matcher m = p.matcher(text);
        if ( m.find() ) {
            return m.group( 0 );
        }
        return null;
    }

    public static List<String> ngrams(final int n, final String str) {
        final List<String> ngrams = new ArrayList<String>();
        final String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    private static String concat(final String[] words, final int start, final int end) {
        final StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }

    public static String parseAndAddInt(String... nums){
        int result = 0;
        for (String n: nums){
            if(isNumeric(n)){
                result += Integer.parseInt(n);
            }
        }
        return formatInt(result);
    }

    public static int getRandomInRange(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String trim(final String s) {
        if (s == null) {
            return null;
        }

        return s.trim();
    }

    public static String formatDouble(double value) {
        if (value == 0)
            return "0";

        return decimalFormatter.format(value);
    }

    public static String formatInt(int value) {
        if (value == 0)
            return "0";

        return volumeFormatter.format(value);
    }

    public static double format2dp(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    public static String sanitizePhoneNumber(String phoneNumber) {
        return null;
    }

}

