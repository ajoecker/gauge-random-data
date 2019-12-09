package com.github.ajoecker.gauge.random.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        assertThat(getElement("foobar")).hasSize(5);
    }

    private String getElement(String foobar) {
        return testContainer.get(foobar).get().toString();
    }

    @Test
    public void uniqueStringWithExtensiveLengthIsSavedCorrectly() {
        randomData.createUniqueId("foobar", 50);
        assertThat(getElement("foobar")).hasSize(RandomData.MAX_LENGTH);
    }

    @Test
    public void setValue() {
        randomData.setVariable("foobar", 3);
        assertThat(testContainer.get("foobar").get()).isEqualTo(3);
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
        assertThat(parseInt(getElement("goo"))).isBetween(100, 999);
    }

    @Test
    public void formatLowercase() {
        randomData.createString("goo", "%s%s");
        assertLowerCaseWithSize(2);
    }

    private void assertLowerCaseWithSize(int size) {
        SoftAssertions.assertSoftly(softAssertions -> {
            String goo = getElement("goo");
            softAssertions.assertThat(goo).matches(i -> i.chars().allMatch(n -> n >= 97 && n <= 122), "all characters must be lowercase");
            softAssertions.assertThat(goo).hasSize(size);
        });
    }

    @Test
    public void formatUppercase() {
        randomData.createString("goo", "%S%S");
        assertThat(getElement("goo")).matches(i -> i.chars().allMatch(n -> n >= 65 && n <= 90), "all characters must be uppercase");
    }

    @Test
    public void mixedPattern() {
        randomData.createString("goo", "%s%s{3}");
        assertLowerCaseWithSize(4);
    }

    @Test
    public void today() {
        randomData.createDateToday("foo", "YYYY-MM-dd");
        assertThat(testContainer.get("foo")).contains(LocalDate.now().toString());
    }

    @Test
    public void today2() {
        randomData.createDate("foo", "0M", "YYYY-MM-dd");
        assertThat(testContainer.get("foo")).contains(LocalDate.now().toString());
    }

    @Test
    public void inAMonth() {
        randomData.createDate("foo", "1m", "YYYY-MM-dd");
        assertThat(testContainer.get("foo")).contains(LocalDate.now().plusMonths(1).toString());
    }

    @Test
    public void lastWeek() {
        randomData.createDate("foo", "-1w", "YYYY-MM-dd");
        assertThat(testContainer.get("foo")).contains(LocalDate.now().minusWeeks(1).toString());
    }

    @Test
    public void nextYearStartOfMonth() {
        randomData.createDateAtStartOfMonth("foo", "1Y", "YYYY-MM-dd");
        assertThat(testContainer.get("foo")).contains(LocalDate.now().plusYears(1).withDayOfMonth(1).toString());
    }
}
