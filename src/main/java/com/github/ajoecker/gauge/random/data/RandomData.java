package com.github.ajoecker.gauge.random.data;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.thoughtworks.gauge.Step;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class RandomData {
    public static final int MAX_LENGTH = UUID.randomUUID().toString().length();
    private static final Pattern FORMAT = Pattern.compile("%(\\w)\\{(\\d)\\}");
    private Faker faker;
    private VariableStorage variableStorage;


    public RandomData() {
        this(VariableStorage.create());
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
            int fAsInt = Integer.parseInt(frequency);
            String replacement = getReplacement(type, fAsInt);
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
        int correctLength = length > MAX_LENGTH ? MAX_LENGTH : length;
        variableStorage.put(variable, UUID.randomUUID().toString().substring(0, correctLength));
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
}
