package com.github.ajoecker.gauge.random.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomDataTest {
    private Map<String, Object> testContainer;
    private RandomData randomData;

    @BeforeEach
    public void clearStart() {
        testContainer = new HashMap<>();
        randomData = new RandomData(((key, value) -> testContainer.put(key, value)));
    }

    @Test
    public void UniqueStringIsSaved() {
        randomData.createUniqueId("foobar");
        assertThat(testContainer.get("foobar")).isNotNull();
    }

    @Test
    public void uniqueStringWithLengthIsSavedCorrectly() {
        randomData.createUniqueId("foobar", 5);
        assertThat(testContainer.get("foobar").toString()).hasSize(5);
    }

    @Test
    public void uniqueStringWithExtensiveLengthIsSavedCorrectly() {
        randomData.createUniqueId("foobar", 50);
        assertThat(testContainer.get("foobar").toString()).hasSize(RandomData.MAX_LENGTH);
    }

    @Test
    public void setValue() {
        randomData.setVariable("foobar", 3);
        assertThat(testContainer.get("foobar")).isEqualTo(3);
    }

    @Test
    public void setFirstname() {
        randomData.setFirstName("firstName");
        assertThat(testContainer.get("firstName")).isNotNull();
    }

    @Test
    public void setLastname() {
        randomData.setLastName("lastName");
        assertThat(testContainer.get("lastName")).isNotNull();
    }

    @Test
    public void setEmail() {
        randomData.setEmail("email");
        assertThat(testContainer.get("email")).isNotNull();
    }
}
