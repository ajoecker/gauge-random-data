package com.github.ajoecker.gauge.random.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

public class RandomDataTest {
    private VariableStorage testContainer;
    private RandomData randomData;

    @BeforeEach
    public void clearStart() {
        testContainer = new VariableStorage() {
            private Map<String, Object> container = new HashMap<>();

            @Override
            public void put(String key, Object value) {
                container.put(key, value);
            }

            @Override
            public Optional<Object> get(String key) {
                return Optional.ofNullable(container.get(key));
            }

            @Override
            public void print() {
            }
        };
        randomData = new RandomData(testContainer);
    }

    @Test
    public void uniqueStringIsSaved() {
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

    @Test
    public void formatNumbers() {
        randomData.createString("goo", "%d%d%d");
        assertThat(parseInt(testContainer.get("goo").toString())).isBetween(100, 999);
    }

    @Test
    public void formatLowercase() {
        randomData.createString("goo", "%s%s");
        assertLowerCaseWithSize(2);
    }

    private void assertLowerCaseWithSize(int size) {
        SoftAssertions.assertSoftly(softAssertions -> {
            String goo = testContainer.get("goo").toString();
            softAssertions.assertThat(goo).matches(i -> i.chars().allMatch(n -> n >= 97 && n <= 122), "all characters must be lowercase");
            softAssertions.assertThat(goo).hasSize(size);
        });
    }

    @Test
    public void formatUppercase() {
        randomData.createString("goo", "%S%S");
        assertThat(testContainer.get("goo").toString()).matches(i -> i.chars().allMatch(n -> n >= 65 && n <= 90), "all characters must be uppercase");
    }

    @Test
    public void mixedPattern() {
        randomData.createString("goo", "%s%s{3}");
        assertLowerCaseWithSize(4);
    }
}
