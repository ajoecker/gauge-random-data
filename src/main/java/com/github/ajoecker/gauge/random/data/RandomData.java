package com.github.ajoecker.gauge.random.data;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.thoughtworks.gauge.Step;

import java.util.Locale;
import java.util.UUID;

import static com.thoughtworks.gauge.datastore.DataStoreFactory.*;

public class RandomData {
    public static final int MAX_LENGTH = UUID.randomUUID().toString().length();
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

    @Step("Create a string as <variable> with length <length>")
    public void createUniqueId(String variable, int length) {
        int correctLength = length > MAX_LENGTH ? MAX_LENGTH : length;
        variableStorage.put(variable, UUID.randomUUID().toString().substring(0, correctLength));
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
