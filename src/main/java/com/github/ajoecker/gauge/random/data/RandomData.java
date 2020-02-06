package com.github.ajoecker.gauge.random.data;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.thoughtworks.gauge.Step;
import org.tinylog.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.ajoecker.gauge.random.data.DateParser.dateFromPattern;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class RandomData {
    public static final int MAX_LENGTH = UUID.randomUUID().toString().length();
    private static final Pattern FORMAT = Pattern.compile("%(\\w)\\{(\\d)\\}");
    private Faker faker;
    private VariableStorage variableStorage;

    public RandomData() {
        this(VariableStorage.get());
    }

    public RandomData(VariableStorage variableStorage) {
        this.variableStorage = variableStorage;
        this.faker = initFaker();
    }

    private Faker initFaker() {
        String localeEnv = System.getenv("gauge.data.locale");
        Locale locale = Strings.isNullOrEmpty(localeEnv) ? Locale.getDefault() : Locale.forLanguageTag(localeEnv);
        return new Faker(locale);
    }

    private String replaceFormatPattern(String input) {
        String result = input;
        Matcher matcher = FORMAT.matcher(input);
        while (matcher.find()) {
            String type = matcher.group(1);
            String frequency = matcher.group(2);
            String replacement = getReplacement(type, Integer.parseInt(frequency));
            result = result.replace("%" + type + "{" + frequency + "}", replacement);
        }
        return result;
    }

    private String getReplacement(String type, int fAsInt) {
        switch (type) {
            case "s":
                return randomAlphabetic(fAsInt).toLowerCase();
            case "S":
                return randomAlphabetic(fAsInt).toUpperCase();
            case "d":
                return randomNumeric(fAsInt);
            default:
                return "";
        }
    }

    @Step("Create a string as <variable> in format <format>")
    public void createString(String variable, String format) {
        String result = replaceFormatPattern(format);
        result = replace(result, "%s", () -> randomAlphabetic(1).toLowerCase());
        result = replace(result, "%S", () -> randomAlphabetic(1).toUpperCase());
        result = replace(result, "%d", () -> randomNumeric(1));
        variableStorage.put(variable, result);
    }

    private String replace(String base, String toReplace, Supplier<String> replacement) {
        while (base.contains(toReplace)) {
            base = base.replace(toReplace, replacement.get());
        }
        return base;
    }

    @Step("Create a string as <variable> with length <length>")
    public void createUniqueId(String variable, int length) {
        variableStorage.put(variable, uniqueId(length));
    }

    private String uniqueId(int length) {
        int correctLength = length > MAX_LENGTH ? MAX_LENGTH : length;
        String theId = UUID.randomUUID().toString().substring(0, correctLength);
        Logger.info("created id {}", theId);
        return theId;
    }

    @Step("Create <variable> from file <file>")
    public void createFromFile(String variable, String fileContent) {
        variableStorage.put(variable, fileContent.replaceAll("\\R", " "));
    }

    @Step("Create a string <variable>")
    public void createUniqueId(String variable) {
        createUniqueId(variable, 8);
    }

    @Step("Set <variable> to <value>")
    public void setVariable(String variable, Object value) {
        variableStorage.put(variable, value);
    }

    @Step("Create a lastname as <variable>")
    public void setLastName(String variable) {
        variableStorage.put(variable, faker.name().lastName());
    }

    @Step("Create a firstname as <variable>")
    public void setFirstName(String variable) {
        variableStorage.put(variable, faker.name().firstName());
    }

    @Step("Create an email as <variable>")
    public void setEmail(String variable) {
        variableStorage.put(variable, faker.internet().emailAddress());
    }

    @Step("Create a gmail address with prefix <prefix> as <variable>")
    public void setGmail(String prefix, String variable) {
        String email = String.format("%s+%s@gmail.com", prefix, uniqueId(8));
        variableStorage.put(variable, email);
    }

    @Step("Set date <variable> to today with format <format>")
    public void createDateToday(String variable, String format) {
        setDate(variable, format, LocalDate.now());
    }

    @Step("Set date <variable> to <shift> with format <format>")
    public void createDate(String variable, String shift, String format) {
        setDate(variable, format, dateFromPattern(shift));
    }

    @Step("Set date <variable> to start of month <shift> with format <format>")
    public void createDateAtStartOfMonth(String variable, String shift, String format) {
        setDate(variable, format, dateFromPattern(shift).withDayOfMonth(1));
    }

    @Step("Set date <variable> to start of this month with format <format>")
    public void createDateAtStartOfMonth(String variable, String format) {
        setDate(variable, format, LocalDate.now().withDayOfMonth(1));
    }

    @Step("Set date <variable> to start of next month with format <format>")
    public void createDateAtStartOfNextMonth(String variable, String format) {
        setDate(variable, format, LocalDate.now().plusMonths(1).withDayOfMonth(1));
    }

    public String shiftDate(String shift, String format) {
        return ofPattern(format).format(dateFromPattern(shift));
    }

    private void setDate(String variable, String format, LocalDate localDate) {
        variableStorage.put(variable, ofPattern(format).format(localDate));
    }
}
