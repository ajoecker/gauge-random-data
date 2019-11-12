package com.github.ajoecker.random.data;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.thoughtworks.gauge.Step;

import java.util.Locale;
import java.util.UUID;

import static com.thoughtworks.gauge.datastore.DataStoreFactory.getScenarioDataStore;

public class RandomData {
    private Faker faker;

    public RandomData() {
        String localeEnv = System.getenv("gauge.data.locale");
        Locale locale = Strings.isNullOrEmpty(localeEnv) ? Locale.getDefault() : Locale.forLanguageTag(localeEnv);
        faker = new Faker(locale);
    }

    @Step("Create a string as <variable> with length <length>")
    public void createUniqueId(String variable, int length) {
        String uuid = UUID.randomUUID().toString();
        length = length > uuid.length() ? uuid.length() : length;
        getScenarioDataStore().put(variable, uuid.substring(0, length));
    }

    @Step("Create a string <variable>")
    public void createUniqueId(String variable) {
        createUniqueId(variable, 8);
    }

    @Step("Set <variable> to <value>")
    public void setVariable(String variable, Object value) {
        getScenarioDataStore().put(variable, value);
    }

    @Step("Create a lastname as <variable>")
    public void setLastName(String variable) {
        getScenarioDataStore().put(variable, faker.name().lastName());
    }

    @Step("Create a firstname as <variable>")
    public void setFirstName(String variable) {
        getScenarioDataStore().put(variable, faker.name().firstName());
    }

    @Step("Create an email as <variable>")
    public void setEmail(String variable) {
        getScenarioDataStore().put(variable, faker.internet().emailAddress());
    }
}
