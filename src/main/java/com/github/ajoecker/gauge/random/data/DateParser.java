package com.github.ajoecker.gauge.random.data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

class DateParser {
    static LocalDate dateFromPattern(String startPattern) {
        int duration = Integer.parseInt(startPattern.substring(0, startPattern.length() - 1).trim());
        var chronoUnit = fromOneLetter(startPattern.charAt(startPattern.length() - 1));
        return chronoUnit.map(cU -> LocalDate.now().plus(duration, cU)).orElseThrow();
    }

    private static Optional<ChronoUnit> fromOneLetter(char letter) {
        switch (letter) {
            case 'd':
            case 'D':
                return Optional.of(ChronoUnit.DAYS);
            case 'w':
            case 'W':
                return Optional.of(ChronoUnit.WEEKS);
            case 'm':
            case 'M':
                return Optional.of(ChronoUnit.MONTHS);
            case 'y':
            case 'Y':
                return Optional.of(ChronoUnit.YEARS);
            default:
                return Optional.empty();
        }
    }
}
